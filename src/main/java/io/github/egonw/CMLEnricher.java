/**
 * @file   CMLEnricher.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Apr 28 01:41:35 2014
 * 
 * @brief  Main class to enrich CML files.
 * 
 * 
 */

package io.github.egonw;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Nodes;
import nux.xom.pool.XOMUtil;

import org.apache.commons.io.FilenameUtils;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import nu.xom.Namespace;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Attribute;
import nu.xom.Node;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import java.util.Collection;
import org.openscience.cdk.interfaces.IBond;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.interfaces.MinimumSpanningTree;
import nu.xom.XPathContext;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;
import java.util.Map;
import java.util.TreeMap;
import com.google.common.base.Joiner;


public class CMLEnricher {
    private final Cli cli;
    private final Logger logger;

    private Document doc;
    private IAtomContainer molecule;
    private int atomSetCount;
    private List<RichAtomSet> atomSets = new ArrayList<RichAtomSet>();
    private CactusExecutor executor = new CactusExecutor();
    private SreAnnotations annotations;
    private SreDescription description = new SreDescription();
    private List<RichAtomSet> majorSystems;
    private List<RichAtomSet> minorSystems;
    private Set<IAtom> singletonAtoms = new HashSet<IAtom>();
    private StructuralGraph structure = new StructuralGraph();

    /** 
     * Constructor
     * 
     * @param initCli The interpreted command line.
     * @param initLogger The logger structure.
     * 
     * @return The newly created object.
     */    
    public CMLEnricher(Cli initCli, Logger initLogger) {
        cli = initCli;
        logger = initLogger;
    }


    /** 
     * Enriches all CML files given as input arguments.
     * 
     */
    public void enrichFiles() {
        for (String file : cli.files) {
            enrichFile(file);
        }
    }


    /** 
     * Convenience method to enrich a CML file. Does all the error catching.
     * 
     * @param fileName File to enrich.
     */
    private void enrichFile(String fileName) {
        this.atomSetCount = 0;
        try {
            readFile(fileName);
            buildXOM();
            removeExplicitHydrogenes();
            this.annotations = new SreAnnotations(this.molecule);
            enrichCML();
            this.atomSets.stream().forEach(this::finalizeAtomSet);
            getAbstractionGraph();
            nameMolecule(this.doc.getRootElement().getAttribute("id").getValue(), this.molecule);
            this.annotations.finalize();
            this.doc.getRootElement().appendChild(this.annotations);
            executor.execute();
            executor.addResults(this.doc, this.logger);
            executor.shutdown();
            generateDescription();
            this.doc.getRootElement().appendChild(this.description);
            writeFile(fileName);
        } catch (Exception e) { 
            // TODO: Meaningful exception handling by exceptions/functions.
            this.logger.error("Something went wrong when parsing File " + fileName +
                              ":" + e.getMessage() + "\n");
            e.printStackTrace();
            return;
        }
        if (this.cli.cl.hasOption("vis")) {
            this.structure.visualize(this.majorSystems, this.singletonAtoms);
        } 
    }


    /** 
     * Loads current file into the molecule IAtomContainer.
     * 
     * @param fileName File to load.
     * 
     * @throws IOException Problems with loading file.
     * @throws CDKException Problems with CML file format.
     */
    private void readFile(String fileName) throws IOException, CDKException {
        InputStream file = new BufferedInputStream
            (new FileInputStream(fileName));
        ISimpleChemObjectReader reader = new ReaderFactory().createReader(file);
        IChemFile cFile = null;
        cFile = reader.read(SilentChemObjectBuilder.getInstance().
                            newInstance(IChemFile.class));
        reader.close();
        this.molecule = ChemFileManipulator.getAllAtomContainers(cFile).get(0);
        this.logger.logging(this.molecule);
    }


    /** 
     * Build the CML XOM element.
     * 
     * @throws IOException Problems with StringWriter
     * @throws CDKException Problems with CMLWriter
     * @throws ParsingException Problems with building CML XOM.
     */
    private void buildXOM() throws IOException, CDKException, ParsingException {
        StringWriter outStr = new StringWriter();
        CMLWriter cmlwriter = new CMLWriter(outStr);
        cmlwriter.write(this.molecule);
        cmlwriter.close();
        String cmlcode = outStr.toString();

        Builder builder = new CMLBuilder(); 
        // this.doc.getRootElement().addNamespaceDeclaration
        //     ("cml", "http://www.xml-cml.org/schema");
        this.doc = builder.build(cmlcode, "");
        this.logger.logging(this.doc.toXML());
    }


