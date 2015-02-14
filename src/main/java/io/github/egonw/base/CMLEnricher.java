/**
 * @file   CMLEnricher.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Apr 28 01:41:35 2014
 * 
 * @brief  Main class to enrich CML files.
 * 
 * 
 */


//
package io.github.egonw.base;

import io.github.egonw.analysis.MolecularFormula;
import io.github.egonw.analysis.StructuralAnalysis;
import io.github.egonw.analysis.StructuralFormula;
import io.github.egonw.cactus.Cactus;
import io.github.egonw.cactus.CactusCallable;
import io.github.egonw.cactus.CactusExecutor;
import io.github.egonw.sre.SreAttribute;
import io.github.egonw.sre.SreNamespace;
import io.github.egonw.sre.SreOutput;
import io.github.egonw.sre.SreSpeech;
import io.github.egonw.structure.RichAtomSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nux.xom.pool.XOMUtil;

import org.apache.commons.io.FilenameUtils;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtomSet;

/**
 * The basic loop for semantically enriching chemical diagrams.
 */

public class CMLEnricher {
    private StructuralAnalysis analysis;
    private SreOutput sreOutput;
    private SreSpeech sreSpeech;

    private Document doc;
    private IAtomContainer molecule;
    private CactusExecutor executor = new CactusExecutor();
    private StructuralFormula formula = new StructuralFormula();

    /**
     * Constructor
     * @return The newly created object.
     */
    public CMLEnricher() {
    }

    /**
     * Enriches all CML files given as input arguments.
     * 
     */
    public void enrichFiles() {
        for (String file : Cli.getFiles()) {
            enrichFile(file);
        }
    }

    /**
     * Convenience method to enrich a CML file. Does all the error catching.
     * 
     * @param fileName
     *            File to enrich.
     */
    private void enrichFile(String fileName) {
        try {
            readFile(fileName);
            buildXOM();
            if (Cli.hasOption("c")) {
                writeFile(fileName, "simple");
            }
            removeExplicitHydrogens();

            this.analysis = new StructuralAnalysis(this.molecule);
            this.sreOutput = new SreOutput(this.analysis);
            this.analysis.computePositions();
            // TODO (sorge): Write to logger
            this.analysis.printPositions();

            MolecularFormula.set(this.analysis.getAtomSets());
            this.appendAtomSets();
            if (Cli.hasOption("ann")) {
                this.sreOutput = new SreOutput(this.analysis);
                this.doc.getRootElement().appendChild(
                        this.sreOutput.getAnnotations());
            }
            if (!Cli.hasOption("nonih")) {
                executor.execute();
                executor.addResults(this.doc);
                executor.shutdown();
            }
            if (Cli.hasOption("descr")) {
                this.sreSpeech = new SreSpeech(this.analysis, this.doc);
                this.doc.getRootElement().appendChild(this.sreSpeech.getAnnotations());
            }
            if (Cli.hasOption("sf")){
            	String structuralFormula =
                    this.formula.getStructuralFormula(this.analysis,
                                                      Cli.hasOption("sub"));
            	System.out.println(structuralFormula);
            }
            writeFile(fileName, "enr");
        } catch (Exception e) {
            // TODO (sorge) Meaningful exception handling by
            // exceptions/functions.
            Logger.error("Something went wrong when parsing File "
                    + fileName + ":" + e.getMessage() + "\n");
            e.printStackTrace();
            return;
        }
        if (Cli.hasOption("vis")) {
            this.analysis.visualize();
        }
    }

    /**
     * Loads current file into the molecule IAtomContainer.
     * 
     * @param fileName
     *            File to load.
     * 
     * @throws IOException
     *             Problems with loading file.
     * @throws CDKException
     *             Problems with CML file format.
     */
    private void readFile(String fileName) throws IOException, CDKException {
        InputStream file = new BufferedInputStream(
                new FileInputStream(fileName));
        ISimpleChemObjectReader reader = new ReaderFactory().createReader(file);
        IChemFile cFile = null;
        cFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(
                IChemFile.class));
        reader.close();
        this.molecule = ChemFileManipulator.getAllAtomContainers(cFile).get(0);
        Logger.logging(this.molecule);
    }

    /**
     * Build the CML XOM element.
     * 
     * @throws IOException
     *             Problems with StringWriter
     * @throws CDKException
     *             Problems with CMLWriter
     * @throws ParsingException
     *             Problems with building CML XOM.
     */
    private void buildXOM() throws IOException, CDKException, ParsingException {
        StringWriter outStr = new StringWriter();
        CMLWriter cmlwriter = new CMLWriter(outStr);
        cmlwriter.write(this.molecule);
        cmlwriter.close();
        String cmlcode = outStr.toString();

        Builder builder = new CMLBuilder();
        // this.doc.getRootElement().addNamespaceDeclaration
        // ("cml", "http://www.xml-cml.org/schema");
        this.doc = builder.build(cmlcode, "");
        Logger.logging(this.doc.toXML());
    }

    private void removeExplicitHydrogens() {
        this.molecule = AtomContainerManipulator.removeHydrogens(this.molecule);
    }

    /**
     * Writes current document to a CML file.
     * 
     * @param fileName
     * @param extension
     *
     * @throws IOException
     *             Problems with opening output file.
     * @throws CDKException
     *             Problems with writing the CML XOM.
     */
    private void writeFile(String fileName, String extension) throws IOException, CDKException {
        String basename = FilenameUtils.getBaseName(fileName);
        OutputStream outFile = new BufferedOutputStream(new FileOutputStream(
                basename + "-" + extension + ".cml"));
        PrintWriter output = new PrintWriter(outFile);
        this.doc.getRootElement().addNamespaceDeclaration(
                SreNamespace.getInstance().prefix,
                SreNamespace.getInstance().uri);
        output.write(XOMUtil.toPrettyXML(this.doc));
        output.flush();
        output.close();
    }

    /**
     * Creates a deep clone of an atom container catching possible errors.
     * 
     * @param container
     *            The container to be cloned.
     * @return The cloned container. Possibly null if cloning failed!
     */
    private IAtomContainer checkedClone(IAtomContainer container) {
        IAtomContainer newcontainer = null;
        try {
            newcontainer = container.clone();
            AtomContainerManipulator
                    .percieveAtomTypesAndConfigureAtoms(newcontainer);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
                    .addImplicitHydrogens(newcontainer);
        } catch (CloneNotSupportedException e) {
            Logger.error("Something went wrong cloning atom container: "
                    + e.getMessage());
        } catch (Throwable e) {
            Logger.error("Error " + e.getMessage());
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
            // this.atomSets.add(richSet);
            this.doc.getRootElement().appendChild(set);
            set.addAttribute(new SreAttribute("formula", richSet.molecularFormula));
            if (richSet.getType() == RichAtomSet.Type.FUNCGROUP) {
                set.addAttribute(new SreAttribute("name", richSet.name));
            } else {
                nameMolecule(richSet.getId(), richSet.getStructure());
            }
        }
    }

    /**
     * Computes some names for a molecule by registering calls to Cactus.
     * 
     * @param id
     *            The id of the atom set.
     * @param container
     *            The molecule to be named.
     */
    private void nameMolecule(String id, IAtomContainer container) {
        // TODO (sorge) catch the right exception.
        Logger.logging("Registering calls for " + id);
        IAtomContainer newcontainer = checkedClone(container);
        if (newcontainer != null) {
            this.executor.register(new CactusCallable(id, Cactus.Type.IUPAC,
                    newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.NAME,
                    newcontainer));
        }
    }

}
