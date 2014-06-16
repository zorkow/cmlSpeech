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

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingSearch;
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

    private static SortedMap<String, RichStructure> richAtoms = 
        new TreeMap(new CMLNameComparator());
    private static SortedMap<String, RichStructure> richBonds = 
        new TreeMap(new CMLNameComparator());
    private static SortedMap<String, RichStructure> richAtomSets = 
        new TreeMap(new CMLNameComparator());

    private Set<String> singletonAtoms = new HashSet<String>();


    public StructuralAnalysis(IAtomContainer molecule, Cli cli, Logger logger) {
        this.cli = cli;
        this.logger = logger;
        this.molecule = molecule;
        

        this.initStructure();
        
        this.ringSearch();
        this.aliphaticChains();
        // TODO(sorge): functionalGroups();
        
        this.computeContexts();
        this.singletonAtoms();

        this.atomSetsAttachments();
        this.connectingBonds();
        this.sharedComponents();
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
                subRing.getSuperSystems().add(ringId);
                subRing.getContexts().add(ringId);
                ring.getSubSystems().add(subRingId);
                //ring.getComponents().add(subRingId);
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



    /////////////

    private void singletonAtoms() {
        Set<String> atomSetComponents = new HashSet<String>();
        this.richAtomSets.values().
            forEach(as -> atomSetComponents.addAll(as.getComponents()));
        for (String atom : this.richAtoms.keySet()) {
            if (!atomSetComponents.contains(atom)) {
                this.singletonAtoms.add(atom);
            }
        }
    }







    ///////////////////////////////////////////

    private void atomSetsAttachments() {
        this.richAtomSets.values().
            forEach(as -> this.atomSetAttachments((RichAtomSet)as));
    }


    private void atomSetAttachments(RichAtomSet atomSet) {
        IAtomContainer container = atomSet.getStructure();
        Set<IBond> externalBonds = externalBonds(container);
        for (IBond bond : externalBonds) {
            atomSet.getExternalBonds().add(bond.getID());
        }
        Set<IAtom> connectingAtoms = connectingAtoms(container, externalBonds);
        for (IAtom atom : connectingAtoms) {
            atomSet.getConnectingAtoms().add(atom.getID());
        }
    }


    /**
     * Compute the bonds that connects this atom container to the rest of the
     * molecule.
     * @param container The substructure under consideration.
     * @return List of bonds attached to but not contained in the container.
     */
    private Set<IBond> externalBonds(IAtomContainer container) {
        Set<IBond> internalBonds = Sets.newHashSet(container.bonds());
        Set<IBond> allBonds = Sets.newHashSet();
        for (IAtom atom : container.atoms()) {
            allBonds.addAll(this.molecule.getConnectedBondsList(atom));
        }
        return Sets.difference(allBonds, internalBonds);
    }


    /**
     * Compute the atoms that have bonds not internal to the molecule.
     * @param container The substructure under consideration.
     * @param bonds External bonds.
     * @return List of atoms with external connections.
     */
    private Set<IAtom> connectingAtoms(IAtomContainer container, Set<IBond> bonds) {
        Set<IAtom> allAtoms = Sets.newHashSet(container.atoms());
        Set<IAtom> connectedAtoms = Sets.newHashSet();
        for (IBond bond : bonds) {
            connectedAtoms.addAll
                (Lists.newArrayList(bond.atoms()).stream().
                 filter(a -> allAtoms.contains(a)).collect(Collectors.toSet()));
        }
        return connectedAtoms;
    }


    /**
     * Compute the connecting bonds for tha atom container from the set of
     * external bonds.
     * @param container The substructure under consideration.
     * @param externalBonds Bonds external to the substructure.
     * @return List of connecting bonds, i.e., external but not part of another
     *         substructure.
     */
    private void connectingBonds() {
        for (String bond : this.richBonds.keySet()) {
            RichStructure richBond = this.richBonds.get(bond);
            if (richBond.getContexts().isEmpty()) {
                // We assume each bond has two atoms only!
                this.addConnections(bond, 
                                    ((TreeSet<String>)richBond.getComponents()).first(), 
                                    ((TreeSet<String>)richBond.getComponents()).last());
            }
        }
    }


    private void addConnections(String bond, String atomA, String atomB) {
        Set<String> contextAtomA = Sets.intersection
            (this.richAtoms.get(atomA).getContexts(), 
             this.richAtomSets.keySet());
        Set<String> contextAtomB = Sets.intersection
            (this.richAtoms.get(atomB).getContexts(), 
             this.richAtomSets.keySet());
        if (contextAtomA.isEmpty()) {
            contextAtomA = new HashSet<String>();
            contextAtomA.add(atomA);
        }
        if (contextAtomB.isEmpty()) {
            contextAtomB = new HashSet<String>();
            contextAtomB.add(atomB);
        }
        for (String contextA : contextAtomA) {
            RichStructure richStructureA = this.getRichStructure(contextA);
            for (String contextB : contextAtomB) {
                RichStructure richStructureB = this.getRichStructure(contextB);
                richStructureA.getConnections().add
                    (new Connection(Connection.Type.CONNECTINGBOND, bond, contextB));
                richStructureB.getConnections().add
                    (new Connection(Connection.Type.CONNECTINGBOND, bond, contextA));
            }
        }
    }


    private void sharedComponents() {
        for (String atomSet : this.richAtomSets.keySet()) {
            RichAtomSet richAtomSet = (RichAtomSet)this.richAtomSets.get(atomSet);
            Set<String> internalComponents = Sets.difference
                (richAtomSet.getComponents(), this.richAtomSets.keySet());
            for (String component : internalComponents) {
                RichStructure richComponent = this.getRichStructure(component);
                Set<String> contexts = Sets.intersection
                    (richComponent.getContexts(), this.richAtomSets.keySet());
                for (String context : contexts) {
                    if (richAtomSet.getSubSystems().contains(context) ||
                        richAtomSet.getSuperSystems().contains(context) ||
                        context.equals(atomSet)) {
                        continue;
                    }
                    if(this.isBond(context)) {
                        richAtomSet.getConnections().add
                            (new Connection(Connection.Type.SHAREDBOND, component, context));
                    } else {
                        richAtomSet.getConnections().add
                            (new Connection(Connection.Type.SHAREDATOM, component, context));
                    }
                }
            }
        }
    }

    
    /**
     * Computes the siblings of this atom set if it is a subring.
     * @param atomSet The given atom set.
     * @return A list of siblings.
     */
    public Set<String> siblingsNEW(RichAtomSet atomSet) {
        Set<String> result = new HashSet<String>();
        if (atomSet.type == RichAtomSet.Type.SMALLEST) {
            for (String superSystem : atomSet.getSuperSystems()) {
                result.addAll(((RichAtomSet)this.getRichAtomSet(superSystem)).getSubSystems());
            }
        }
        result.remove(atomSet);
        return result;
    }

    
    /**
     * Computes the siblings of this atom set if it is a subring.
     * @param atomSet The given atom set.
     * @return A list of siblings.
     */
    public Set<RichStructure> siblings(RichAtomSet atomSet) {
        Set<String> result = new HashSet<String>();
        if (atomSet.type == RichAtomSet.Type.SMALLEST) {
            for (String superSystem : atomSet.getSuperSystems()) {
                result.addAll(((RichAtomSet)this.getRichAtomSet(superSystem)).getSubSystems());
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

    public List<RichAtom> getSingletonAtoms() {
        return (List<RichAtom>)(List<?>)this.singletonAtoms.stream()
            .map(this::getRichAtom).collect(Collectors.toList());
    }


    public boolean isAtom(String id) {
        return this.richAtoms.containsKey(id);
    }

    public boolean isBond(String id) {
        return this.richBonds.containsKey(id);
    }

    public boolean isAtomSet(String id) {
        return this.richAtomSets.containsKey(id);
    }


    SreAnnotations annotations = new SreAnnotations();

    
    public SreAnnotations toSre() {
        
        for (String structure : this.richAtoms.keySet()) {
            this.annotations.registerAnnotation(structure, SreNamespace.Tag.ATOM);
            this.toSreStructure(this.richAtoms.get(structure));
        }
        for (String structure : this.richBonds.keySet()) {
            annotations.registerAnnotation(structure, SreNamespace.Tag.BOND);
            this.toSreStructure(this.richBonds.get(structure));
        }
        for (String structure : this.richAtomSets.keySet()) {
            annotations.registerAnnotation(structure, SreNamespace.Tag.ATOMSET);
            this.toSreStructure((RichAtomSet)this.richAtomSets.get(structure));
        }
        annotations.finalize();
        return annotations;
    }


    private void toSreSet(String annotate, SreNamespace.Tag tag, Set<String> set) {
        for (String element : set) {
            this.annotations.appendAnnotation(annotate, tag, this.toSreElement(element));
        }
    }


    private void toSreConnections(RichStructure structure) {
        String id = structure.getId();
        Set<Connection> connections = structure.getConnections();
        for (Connection connection : connections) {
            SreNamespace.Tag tag;
            switch (connection.getType()) {
            case SHAREDBOND:
                tag = SreNamespace.Tag.SHAREDBOND;
                break;
            case SHAREDATOM:
                tag = SreNamespace.Tag.SHAREDATOM;
                break;
            case CONNECTINGBOND:
            default:
                tag = SreNamespace.Tag.CONNECTION;
                break;
            }
            this.annotations.appendAnnotation(id, SreNamespace.Tag.CONNECTIONS,
                                              new SreElement(tag,
                                                             this.toSreElement(connection.getConnector()), 
                                                             this.toSreElement(connection.getConnected())));
        }
    }

    private void toSreStructure(RichStructure structure) {
        String id = structure.getId();
        this.toSreSet(id, SreNamespace.Tag.CONTEXT, structure.getContexts());
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, structure.getComponents());
        this.toSreSet(id, SreNamespace.Tag.EXTERNALBONDS, structure.getExternalBonds());
        this.toSreConnections(structure);
    }


    private void toSreStructure(RichAtomSet structure) {
        String id = structure.getId();
        this.toSreStructure((RichStructure)structure);
        this.toSreSet(id, SreNamespace.Tag.INTERNALBONDS, 
                      structure.getComponents().stream().filter(this::isBond).collect(Collectors.toSet()));
        this.toSreSet(id, SreNamespace.Tag.SUBSYSTEM, structure.getSubSystems());
        this.toSreSet(id, SreNamespace.Tag.SUPERSYSTEM, structure.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CONNECTINGATOMS, structure.getConnectingAtoms());
    }


    private SreElement toSreElement(String name) {
        if (this.isAtom(name)) {
            return new SreElement(SreNamespace.Tag.ATOM, name);
        }
        if (this.isBond(name)) {
            return new SreElement(SreNamespace.Tag.BOND, name);
        }
        if (this.isAtomSet(name)) {
            return new SreElement(SreNamespace.Tag.ATOMSET, name);
        }
        return new SreElement(SreNamespace.Tag.UNKNOWN, name);
    }


}
