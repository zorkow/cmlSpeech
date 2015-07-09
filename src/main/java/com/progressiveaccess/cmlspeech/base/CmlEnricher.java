// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file CMLEnricher.java
 * @author Volker Sorge <a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Mon Apr 28 01:41:35 2014
 *
 * @brief Main class to enrich CML files.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.base;

import com.progressiveaccess.cmlspeech.analysis.MolecularFormula;
import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.analysis.StructuralAnalysis;
import com.progressiveaccess.cmlspeech.analysis.StructuralFormula;
import com.progressiveaccess.cmlspeech.cactus.CactusCallable;
import com.progressiveaccess.cmlspeech.cactus.CactusExecutor;
import com.progressiveaccess.cmlspeech.cactus.CactusType;
import com.progressiveaccess.cmlspeech.sre.SreNamespace;
import com.progressiveaccess.cmlspeech.sre.SreOutput;
import com.progressiveaccess.cmlspeech.sre.SreStructure;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichSetType;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.xmlcml.cml.element.CMLAtomSet;

import java.io.IOException;
import java.util.List;
import com.progressiveaccess.cmlspeech.sre.SreElement;
import com.progressiveaccess.cmlspeech.sre.SreUtil;
import nu.xom.Node;
import nu.xom.Nodes;
import java.util.stream.Collectors;
import com.progressiveaccess.cmlspeech.sre.SreAttribute;
import java.util.Set;
import java.util.ArrayList;

/**
 * The basic loop for semantically enriching chemical diagrams.
 */

public class CmlEnricher {

  private Document doc;
  private IAtomContainer molecule;
  private final CactusExecutor executor = new CactusExecutor();


  /**
   * Convenience method to enrich a CML file. Does all the error catching.
   *
   * @param fileName
   *          File to enrich.
   */
  public void enrichFile(final String fileName) {
    this.loadMolecule(fileName);
    if (Cli.hasOption("c")) {
      try {
        FileHandler.writeFile(this.doc, fileName, "simple");
      } catch (final IOException e) {
        Logger.error("IO error: Can't write " + fileName + "\n");
        e.printStackTrace();
      } catch (final CDKException e) {
        Logger.error("Not a valid CDK structure to write: " + e.getMessage()
            + "\n");
        e.printStackTrace();
      }
    }
    this.analyseMolecule();
    this.nameAtomSets();
    this.appendAtomSets();
    this.annotateMolecule();
    this.doc.getRootElement().addNamespaceDeclaration(
        SreNamespace.getInstance().getPrefix(),
        SreNamespace.getInstance().getUri());
    if (Cli.hasOption("annonly")) {
      this.removeNonAnnotations();
    }
    try {
      FileHandler.writeFile(this.doc, fileName, "enr");
    } catch (final IOException e) {
      Logger.error("IO error: Can't write enriched file " + fileName + "\n");
      e.printStackTrace();
    } catch (final CDKException e) {
      Logger.error("Not a valid CDK structure to write: " + e.getMessage()
          + "\n");
      e.printStackTrace();
    }
    if (Cli.hasOption("vis")) {
      if (Cli.hasOption("vis_recursive")) {
        RichStructureHelper.getAtomSets().stream().forEach(a -> a.visualize());
      } else {
        RichStructureHelper.getRichMolecule().visualize();
      }
    }
  }


  /**
   * Loads a molecule and initiates the CML document.
   *
   * @param fileName
   *          The input filename.
   */
  public void loadMolecule(final String fileName) {
    try {
      this.molecule = FileHandler.readFile(fileName);
      this.doc = FileHandler.buildXom(this.molecule);
    } catch (IOException | CDKException | ParsingException e) {
      Logger.error("IO error: " + e.getMessage() + " Can't load file "
          + fileName + "\n");
      e.printStackTrace();
      System.exit(0);
    }
  }


  /**
   * Runs the analysis of the molecule.
   */
  public void analyseMolecule() {
    this.removeExplicitHydrogens();
    new StructuralAnalysis(this.molecule);
    RichStructureHelper.getAtomSets().forEach(RichAtomSet::printPositions);
  }


  /**
   * Names the molecule and its components and inserts the results in the CML
   * document.
   */
  public void nameAtomSets() {
    RichStructureHelper.getAtomSets().stream().forEach(this::nameAtomSet);
    MolecularFormula.set(RichStructureHelper.getAtomSets());
    if (!Cli.hasOption("no_nih")) {
      this.executor.execute();
      this.executor.addResults(this.doc);
      this.executor.shutdown();
    }
    new StructuralFormula();
  }