    private void removeExplicitHydrogenes() {
        this.molecule = AtomContainerManipulator.removeHydrogens(this.molecule);
    }

    /** 
     * Writes current document to a CML file.
     * 
     * @param fileName
     *
     * @throws IOException Problems with opening output file.
     * @throws CDKException Problems with writing the CML XOM.
     */
    private void writeFile(String fileName) throws IOException, CDKException {
        FilenameUtils fileUtil = new FilenameUtils();
        String basename = fileUtil.getBaseName(fileName);
        OutputStream outFile = new BufferedOutputStream
            (new FileOutputStream(basename + "-enr.cml"));
        PrintWriter output = new PrintWriter(outFile);
        this.doc.getRootElement().addNamespaceDeclaration
            (SreNamespace.getInstance().prefix, SreNamespace.getInstance().uri);
        output.write(XOMUtil.toPrettyXML(this.doc));
        output.flush();
        output.close();
    }


    /** Enriches the current CML documment. */
    private void enrichCML() {
        RingSearch ringSearch = new RingSearch(this.molecule);

        if (this.cli.cl.hasOption("s")) {
            getFusedRings(ringSearch);
        } else {
            getFusedRings(ringSearch, this.cli.cl.hasOption("sssr") ?
                          this::sssrSubRings : this::smallestSubRings);
        }
        getIsolatedRings(ringSearch);
        List<IAtomContainer> chains = getAliphaticChain();
        for (IAtomContainer chain : chains) {
            this.logger.logging(chain);
            RichAtomSet set = new RichAtomSet(chain, RichAtomSet.Type.ALIPHATIC);
            appendAtomSet("Aliphatic chain", set);
        }
    }


