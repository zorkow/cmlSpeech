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
     * Convenience method to enrich a CML file. Does all the error catching.
     * 
     * @param fileName
     *            File to enrich.
     */
    public void enrichFile(String fileName) {
        try {
            this.molecule = FileHandler.readFile(fileName);
            this.doc = FileHandler.buildXOM(this.molecule);
            if (Cli.hasOption("c")) {
                FileHandler.writeFile(this.doc, fileName, "simple");
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
            doc.getRootElement().
                addNamespaceDeclaration(SreNamespace.getInstance().prefix,
                                        SreNamespace.getInstance().uri);
            FileHandler.writeFile(this.doc, fileName, "enr");
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

    private void removeExplicitHydrogens() {
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
        List<RichAtomSet> richSets = this.analysis.getAtomSets();
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

}