  /**
   * Computes some names for a molecule by registering calls to Cactus.
   *
   * @param set
   *          The rich atom set.
   */
  private void nameAtomSet(final RichAtomSet set) {
    final IAtomContainer newcontainer = this.checkedClone(set.getStructure());
    if (newcontainer != null) {
      this.executor.register(new CactusCallable(set.getId(),
          (final String name) -> {
          set.setIupac(name);
        },
          CactusType.IUPAC, newcontainer));
      if (set.getType() != RichSetType.FUNCGROUP) {
        this.executor.register(new CactusCallable(set.getId(),
            (final String name) -> {
            set.setName(name);
          },
            CactusType.NAME, newcontainer));
      }
    }
  }


  /**
   * Appends annotations to the CML document.
   */
  public void annotateMolecule() {
    if (Cli.hasOption("ann")) {
      SreOutput sreOutput = new SreOutput();
      this.doc.getRootElement().appendChild(sreOutput.getAnnotations());
    }
    if (Cli.hasOption("struct")) {
      SreStructure sreStructure = new SreStructure();
      SreElement annotation = sreStructure.getAnnotations();
      this.reattachHydrogens(annotation);
      this.doc.getRootElement().appendChild(annotation);
    }
  }


  /**
   * Removes explicit hydrogens from the CML representation.
   */
  private void removeExplicitHydrogens() {
    // TODO (sorge) These should be reattached at the end!
    this.molecule = AtomContainerManipulator.removeHydrogens(this.molecule);
  }