    /**
     * Creates a deep clone of an atom container catching possible errors.
     * @param container The container to be cloned.
     * @return The cloned container. Possibly null if cloning failed!
     */
    private IAtomContainer checkedClone(IAtomContainer container) {
        IAtomContainer newcontainer = null;
        try {
            newcontainer = container.clone();
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newcontainer);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(newcontainer);
        } catch (CloneNotSupportedException e){
            this.logger.error("Something went wrong cloning atom container: " + e.getMessage());
        } catch (Throwable e) {
            this.logger.error("Error " + e.getMessage());
        }
        return newcontainer;
    }


    /**
     * Computes the longest aliphatic chain for the molecule.
     * @return The value of the aliphatic chain.
     */
    private List<IAtomContainer> getAliphaticChain() {
        //IAtomContainer container = checkedClone(this.molecule);
        IAtomContainer container = this.molecule;
        if (container == null) { return null; }
        AliphaticChain chain = new AliphaticChain();
        DescriptorValue descr = chain.calculate(container);
        List<IAtomContainer> result = chain.extract();
        this.logger.logging("Longest Chain Count: " + descr.getValue());
        return(result);
    }   
    

    // TODO(sorge): With the RichAtomSet class it should be possible to simply 
    //              append all the atomsets at the end of the computation.
    /** 
     * Computes Isolated rings.
     * 
     * @param ringSearch The current ringsearch.
     */
    private void getIsolatedRings(RingSearch ringSearch) {
        List<IAtomContainer> ringSystems = ringSearch.isolatedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            RichAtomSet set = new RichAtomSet(ring, RichAtomSet.Type.ISOLATED);
            appendAtomSet("Isolated ring", set);
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
            RichAtomSet set = new RichAtomSet(ring, RichAtomSet.Type.FUSED);
            appendAtomSet("Fused Ring", set);
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
        for (IAtomContainer ring : ringSystems) {
            RichAtomSet set = new RichAtomSet(ring, RichAtomSet.Type.FUSED);
            String ringId = appendAtomSet("Fused ring", set);
            List<IAtomContainer> subRings = subRingMethod.apply(ring);
            for (IAtomContainer subRing : subRings) {
                RichAtomSet subSet = new RichAtomSet(subRing, RichAtomSet.Type.SMALLEST);
                set.addSub(appendAtomSet("Subring", subSet, ringId));
                subSet.addSup(ringId);
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


    /** 
     * Append an Atom Set to the CML documents.
     * 
     * @param title Title of the atom set to be added. 
     * @param atoms Iterable atom list.
     * 
     * @return The atom set id.
     */
    private String appendAtomSet(String title, RichAtomSet set) {
        String id = getAtomSetId();
        set.setTitle(title);
        set.setId(id);
        this.logger.logging(title + " has atoms:");
        // TODO (sorge) Refactor that eventually together with appendAtomSet.
        for (IAtom atom : set.container.atoms()) {
            String atomId = atom.getID();
            Element node = SreUtil.getElementById(this.doc, atomId);
            this.annotations.appendAnnotation(node, atomId, SreNamespace.Tag.COMPONENT, new SreElement(set));
            set.addAtom((CMLAtom)node);
            this.logger.logging(" " + atomId);
        }
        this.logger.logging("\n");
        for (IBond bond : set.container.bonds()) {
            String bondId = bond.getID();
            Element node = SreUtil.getElementById(this.doc, bondId);
            this.annotations.appendAnnotation(node, bondId, SreNamespace.Tag.COMPONENT, new SreElement(set));
            this.annotations.appendAnnotation(set, SreNamespace.Tag.INTERNALBONDS, new SreElement(bond));
        }
        this.atomSets.add(set);
        this.doc.getRootElement().appendChild(set);
        nameMolecule(set.getId(), set.container);
        return(id);
    }


    private void finalizeAtomSet(RichAtomSet atomSet) {
        IAtomContainer container = atomSet.container;
        Set<IBond> externalBonds = externalBonds(container);
        for (IBond bond : externalBonds) {
            String bondId = bond.getID();
            this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.EXTERNALBONDS, new SreElement(bond));
        }
        Set<IAtom> connectingAtoms = connectingAtoms(container, externalBonds);
        for (IAtom atom : connectingAtoms) {
            String atomId = atom.getID();
            this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTINGATOMS, new SreElement(atom));
        }
        Set<IBond> connectingBonds = connectingBonds(container, externalBonds);
        for (IBond bond : connectingBonds) {
            String bondId = bond.getID();
            this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTINGBONDS, new SreElement(bond));
            addConnection(atomSet, bond);
        }
        computeSharedConnections(atomSet);
        atomSet.computePositions();
    }


    private void computeSharedConnections(RichAtomSet atomSet) {
        if (atomSet.type == RichAtomSet.Type.SMALLEST) {
            sharedBonds(atomSet);
        }
        sharedAtoms(atomSet);
    }

    private void addConnection(RichAtomSet atomSet, IBond bond) {
        // For now we assume that the bond has exactly two atoms.
        IAtom internalAtom = bond.getAtom(0);
        IAtom externalAtom;
        if (atomSet.container.contains(internalAtom)) {
            externalAtom = bond.getAtom(1);
        } else {
            externalAtom = internalAtom;
            internalAtom = bond.getAtom(1);
        }
        boolean simple = true;
        for (RichAtomSet otherSet : this.atomSets) {
            if (otherSet.container.contains(externalAtom)) {
                simple = false;
                atomSet.addConnection(internalAtom, otherSet, bond);
                this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTIONS, 
                                                  new SreElement(SreNamespace.Tag.CONNECTION,
                                                                 new SreElement(bond),
                                                                 new SreElement(otherSet)));
            }
        }
        if (simple) {
            this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTIONS, 
                                              new SreElement(SreNamespace.Tag.CONNECTION,
                                                             new SreElement(bond),
                                                             new SreElement(externalAtom)));
            atomSet.addConnection(internalAtom, externalAtom, bond);
            addSingletonAtom(externalAtom);
            this.annotations.appendAnnotation(externalAtom, SreNamespace.Tag.CONNECTIONS,
                                              new SreElement(SreNamespace.Tag.CONNECTION,
                                                             new SreElement(bond),
                                                             new SreElement(atomSet)));
        }
    }

    private void addConnection(IAtom atom) {
        for (IBond bond : this.molecule.getConnectedBondsList(atom)) {
            IAtom atomA = bond.getAtom(0);
            if (atomA == atom) {
                atomA = bond.getAtom(1);
            }
            this.annotations.appendAnnotation(atom, SreNamespace.Tag.CONNECTIONS,
                                              new SreElement(SreNamespace.Tag.CONNECTION,
                                                             new SreElement(bond),
                                                             new SreElement(atomA)));
        }
    }
    

    private void addSingletonAtom(IAtom atom) {
        // Maybe put this into an enriched container.
        this.singletonAtoms.add(atom);
    }

    private void sharedBonds(RichAtomSet atomSet) {
        Set<String> siblings = atomSet.siblings(atomSets);
        Set<RichAtomSet> siblingSets = siblings.stream()
            .map(as -> RichAtomSet.retrieveAtomSet(this.atomSets, as))
            .collect(Collectors.toSet());
        for (IBond bond : atomSet.container.bonds()) {
            for (RichAtomSet sibling : siblingSets) {
                if (sibling.container.contains(bond)) {
                    this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTIONS, 
                                                      new SreElement(SreNamespace.Tag.SHAREDBOND,
                                                                     new SreElement(bond),
                                                                     new SreElement(sibling)));
                }
            }
        };
    }
    
    private void sharedAtoms(RichAtomSet atomSet) {
        for (IAtom atom : atomSet.container.atoms()) {
            for (RichAtomSet otherSet : this.atomSets) {
                // Cases: No shared Atom if sub or super
                if (atomSet.isSub(otherSet) ||
                    atomSet.isSup(otherSet) || 
                    otherSet.getId() == atomSet.getId()) {
                    continue;
                }
                if (otherSet.container.contains(atom)) {
                    this.annotations.appendAnnotation(atomSet, SreNamespace.Tag.CONNECTIONS, 
                                                      new SreElement(SreNamespace.Tag.SHAREDATOM,
                                                                     new SreElement(atom),
                                                                     new SreElement(otherSet)));
                }
            }
        };
    }
    

    /**
     * Compute the connecting bonds for tha atom container from the set of
     * external bonds.
     * @param container The substructure under consideration.
     * @param externalBonds Bonds external to the substructure.
     * @return List of connecting bonds, i.e., external but not part of another
     *         substructure.
     */
    private Set<IBond> connectingBonds(IAtomContainer container, Set<IBond> externalBonds) {
        Set<IBond> connectingBonds = Sets.newHashSet();
        for (IBond bond : externalBonds) {
            if (isConnecting(container, bond)) {
                connectingBonds.add(bond);
            }
        }
        return connectingBonds;
    }


    /**
     * Checks if a bond is a connecting bond for this atom container. A
     * connecting bond is an external bond that is not internal to any other
     * structure. For example >-< is a connecting bond, while >< is not, and
     * only contains a connecting atom.
     * @param atoms The system.
     * @param bond An external bond of that system.
     * @return True if the bond is truely connecting.
     */
    private Boolean isConnecting(IAtomContainer atoms, IBond bond) {
        return this.atomSets.stream().
            allMatch(ring -> !(ring.container.contains(bond)));
    }


    /** 
     * Append an Atom Set to the CML documents.
     * 
     * @param title Title of the atom set to be added. 
     * @param atoms Iterable atom list.
     * @param superSystem Id of the super set.
     * 
     * @return The atom set id.
     */
    private String appendAtomSet(String title, RichAtomSet set, String superSystem) {
        String id = appendAtomSet(title, set);
        Element sup = SreUtil.getElementById(this.doc, superSystem);
        Element sub = SreUtil.getElementById(this.doc, id);
        this.annotations.appendAnnotation(sup, superSystem, SreNamespace.Tag.SUBSYSTEM, 
                                          new SreElement(SreNamespace.Tag.ATOMSET, id));
        this.annotations.appendAnnotation(sub, id, SreNamespace.Tag.SUPERSYSTEM, 
                                          new SreElement(SreNamespace.Tag.ATOMSET, superSystem));
        return(id);
    };


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
     * Compute the atoms that have bonds not internal to the molecule.
     * @param container The substructure under consideration.
     * @return List of atoms with external connections.
     */
    private Set<IAtom> connectingAtoms(IAtomContainer container) {
        Set<IBond> bonds = externalBonds(container);
        return connectingAtoms(container, bonds);
    }


    /**
     * Computes some names for a molecule by registering calls to Cactus.
     * @param id The id of the atom set.
     * @param container The molecule to be named.
     */
    private void nameMolecule(String id, IAtomContainer container) {
        // TODO (sorge) catch the right exception.
        this.logger.logging("Registering calls for " + id);
        IAtomContainer newcontainer = checkedClone(container);
        if (newcontainer != null) {
            this.executor.register(new CactusCallable(id, Cactus.Type.IUPAC, newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.NAME, newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.FORMULA, newcontainer));
        }
    }

    
    /** Computes the major path in the molecule. */
    private void getAbstractionGraph() {
        // TODO (sorge) Maybe refactor this out of path computation.
        // TODO (sorge) refactor to have major/minor systems and singletons held
        // globally.
        this.majorSystems = getMajorSystems();
        completeSingletonAtoms(this.majorSystems);
        List<String> msNames = this.majorSystems.stream()
            .map(RichAtomSet::getId)
            .collect(Collectors.toList());
        msNames.addAll(this.singletonAtoms.stream()
                       .map(IAtom::getID)
                       .collect(Collectors.toList()));
        msNames.stream().forEach(ms -> this.structure.addVertex(ms));

        for (String ms : msNames) {
            SreElement connections = this.annotations.retrieveAnnotation(ms, SreNamespace.Tag.CONNECTIONS);
            if (connections != null) {
                addSingleEdges(ms, connections, msNames);
            }
        }
    };


    private void addSingleEdges(String source, SreElement connections, List<String> systems) {
        Elements children = connections.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Elements grandkids = children.get(i).getChildElements();
            String target = grandkids.get(1).getValue();
            if (systems.contains(target)) {
                this.structure.addEdge(source, target,
                                       (SreElement)grandkids.get(0));
            }
        }
    }
    
    
    private List<RichAtomSet> getMajorSystems() {
        return this.atomSets.stream()
            .filter(as -> as.type != RichAtomSet.Type.SMALLEST)
            .collect(Collectors.toList());
    }


    private void completeSingletonAtoms(List<RichAtomSet> majorSystems) {
        Set<IAtom> majorAtoms = new HashSet<IAtom>();
        majorSystems.stream().
            forEach(ms -> majorAtoms.
                    addAll(Lists.newArrayList(ms.container.atoms())));
        for (IAtom atom : this.molecule.atoms()) {
            if (!majorAtoms.contains(atom) && !this.singletonAtoms.contains(atom)) {
                addConnection(atom);
                addSingletonAtom(atom);
            }
        }
    }


    // TODO (sorge): This needs serious refactoring! Generating descriptions could be 
    // done easier when keeping some of the information in more dedicated data structures!
    
    private void generateDescription() {
        // Currently using fixed levels!
        descriptionTopLevel();
        //descriptionMajorLevel();
        //        this.description.addDescription(2, "Aliphatic Chain", descriptionAtomSetElements("as1"));
        descriptionMajorLevel();
        this.description.finalize();
    }


    private void descriptionMajorLevel() {
        this.majorSystems.stream().forEach(this::descriptionMajorSystem);
    }

    private void descriptionMajorSystem(RichAtomSet system) {
        // This is what we need.
        Element systemElement;
        Element systemAnnotation;
        List<Element> atoms = new ArrayList<Element>();
        List<String> atomNames = new ArrayList<String>();
        List<Element> bonds = new ArrayList<Element>();
        List<String> bondNames = new ArrayList<String>();
        List<Element> connections = new ArrayList<Element>(); 
        
        // Filling things in.
        String id = system.getId();
        systemElement = (Element)SreUtil.xpathQueryElement
            (this.doc.getRootElement(), 
             "//cml:atomSet[@id='" + id + "']");
        System.out.println(1);
        systemAnnotation = (Element)SreUtil.xpathQueryElement
            (this.annotations, 
             "//sre:annotation[sre:atomSet='" + id + "']");
        System.out.println(2);
        atomNames = this.splitAttribute(systemElement.getValue());
        System.out.println(3);
        bondNames = SreUtil.xpathValueList(systemAnnotation,
                                           "//sre:internalBonds/sre:bond");
        System.out.println(4);
        for (String atomName : atomNames) {
            atoms.add((Element)SreUtil.xpathQueryElement
                      (this.doc.getRootElement(), 
                       "//cml:atom[@id='" + atomName + "']"));
        }
        System.out.println(5);
        for (String bondName : bondNames) {
            bonds.add((Element)SreUtil.xpathQueryElement
                      (this.doc.getRootElement(), 
                       "//cml:bond[@id='" + bondName + "']"));
        }
        System.out.println(6);
        Nodes aux = SreUtil.xpathQuery(systemAnnotation, ".//sre:connection");
        for (int i = 0; i < aux.size(); i++) {
            connections.add((Element)aux.get(i));
        } 
        // System.out.println(systemElement.toXML());
        System.out.println(systemAnnotation.toXML());
        // atomNames.stream().forEach(System.out::println);
        // bondNames.stream().forEach(System.out::println);
        // bonds.stream().forEach(x -> System.out.println(x.toXML()));
        // atoms.stream().forEach(x -> System.out.println(x.toXML()));
        connections.stream().forEach(x -> System.out.println(x.toXML()));

        switch (system.type) {
        case ALIPHATIC:
            descriptionAliphaticChain
                (system, systemElement, systemAnnotation, atoms, atomNames, bonds, bondNames, connections);
            // descriptionAliphaticChain(system, (Element)systemElement.get(0));
            break;
        }
    }

    private void descriptionAliphaticChain(RichAtomSet system,
                                           Element systemElement,
                                           Element systemAnnotation,
                                           List<Element> atoms,
                                           List<String> atomNames,
                                           List<Element> bonds,
                                           List<String> bondNames,
                                           List<Element> connections) {
        String length = SreUtil.xpathValue(systemElement, "@size");
        String content = describeName(systemElement);
        String specialBonds = getMultiBonds(system, bonds);
        List<String> nextSystems = new ArrayList<String>();
        String substitutions = getSubstitutions(system, bonds, connections, nextSystems);
        this.description.addDescription(2, "Aliphatic Chain of size " + length, 
                                        atomNames, bondNames);
    };

    
    private String getSubstitutions(RichAtomSet system, List<Element> bonds, 
                                    List<Element> connections, List<String> nextSystems) {
        return "";
    }


    private String getMultiBonds(RichAtomSet system, List<Element> bonds) {
        Map<Integer, String> bounded = new TreeMap<Integer, String>();
        for (Element bond : bonds) {
            String order = bond.getAttribute("order").getValue();
            System.out.println(order);
            
            if (order.equals("S")) {
                continue;
            }
            List<String> atoms = splitAttribute(bond.getAttribute("atomRefs2").getValue());
            Integer atomA = system.getAtomPosition(atoms.get(0));
            Integer atomB = system.getAtomPosition(atoms.get(1));
            if (atomA > atomB) {
                Integer aux = atomA;
                atomA = atomB;
                atomB = aux;
            }
            bounded.put(atomA, ((order.equals("D")) ? "Double" : "Triple") + 
                        " bond between position " + atomA + " and " + atomB + ".");
        }
        Joiner joiner = Joiner.on(" ");
        return joiner.join(bounded.values());
    }


    private List<String> descriptionAtomSetElements(String id) {
        String atoms = SreUtil.xpathValue(this.doc.getRootElement(),
                                          "//cml:atomSet[@id='" + id + "']");
        List<String> bonds = SreUtil.xpathValueList(this.annotations,
                                                    "//sre:annotation/sre:atomSet[text()='" + id + 
                                                    "']/following-sibling::sre:internalBonds/sre:bond"
                                                    );
        bonds.add(0, atoms);
        bonds.stream().forEach(System.out::println);
        return bonds;
    };

    private void descriptionTopLevel() {
        String content = describeName(this.doc.getRootElement());
        List<String> elements = SreUtil.xpathValueList(this.doc.getRootElement(), 
                                                         "//cml:atom/@id | //cml:bond/@id");
        this.description.addDescription(3, content, elements);
    }

    private String describeName(Element element) {
        return SreUtil.xpathValue(element, "//@sre:name | //@sre:iupac | //@sre:formula");
    }


    private List<String> splitAttribute(String attribute) {
        try {
            return Arrays.asList(attribute.split("\\s+"));
        } catch (PatternSyntaxException ex) {
            return new ArrayList<String>();
        }
    }
}

