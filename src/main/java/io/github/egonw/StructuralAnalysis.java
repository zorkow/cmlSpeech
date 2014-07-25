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
import java.util.Stack;
import java.util.Comparator;
import java.util.Collections;
import org.jgrapht.alg.NeighborIndex;
import java.util.SortedSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;

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

    private List<RichAtomSet> majorSystems;
    private List<RichAtomSet> minorSystems;
    private Set<String> singletonAtoms = new HashSet<String>();

    private List<String> majorPath = new ArrayList<String>();
    //private BiMap<Integer, String> atomPositions = HashBiMap.create();
    private ComponentsPositions atomPositions = new ComponentsPositions();


    public StructuralAnalysis(IAtomContainer molecule, Cli cli, Logger logger) {
        this.cli = cli;
        this.logger = logger;
        this.molecule = molecule;
        

        this.initStructure();
        
        this.ringSearch();
        this.aliphaticChains();
        // TODO(sorge): functionalGroups();
        
        this.contexts();
        this.singletonAtoms();
        this.majorSystems();
        this.minorSystems();

        this.atomSetsAttachments();
        this.connectingBonds();
        this.sharedComponents();
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


    public RichAtom getRichAtom(String id) {
        return (RichAtom)this.richAtoms.get(id);
    }

    private RichStructure setRichAtom(IAtom atom) {
        return this.setRichStructure(this.richAtoms, atom.getID(), new RichAtom(atom));
    }
    


    public RichBond getRichBond(String id) {
        return (RichBond)this.richBonds.get(id);
    }

    public RichStructure getRichBond(IBond bond) {
        return this.getRichBond(bond.getID());
    }

    private RichStructure setRichBond(IBond bond) {
        return this.setRichStructure(this.richBonds, bond.getID(), new RichBond(bond));
    }
    


    public RichAtomSet getRichAtomSet(String id) {
        return (RichAtomSet)this.richAtomSets.get(id);
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

    
    public List<RichAtom> getAtoms() {
        return (List<RichAtom>)(List<?>)new ArrayList(this.richAtoms.values());
    }

    public List<RichBond> getBonds() {
        return (List<RichBond>)(List<?>)new ArrayList(this.richBonds.values());
    }

    public List<RichAtomSet> getAtomSets() {
        return (List<RichAtomSet>)(List<?>)new ArrayList(this.richAtomSets.values());
    }

    public List<RichAtom> getSingletonAtoms() {
        return (List<RichAtom>)(List<?>)this.singletonAtoms.stream()
            .map(this::getRichAtom).collect(Collectors.toList());
    }


    /** Initialises the structure from the molecule. */
    private void initStructure() {
        this.molecule.atoms().forEach(this::setRichAtom);
        for (IBond bond : this.molecule.bonds()) {
            this.setRichBond(bond);
            for (IAtom atom : bond.atoms()) {
                RichStructure richAtom = this.getRichAtom(atom.getID());
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


    private void contexts() {
        for (String key : this.richAtomSets.keySet()) {
            Set<String> set = this.getRichAtomSet(key).getComponents();
            for (String component : set) {
                this.getRichStructure(component).getContexts().add(key);
            };
        }
    }


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
            RichStructure richBond = this.getRichBond(bond);
            String first = ((TreeSet<String>)richBond.getComponents()).first();
            String last = ((TreeSet<String>)richBond.getComponents()).last();
            if (richBond.getContexts().isEmpty()) {
                // We assume each bond has two atoms only!
                this.addSetConnections(bond, first, last);
            }
            this.addConnectingBond(this.getRichStructure(first), bond, last);
            this.addConnectingBond(this.getRichStructure(last), bond, first);
        }
    }

    
    private void addConnectingBond(RichStructure structure, String bond, String connected) {
        structure.getConnections().add
            (new Connection(Connection.Type.CONNECTINGBOND, bond, connected));
    }

    /**
     * Creates the context cloud for an atom, that is the list of all atom sets
     * in its context.
     * @param atom The input atom.
     * @return The resulting context cloud.
     */
    private Set<String> contextCloud(String atom) {
        Set<String> contextAtom = Sets.intersection
            (this.getRichAtom(atom).getContexts(), 
             this.richAtomSets.keySet());
        if (contextAtom.isEmpty()) {
            contextAtom = new HashSet<String>();
            contextAtom.add(atom);
        }
        return contextAtom;
    }
    

    /**
     * Adds connections atom set structures.
     * @param bond The bond.
     * @param atomA The first atom in the bond.
     * @param atomB The second atom in the bond.
     */
    private void addSetConnections(String bond, String atomA, String atomB) {
        Set<String> contextAtomA = this.contextCloud(atomA);
        Set<String> contextAtomB = this.contextCloud(atomB);
        for (String contextA : contextAtomA) {
            RichStructure richStructureA = this.getRichStructure(contextA);
            for (String contextB : contextAtomB) {
                RichStructure richStructureB = this.getRichStructure(contextB);
                this.addConnectingBond(richStructureA, bond, contextB);
                this.addConnectingBond(richStructureB, bond, contextA);
            }
        }
    }


    private void sharedComponents() {
        for (String atomSet : this.richAtomSets.keySet()) {
            RichAtomSet richAtomSet = this.getRichAtomSet(atomSet);
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
                result.addAll(this.getRichAtomSet(superSystem).getSubSystems());
            }
        }
        result.remove(atomSet.getId());
        return result.stream()
            .map(this::getRichAtomSet)
            .collect(Collectors.toSet());
    }

    
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


    /** Compute the major systems, i.e., all systems that are not part of a
     * larger supersystem. */
    private void majorSystems() {
        this.majorSystems = this.getAtomSets().stream()
            .filter(as -> as.type != RichAtomSet.Type.SMALLEST)
            .collect(Collectors.toList());
    }
    

    /**
     * Returns the major systems.
     * @return List of major systems.
     */
    public List<RichAtomSet> getMajorSystems() {
        return this.majorSystems;
    }


    /** Compute the minor systems, i.e., all systems that have no non-atomic
     * sub-system. */
    private void minorSystems() {
        this.minorSystems = this.getAtomSets().stream()
            .filter(as -> as.type != RichAtomSet.Type.FUSED)
            .collect(Collectors.toList());
    }
    

    /**
     * Returns the minor systems.
     * @return List of minor systems.
     */
    public List<RichAtomSet> getMinorSystems() {
        return this.minorSystems;
    }


    /**
     * Computes the major path through the molecule.
     * @param graph The abstraction graph for the molecule.
     */
    public void majorPath (StructuralGraph graph) {
        NeighborIndex index = new NeighborIndex(graph);
        Comparator<String> comparator = new RichStructureCompare();
        Stack<String> rest = new Stack<String>();
        List<String> vertices = new ArrayList<String>(graph.vertexSet());
        Collections.sort(vertices, comparator);
        List<String> visited = new ArrayList<String>();
        rest.push(vertices.get(0));
        while (!rest.empty()) {
            String current = rest.pop();
            if (visited.contains(current)) {
                continue;
            }
            this.majorPath.add(current);
            vertices = new ArrayList<String>(index.neighborsOf(current));
            Collections.sort(vertices, comparator);
            for (int i = vertices.size() - 1; i >= 0; i--) {
                rest.push(vertices.get(i));
            }
            visited.add(current);
        }
    }

    // Comparison in terms of "interestingness". The most interesting is sorted to the front.
    // TODO (sorge): Maybe reverse this?
    public class RichStructureCompare implements Comparator<String> {

        // TODO (sorge): Rationalise this code!
        //private Boolean moreInteresting(String vertexA, String vertexB) {
        public int compare(String vertexA, String vertexB) {
            if (vertexB.equals("")) {
                return -1;
            }
            if (StructuralAnalysis.this.isAtom(vertexA)) {
                return 1;
            }
            // TODO (sorge): Take Atom weight into account!
            if (StructuralAnalysis.this.isAtom(vertexB)) {
                return -1;
            }
            // Now both should be rich atom sets!
            RichAtomSet structureA = StructuralAnalysis.this.getRichAtomSet(vertexA);
            RichAtomSet structureB = StructuralAnalysis.this.getRichAtomSet(vertexB);
            RichAtomSet.Type typeA = structureA.getType();
            RichAtomSet.Type typeB = structureB.getType();
            if (typeA == RichAtomSet.Type.ALIPHATIC && 
                (typeB == RichAtomSet.Type.FUSED || 
                 typeB == RichAtomSet.Type.ISOLATED ||
                 typeB == RichAtomSet.Type.SMALLEST)) {
                return 1;
            }
        if (typeB == RichAtomSet.Type.ALIPHATIC && 
            (typeA == RichAtomSet.Type.FUSED || 
             typeA == RichAtomSet.Type.ISOLATED ||
             typeA == RichAtomSet.Type.SMALLEST)) {
            return -1;
        }
        // (TODO) sorge: Add functional group cases!
        
        return -1 * Integer.compare(structureA.getStructure().getAtomCount(), 
                                    structureB.getStructure().getAtomCount());
        }
        
    }

    public void computePositions() {
        Integer position = 0;
        for (String structure : this.majorPath) {
            System.out.println(position);
            if (this.isAtom(structure)) {
                this.atomPositions.put(++position, structure);
            } else {
                RichAtomSet atomSet = this.getRichAtomSet(structure);
                if (atomSet.getType() == RichAtomSet.Type.FUSED) {
                    for (String sub : atomSet.getSubSystems()) {
                        RichAtomSet subSystem = this.getRichAtomSet(sub);
                        position = this.appendPositions(subSystem, position);
                        atomSet.appendPositions(subSystem);
                    }
                } else {
                    position = appendPositions(atomSet, position);
                }
            }
        }
    }

    public Integer appendPositions(RichAtomSet atomSet, Integer position) {
        System.out.println("here");
        atomSet.computePositions(position);
        Iterator<String> iterator = atomSet.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (!this.atomPositions.containsValue(value)) { 
                this.atomPositions.put(++position, value);
            }
        }
        return position;
    }

    public void printPositions () {
        for (Integer key : this.atomPositions.atomPositions.keySet()) {
            System.out.printf("%d: %s\n", key, this.atomPositions.get(key));
        }
        this.majorPath.stream().forEach(a -> 
                                        {if (this.isAtomSet(a)) {
                                                System.out.println(a);
                                                this.getRichAtomSet(a).printPositions();
                                            }});
    }

    public String getPositionAtom(Integer position) {
        return this.atomPositions.get(position);
    }


    public Integer getAtomPosition(String atom) {
        return this.atomPositions.inverse().get(atom);
    }

}
