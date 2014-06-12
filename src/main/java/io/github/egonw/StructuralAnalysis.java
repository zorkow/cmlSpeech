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
import java.util.Set;
import java.util.HashSet;
import com.google.common.collect.Iterables;
import java.util.Map;

/**
 *
 */

public class StructuralAnalysis {
    
    private int atomSetCount = 0;
    private final Logger logger;
    private final IAtomContainer molecule;
    // TODO(sorge) Refactor this into a separate flags module.
    private final Cli cli;

    private static SortedMap<String, RichStructure> richAtoms = 
        new TreeMap(new CMLNameComparator());
    private static SortedMap<String, RichStructure> richBonds = 
        new TreeMap(new CMLNameComparator());
    private static SortedMap<String, RichStructure> richAtomSets = 
        new TreeMap(new CMLNameComparator());


    public StructuralAnalysis(IAtomContainer molecule, Cli cli, Logger logger) {
        this.cli = cli;
        this.logger = logger;
        this.molecule = molecule;
        

        initStructure();
        
        ringSearch();
        aliphaticChains();
        // TODO(sorge): functionalGroups();
        
        computeContexts();

    }

    public RichStructure getRichAtom(String id) {
        return this.richAtoms.get(id);
    }

    private RichStructure setRichAtom(IAtom atom) {
        return this.setRichStructure(this.richAtoms, atom.getID(), new RichAtom(atom));
    }
    


    public RichStructure getRichBond(String id) {
        return this.richBonds.get(id);
    }

    private RichStructure setRichBond(IBond bond) {
        return this.setRichStructure(this.richBonds, bond.getID(), new RichBond(bond));
    }
    


    public RichStructure getRichAtomSet(String id) {
        return this.richAtomSets.get(id);
    }

    private RichStructure setRichAtomSet(IAtomContainer atomSet, RichAtomSet.Type type) {
        String id = getAtomSetId();
        return this.setRichStructure(this.richAtomSets, id, new RichAtomSet(atomSet, type, id));
    }
    
    private RichStructure setRichStructure(SortedMap map, String id, RichStructure structure) {
        map.put(id, structure);
        return structure;
    }
    

    public RichStructure getRichStructure(String id) {
        RichStructure structure = this.richAtoms.get(id);
        if (structure != null) {
            return structure;
        } 
        structure = this.richBonds.get(id);
        if (structure != null) {
            return structure;
        } 
        return this.richAtomSets.get(id);
    }

    
    private void initStructure() {
        this.molecule.atoms().forEach(this::setRichAtom);
        for (IBond bond : this.molecule.bonds()) {
            this.setRichBond(bond);
            for (IAtom atom : bond.atoms()) {
                RichStructure richAtom = this.richAtoms.get(atom.getID());
                richAtom.getContexts().add(bond.getID());
                richAtom.getExternalBonds().add(bond.getID());
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
            this.setRichAtomSet(ring, RichAtomSet.Type.ISOLATED);
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
            this.setRichAtomSet(ring, RichAtomSet.Type.FUSED);
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
            RichAtomSet ring = (RichAtomSet)this.setRichAtomSet(system, RichAtomSet.Type.FUSED);
            
            List<IAtomContainer> subSystems = subRingMethod.apply(system);
            for (IAtomContainer subSystem : subSystems) {
                RichAtomSet subRing = (RichAtomSet)this.setRichAtomSet(subSystem, RichAtomSet.Type.SMALLEST);
                String ringId = ring.getId();
                String subRingId = subRing.getId();
                subRing.getSup().add(ringId);
                subRing.getContexts().add(ringId);
                ring.getSub().add(subRingId);
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


    private String valuesToString(SortedMap<String, RichStructure> map) {
        return Joiner.on("\n").join(map.values()
                                    .stream().map(RichStructure::toString)
                                    .collect(Collectors.toList()));
    }

    public String toString() {
        return valuesToString(this.richAtoms) + "\n" 
            + valuesToString(this.richBonds) + "\n" 
            + valuesToString(this.richAtomSets);
    }


    /**
     * Computes the longest aliphatic chain for the molecule.
     */
    private void aliphaticChains() {
        IAtomContainer container = this.molecule;
        if (container == null) { return; }
        AliphaticChain chain = new AliphaticChain();
        chain.calculate(container);
        for (IAtomContainer set : chain.extract()) {
            this.setRichAtomSet(set, RichAtomSet.Type.ALIPHATIC);
        }
    }


    private void computeContexts() {
        for (String key : this.richAtomSets.keySet()) {
            Set<String> set = this.richAtomSets.get(key).getComponents();
            for (String component : set) {
                this.getRichStructure(component).getContexts().add(key);
            };
        }
    }






    /**
     * Computes the siblings of this atom set if it is a subring.
     * @param atomSet The given atom set.
     * @return A list of siblings.
     */
    public Set<RichStructure> siblings(RichAtomSet atomSet) {
        Set<String> result = new HashSet<String>();
        if (atomSet.type == RichAtomSet.Type.SMALLEST) {
            for (String superSystem : atomSet.getSup()) {
                result.addAll(((RichAtomSet)this.getRichAtomSet(superSystem)).getSub());
            }
        }
        result.remove(atomSet.getId());
        return result.stream()
            .map(this::getRichAtomSet)
            .collect(Collectors.toSet());
    }

    
    public List<RichAtomSet> getAtomSets() {
        return (List<RichAtomSet>)(List<?>)new ArrayList(this.richAtomSets.values());
    }

}
