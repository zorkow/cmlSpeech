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
import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor;
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
import nu.xom.Attribute;
import nu.xom.Node;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import java.util.Collection;
import org.openscience.cdk.interfaces.IBond;
import java.util.Set;
import java.util.stream.Collectors;


public class CMLEnricher {
    private final Cli cli;
    private final Logger logger;

    private Document doc;
    private IAtomContainer molecule;
    private int atomSetCount;
    private List<RichAtomSet> atomSets = new ArrayList<RichAtomSet>();
    private CactusExecutor executor = new CactusExecutor();
    private SreAnnotations annotations;



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
        this.annotations = new SreAnnotations();
        try {
            readFile(fileName);
            buildXOM();
            enrichCML();
            this.atomSets.stream().forEach(this::finalizeAtomSet);
            nameMolecule(this.doc.getRootElement().getAttribute("id").getValue(), this.molecule);
            this.annotations.finalize();
            this.doc.getRootElement().appendChild(this.annotations);
            executor.execute();
            executor.addResults(this.doc, this.logger);
            writeFile(fileName);
            executor.shutdown();
        } catch (Exception e) { 
            // TODO: Meaningful exception handling by exceptions/functions.
            this.logger.error("Something went wrong when parsing File " + fileName +
                              ":" + e.getMessage() + "\n");
            e.printStackTrace();
            return;
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
        this.doc = builder.build(cmlcode, "");
        this.logger.logging(this.doc.toXML());
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
        IAtomContainer chain = getAliphaticChain();
        if (chain != null) {
            this.logger.logging(chain);
            RichAtomSet set = new RichAtomSet(chain, RichAtomSet.Type.ISOLATED);
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
    private IAtomContainer getAliphaticChain() {
        IAtomContainer container = checkedClone(this.molecule);
        if (container == null) { return null; }
        LongestAliphaticChain chain = new LongestAliphaticChain();
        DescriptorValue descr = chain.calculate(container);
        IAtomContainer result = chain.extract();
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
        }
    }


    // public void hee (args) {
        
    // }

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
        // TODO: catch the right exception.
        this.logger.logging("Registering calls for " + id);
        IAtomContainer newcontainer = checkedClone(container);
        if (newcontainer != null) {
            this.executor.register(new CactusCallable(id, Cactus.Type.IUPAC, newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.NAME, newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.FORMULA, newcontainer));
        }
    }

}
