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

    private StructuralAnalysis analysis;
    private SreOutput sreOutput;

    private Document doc;
    private IAtomContainer molecule;
    private CactusExecutor executor = new CactusExecutor();
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
        try {
            readFile(fileName);
            buildXOM();
            removeExplicitHydrogens();
            nameMolecule(this.doc.getRootElement().getAttribute("id").getValue(), this.molecule);

            this.analysis = new StructuralAnalysis(this.molecule, this.cli, this.logger);
            this.sreOutput = new SreOutput(this.analysis);
            getAbstractionGraph();
            this.analysis.majorPath(this.structure);
            this.analysis.computePositions();
            this.analysis.printPositions();

            this.appendAtomSets();
            if (this.cli.cl.hasOption("ann")) {
                this.doc.getRootElement().appendChild(this.sreOutput.getAnnotations());
            }
            if (!this.cli.cl.hasOption("nonih")) {
                executor.execute();
                executor.addResults(this.doc, this.logger);
                executor.shutdown();
            }
            this.sreOutput.computeDescriptions(this.doc);
            this.doc.getRootElement().appendChild(this.sreOutput.getDescriptions());
            writeFile(fileName);
        } catch (Exception e) { 
            // TODO (sorge) Meaningful exception handling by exceptions/functions.
            this.logger.error("Something went wrong when parsing File " + fileName +
                              ":" + e.getMessage() + "\n");
            e.printStackTrace();
            return;
        }
        if (this.cli.cl.hasOption("vis")) {
            this.structure.visualize(this.analysis.getMajorSystems(), 
                                     this.analysis.getSingletonAtoms());
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


    private void removeExplicitHydrogens() {
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
     * Append the Atom Sets from the structural analysis to the CML documents.
     */
    private void appendAtomSets() {
        List<RichAtomSet> richSets = this.analysis.getAtomSets();
        for (RichAtomSet richSet : richSets) {
            CMLAtomSet set = richSet.getCML(this.doc);
            //            this.atomSets.add(richSet);
            this.doc.getRootElement().appendChild(set);
            nameMolecule(richSet.getId(), richSet.getStructure());
        }
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
        List<RichAtomSet> majorSystems = this.analysis.getMajorSystems();
        List<RichAtom> singletonAtoms = this.analysis.getSingletonAtoms();
        List<String> msNames = majorSystems.stream()
            .map(RichAtomSet::getId)
            .collect(Collectors.toList());
        msNames.addAll(singletonAtoms.stream()
                       .map(RichAtom::getId)
                       .collect(Collectors.toList()));
        msNames.stream().forEach(ms -> this.structure.addVertex(ms));
        List<RichStructure> combined = new ArrayList<RichStructure>(majorSystems);
        combined.addAll(singletonAtoms);

        for (RichStructure ms : combined) {
            Set<Connection> connections = ms.getConnections();
            if (!connections.isEmpty()) {
                addSingleEdges(ms.getId(), connections, msNames);
            }
        }
    };


    private void addSingleEdges(String source, Set<Connection> connections, List<String> systems) {
        for (Connection connection : connections) {
            if (systems.contains(connection.getConnected())) {
                this.structure.addEdge(source, connection.getConnected(),
                                       connection.getConnector());
            }
        }
    }
    

}

