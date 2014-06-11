/**
 * @file   StructuralAnalysis.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 21:42:42 2014
 * 
 * @brief  Main routines for structural analysis.
 * 
 * 
 */

//
package io.github.egonw;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.SortedMap;
import java.util.TreeMap;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import com.google.common.base.Joiner;
import java.util.stream.Collectors;
import org.openscience.cdk.ringsearch.RingSearch;
import java.util.function.Function;
import java.util.List;
import com.google.common.collect.Lists;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.interfaces.IRingSet;
import java.util.ArrayList;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.ringsearch.SSSRFinder;

/**
 *
 */

public class StructuralAnalysis {
    
    private int atomSetCount = 0;
    private final Logger logger;
    private final IAtomContainer molecule;
    // TODO(sorge) Refactor this into a separate flags module.
    private final Cli cli;

    private static SortedMap<String, RichStructure> richStructures = 
        new TreeMap(new CMLNameComparator());


    public StructuralAnalysis(IAtomContainer molecule, Cli cli, Logger logger) {
        this.cli = cli;
        this.logger = logger;
        this.molecule = molecule;
        initStructure();
        ringSearch();
        aliphaticChains();
        // TODO(sorge): functionalGroups();
    }

    private void initStructure() {
        for (IAtom atom : this.molecule.atoms()) {
            this.richStructures.put(atom.getID(), new RichAtom(atom));
        }
        for (IBond bond : this.molecule.bonds()) {
            this.richStructures.put(bond.getID(), new RichBond(bond));
            for (IAtom atom : bond.atoms()) {
                (this.richStructures.get(atom.getID()))
                .getContexts().add(bond.getID());
            }
        }
    }


    // TODO (sorge): Outsource that into a separate module similar to aliphatic
    //               chain.
    private void ringSearch() {
        RingSearch ringSearch = new RingSearch(this.molecule);
        
        if (this.cli.cl.hasOption("s")) {
            getFusedRings(ringSearch);
        } else {
            getFusedRings(ringSearch, this.cli.cl.hasOption("sssr") ?
                          this::sssrSubRings : this::smallestSubRings);
        }
        getIsolatedRings(ringSearch);
    }


    /** 
     * Computes Isolated rings.
     * 
     * @param ringSearch The current ringsearch.
     */
    private void getIsolatedRings(RingSearch ringSearch) {
        List<IAtomContainer> ringSystems = ringSearch.isolatedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            String id = getAtomSetId();
            this.richStructures.put(id, new RichAtomSet(ring, RichAtomSet.Type.ISOLATED, id));
        }
    }


    /**
     * Computes fused rings without subsystems.
     * 
     * @param ringSearch The current ringsearch.
     */    
    private void getFusedRings(RingSearch ringSearch) {
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            String id = getAtomSetId();
            this.richStructures.put(id, new RichAtomSet(ring, RichAtomSet.Type.FUSED, id));
        }
    }


    /** 
     * Computes fused rings and their subsystems.
     * 
     * @param ringSearch 
     * @param subRingMethod Method to compute subrings.
     */    
    private void getFusedRings(RingSearch ringSearch, Function<IAtomContainer,
                               List<IAtomContainer>> subRingMethod) {
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
        for (IAtomContainer system : ringSystems) {
            String ringId = getAtomSetId();
            RichAtomSet ring = new RichAtomSet(system, RichAtomSet.Type.FUSED, ringId);
            this.richStructures.put(ringId, ring);
            List<IAtomContainer> subSystems = subRingMethod.apply(system);
            for (IAtomContainer subSystem : subSystems) {
                String subRingId = getAtomSetId();
                RichAtomSet subRing = new RichAtomSet(subSystem, RichAtomSet.Type.SMALLEST, subRingId);
                this.richStructures.put(subRingId, subRing);
                subRing.getContexts().add(ringId);
                ring.getComponents().add(subRingId);
            }
        }
    }


    /** 
     * Predicate that tests if a particular ring has no other ring as proper subset.
     * 
     * @param ring The ring to be tested.
     * @param restRings The other rings (possibly including the first ring).
     * 
     * @return True if ring has smallest coverage.
     */
    // This is quadratic and should be done better!
    // All the iterator to list operations should be done exactly once!
    private static boolean isSmallest(IAtomContainer ring, 
                                      List<IAtomContainer> restRings) {
        List<IAtom> ringAtoms = Lists.newArrayList(ring.atoms());
        for (IAtomContainer restRing : restRings) {
            if (ring == restRing) {
                continue;
            }
            List<IAtom> restRingAtoms = Lists.newArrayList(restRing.atoms());
            if (ringAtoms.containsAll(restRingAtoms)) {
                return false;
            };
        }
        return true;
    };


    /** 
     * Method to compute smallest rings via subset coverage.
     * 
     * @param ring Fused ring to be broken up.
     * 
     * @return Subrings as atom containers.
     */    
    private List<IAtomContainer> smallestSubRings(IAtomContainer ring) {
        AllRingsFinder arf = new AllRingsFinder();
        List<IAtomContainer> subRings = new ArrayList<IAtomContainer>();
        IRingSet rs;
        try {
            rs = arf.findAllRings(ring);
        } catch (CDKException e) {
            this.logger.error("Error " + e.getMessage());
            return subRings;
        }
        List<IAtomContainer> allRings = Lists.newArrayList(rs.atomContainers());
        for (IAtomContainer subRing : allRings) {
            if (isSmallest(subRing, allRings)) {
                subRings.add(subRing);
            }
        }
        return subRings;
    };


    /** 
     * Method to compute smallest rings via SSSR finder.
     * 
     * @param ring Fused ring to be broken up.
     * 
     * @return Subrings as atom containers.
     */
    private List<IAtomContainer> sssrSubRings(IAtomContainer ring) {
        this.logger.logging("SSSR sub ring computation.\n");
        SSSRFinder sssr = new SSSRFinder(ring);
        IRingSet essentialRings = sssr.findSSSR();
        return Lists.newArrayList(essentialRings.atomContainers());
    }


    /**
     * Returns atom set id and increments id counter.
     * @return A new unique atom set id.
     */
    private String getAtomSetId() {
        atomSetCount++;
        return "as" + atomSetCount;
    }


    public String toString() {
        return Joiner.on("\n").join(this.richStructures.values()
                                    .stream().map(RichStructure::toString)
                                    .collect(Collectors.toList()));
    }



    private void aliphaticChains() {
        List<IAtomContainer> chains = getAliphaticChain();
        for (IAtomContainer chain : chains) {
            this.logger.logging(chain);
            String id = getAtomSetId();
            this.richStructures.put(id, new RichAtomSet(chain, RichAtomSet.Type.ALIPHATIC, id));
        }
    }


    /**
     * Computes the longest aliphatic chain for the molecule.
     * @return The value of the aliphatic chain.
     */
    private List<IAtomContainer> getAliphaticChain() {
        IAtomContainer container = this.molecule;
        if (container == null) { return null; }
        AliphaticChain chain = new AliphaticChain();
        chain.calculate(container);
        List<IAtomContainer> result = chain.extract();
        return(result);
    }   
    
}
