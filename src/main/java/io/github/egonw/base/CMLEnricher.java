// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


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
import io.github.egonw.analysis.RichStructureHelper;
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
import io.github.egonw.structure.RichSetType;

import nu.xom.Document;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.xmlcml.cml.element.CMLAtomSet;

import java.util.List;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import nu.xom.ParsingException;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * The basic loop for semantically enriching chemical diagrams.
 */

public class CMLEnricher {
    public StructuralAnalysis analysis;
    
    private Document doc;
    private IAtomContainer molecule;
    private SreOutput sreOutput;
    private SreSpeech sreSpeech;
    private CactusExecutor executor = new CactusExecutor();
    private StructuralFormula formula = new StructuralFormula();

    /**
     * Constructor
     * @return The newly created object.
     */
    public CMLEnricher() {
    }

    /**
     * Convenience method to enrich a CML file. Does all the error catching.
     *
     * @param fileName
     *            File to enrich.
     */
    public void enrichFile(String fileName) {
        this.loadMolecule(fileName);
        if (Cli.hasOption("c")) {
            try {
                FileHandler.writeFile(this.doc, fileName, "simple");
            } catch (IOException e) {
                Logger.error("IO error: Can't write " + fileName + "\n");
                e.printStackTrace();
            } catch (CDKException e) {
                Logger.error("Not a valid CDK structure to write: " +
                             e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
        this.analyseMolecule();
        this.nameMolecule();
        this.annotateMolecule();
        doc.getRootElement().
            addNamespaceDeclaration(SreNamespace.getInstance().prefix,
                                    SreNamespace.getInstance().uri);
        if (Cli.hasOption("annonly")) {
            this.removeNonAnnotations();
        }
        try {
            FileHandler.writeFile(this.doc, fileName, "enr");
        } catch (IOException e) {
            Logger.error("IO error: Can't write enriched file " + fileName + "\n");
            e.printStackTrace();
        } catch (CDKException e) {
            Logger.error("Not a valid CDK structure to write: " +
                         e.getMessage() + "\n");
            e.printStackTrace();
        }
        if (Cli.hasOption("vis")) {
            this.analysis.visualize();
        }
    }


    /**
     * Loads a molecule and initiates the CML document.
     *
     * @param fileName
     */
    public void loadMolecule(String fileName) {
        try {
            this.molecule = FileHandler.readFile(fileName);
            this.doc = FileHandler.buildXOM(this.molecule);
        } catch (IOException | CDKException | ParsingException e) {
            Logger.error("IO error: " + e.getMessage() +
                         " Can't load file " + fileName + "\n");
            e.printStackTrace();
            System.exit(0);
        }
    }


    /**
     * Runs the analysis of the molecule.
     */
    public void analyseMolecule() {
        this.removeExplicitHydrogens();
        this.analysis = new StructuralAnalysis(this.molecule);
        RichStructureHelper.getAtomSets().forEach(RichAtomSet::printPositions);
    }


    /**
     * Names the molecule and its components and inserts the results in the CML
     * document.
     */
    public void nameMolecule() {
        MolecularFormula.set(RichStructureHelper.getAtomSets());
        this.appendAtomSets();
        if (!Cli.hasOption("nonih")) {
            executor.execute();
            executor.addResults(this.doc);
            executor.shutdown();
        }
        if (Cli.hasOption("sf")){
            String structuralFormula =
                this.formula.getStructuralFormula(Cli.hasOption("sub"));
            System.out.println(structuralFormula);
        }
    }


    /**
     * Appends annotations to the CML document.
     */
    public void annotateMolecule() {
        if (Cli.hasOption("ann")) {
            this.sreOutput = new SreOutput();
            this.doc.getRootElement().
                appendChild(this.sreOutput.getAnnotations());
            }
        if (Cli.hasOption("descr")) {
            this.sreSpeech = new SreSpeech(this.analysis, this.doc);
            this.doc.getRootElement().
                appendChild(this.sreSpeech.getAnnotations());
        }
    }


    /**
     * Removes explicit hydrogens from the CML representation.
     */
    private void removeExplicitHydrogens() {
        //  TODO (sorge) These should be reattached at the end!
        this.molecule = AtomContainerManipulator.removeHydrogens(this.molecule);
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
        List<RichAtomSet> richSets = RichStructureHelper.getAtomSets();
        for (RichAtomSet richSet : richSets) {
            CMLAtomSet set = richSet.getCML(this.doc);
            // this.atomSets.add(richSet);
            this.doc.getRootElement().appendChild(set);
            set.addAttribute(new SreAttribute("formula", richSet.molecularFormula));
            if (richSet.getType() == RichSetType.FUNCGROUP) {
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
        Logger.logging("Registering calls for " + id + "\n");
        IAtomContainer newcontainer = checkedClone(container);
        if (newcontainer != null) {
            this.executor.register(new CactusCallable(id, Cactus.Type.IUPAC,
                    newcontainer));
            this.executor.register(new CactusCallable(id, Cactus.Type.NAME,
                    newcontainer));
        }
    }


    private void removeNonAnnotations() {
        Element root = this.doc.getRootElement();
        Elements elements = root.getChildElements();
        for (Integer i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (!element.getLocalName().equals("annotations")) {
                root.removeChild(element);
            }
        }
    }

}