  /**
   * Creates a deep clone of an atom container catching possible errors.
   *
   * @param container
   *          The container to be cloned.
   * @return The cloned container. Possibly null if cloning failed!
   */
  private IAtomContainer checkedClone(final IAtomContainer container) {
    IAtomContainer newcontainer = null;
    try {
      newcontainer = container.clone();
      AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newcontainer);
      CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
          .addImplicitHydrogens(newcontainer);
    } catch (final CloneNotSupportedException e) {
      Logger.error("Something went wrong cloning atom container: "
          + e.getMessage());
    } catch (final Throwable e) {
      Logger.error("Error " + e.getMessage());
    }
    return newcontainer;
  }


  /**
   * Append the Atom Sets from the structural analysis to the CML documents.
   */
  private void appendAtomSets() {
    final List<RichAtomSet> richSets = RichStructureHelper.getAtomSets();
    for (final RichAtomSet richSet : richSets) {
      final CMLAtomSet set = richSet.getCml(this.doc);
      this.doc.getRootElement().appendChild(set);
    }
  }


  /**
   * Removes regular CML from the document.
   */
  private void removeNonAnnotations() {
    final Element root = this.doc.getRootElement();
    final Elements elements = root.getChildElements();
    for (Integer i = 0; i < elements.size(); i++) {
      final Element element = elements.get(i);
      if (!element.getLocalName().equals("annotations")) {
        root.removeChild(element);
      }
    }
  }


  private void reattachHydrogens(SreElement annotation) {
    // Get hydrogen bonds.
    //    Compare bonds in RichStructures with all those in the CML doc.
    // If none, quit.
    // Add the bonds to overall structure.
    // Add the bonds components of the non-hydrogen atom.
    // Add a dummy node for the hydrogen atoms.
    List<String> atoms = RichStructureHelper.getAtoms().stream()
        .map(a -> a.getId()).collect(Collectors.toList());
    List<String> bonds = RichStructureHelper.getBonds().stream()
        .map(b -> b.getId()).collect(Collectors.toList());
    Nodes nodes = SreUtil.xpathQuery(this.doc.getRootElement(), "//cml:bond");
    System.out.println(nodes.size());
    Integer size = 0;
    while (size < nodes.size()) {
      Element bond = (Element) nodes.get(size);
      size++;
      String bondId = bond.getAttributeValue("id");
      if (bonds.contains(bondId)) {
        continue;
      }
      String refs = bond.getAttributeValue("atomRefs2");
      String[] parts = refs.split(" ");
      if (parts.length < 2) {
        continue;
      }
      String atomA = parts[0];
      String atomB = parts[1];
      if (atoms.contains(atomA)) {
        if (atoms.contains(atomB)) {
          continue;
        }
        this.insertHydrogenBond(bondId, atomB, atomA, annotation);
        atoms.add(atomB);
      } else {
        this.insertHydrogenBond(bondId, atomA, atomB, annotation);
        atoms.add(atomA);
      }
      bonds.add(bondId);
      System.out.println(bondId + ": " + atomA + " " + atomB);
    }
  }

  private void insertHydrogenBond(String bond, String hydrogen, String atom,
                                  SreElement annotation) {
    this.insertBond(bond, hydrogen, atom, annotation);
    Nodes nodes = SreUtil.xpathQuery(annotation,
        "//sre:annotation/sre:atom[.='" + atom + "']/..");
    System.out.println("Hydrogen " + hydrogen);
    Integer size = 0;
    List<String> atoms = new ArrayList<String>();
    while (size < nodes.size()) {
      SreElement atomElement = (SreElement) nodes.get(size);
      String parent = SreUtil.xpathValue(atomElement,
                                         "./sre:parents/sre:atomSet");
      System.out.println(parent);
      System.out.println(nodes.get(size).toXML());
      this.addBond(atomElement, bond);
      this.insertHydrogen(bond, hydrogen, parent, annotation);
      if (!atoms.contains(atom)) {
        this.addToContext(bond, hydrogen, atom, annotation);
        atoms.add(atom);
      }
      size++;
    }
  }

  private void addToContext(String bond, String hydrogen, String atom,
                            SreElement annotation) {
    Set<String> contexts = RichStructureHelper.getRichAtom(atom).getContexts();
    for (String context : contexts) {
      if (!RichStructureHelper.isAtomSet(context)) {
        continue;
      }
      System.out.println("Context: " + context);
      SreElement node = (SreElement) SreUtil.xpathQueryElement(annotation,
                                       "//sre:annotation/sre:atomSet[.='" + context + "']/..");
      System.out.println(node.toXML());
      SreElement component = (SreElement) SreUtil.xpathQueryElement(node,
                                                    "./sre:component");
      System.out.println(component.toXML());
      component.appendChild(new SreElement(SreNamespace.Tag.BOND, bond));
      component.appendChild(new SreElement(SreNamespace.Tag.ATOM, hydrogen));
    }
  }

  
  private void addBond(SreElement atom, String bond) {
    Element component = (Element) SreUtil.xpathQueryElement(atom,
                                                            "./sre:component");
    component.appendChild(new SreElement(SreNamespace.Tag.BOND, bond));
  }
  
  private void insertBond(String bond, String hydrogen, String atom,
                                  SreElement annotation) {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATION);
    element.appendChild(new SreElement(SreNamespace.Tag.BOND, bond));
    element.appendChild(new SreElement(SreNamespace.Tag.PARENTS));
    element.appendChild(new SreElement(SreNamespace.Tag.POSITION, "1"));
    SreElement component = new SreElement(SreNamespace.Tag.COMPONENT);
    component.appendChild(new SreElement(SreNamespace.Tag.ATOM, hydrogen));
    component.appendChild(new SreElement(SreNamespace.Tag.ATOM, atom));
    element.appendChild(component);
    element.appendChild(new SreElement(SreNamespace.Tag.NEIGHBOURS));
    annotation.appendChild(element);
  }
  
  private void insertHydrogen(String bond, String hydrogen, String parent,
                                  SreElement annotation) {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATION);
    SreElement atom = new SreElement(SreNamespace.Tag.ATOM, hydrogen);
    atom.addAttribute(new SreAttribute(SreNamespace.Attribute.TYPE,
                                       "Hydrogen"));
    element.appendChild(atom);
    SreElement parents = new SreElement(SreNamespace.Tag.PARENTS);
    parents.appendChild(new SreElement(SreNamespace.Tag.ATOMSET, parent));
    element.appendChild(parents);
    element.appendChild(new SreElement(SreNamespace.Tag.POSITION, "0"));
    SreElement component = new SreElement(SreNamespace.Tag.COMPONENT);
    component.appendChild(new SreElement(SreNamespace.Tag.BOND, bond));
    element.appendChild(component);
    element.appendChild(new SreElement(SreNamespace.Tag.NEIGHBOURS));
    annotation.appendChild(element);
  }
  
}
