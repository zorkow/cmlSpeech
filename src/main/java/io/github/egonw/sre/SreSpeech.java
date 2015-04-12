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
 * @file   SreSpeech.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:33:18 2015
 * 
 * @brief  Sre speech output.
 * 
 * 
 */

//

package io.github.egonw.sre;

import com.google.common.base.Joiner;

import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.connection.Connection;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichBond;
import io.github.egonw.structure.RichChemObject;
import io.github.egonw.structure.RichMolecule;
import io.github.egonw.structure.RichStructure;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Constructs the Sre speech annotations.
 */

public class SreSpeech extends SreXML {

  public Document doc;

  SreSpeech() {
    super();
    this.compute();
  }

  public SreSpeech(Document document) {
    super();
    this.doc = document;
    this.compute();
  }

  @Override
  public void compute() {
    RichMolecule molecule = RichStructureHelper.richMolecule;
    this.atomSet(molecule);

    // Describe the first level.
    for (String structure : molecule.getPath()) {
      if (RichStructureHelper.isAtom(structure)) {
        this.atom(RichStructureHelper.getRichAtom(structure), molecule);
      } else {
        RichAtomSet atomSet = RichStructureHelper.getRichAtomSet(structure);
        this.atomSet(atomSet, molecule);
        // TODO (sorge) Deal with FUSED rings here.
        // Describe the bottom level.
        for (String atom : atomSet.componentPositions) {
          this.atom(RichStructureHelper.getRichAtom(atom), atomSet);
        }
      }
    }

    // Finally add the bonds.
    RichStructureHelper.getBonds().stream().forEach(this::bond);
  }

  /**
   * Turns a speech string into an attribute.
   * 
   * @param speech
   *          The speech string.
   * 
   * @return The newly created attribute.
   */
  private SreAttribute speechAttribute(String speech) {
    return new SreAttribute(SreNamespace.Attribute.SPEECH, speech);
  }

  // Atom to speech translation.
  private void atom(RichAtom atom, RichAtomSet system) {
    String id = atom.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAtom(atom));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
    this.describeConnections(system, atom, id);
  }

  private void atom(RichAtom atom, RichMolecule system) {
    String id = atom.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAtom(atom));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
    this.describeConnections(system, atom, id);
  }

  private SreAttribute speechAtom(RichAtom atom) {
    return speechAttribute(describeAtomPosition(atom) + " "
        + this.describeHydrogenBonds(atom.getStructure()));
  }

  private String describeAtom(RichAtom atom) {
    return AtomTable.lookup(atom.getStructure());
  }

  private String describeAtomPosition(RichAtom atom) {
    Integer position = RichStructureHelper.richMolecule.getPosition(atom
        .getId());
    if (position == null) {
      return describeAtom(atom) + " unknown position.";
    }
    return describeAtom(atom) + " " + position.toString();
  }

  private String describeHydrogenBonds(IAtom atom) {
    String hydrogens = describeHydrogens(atom);
    return hydrogens.equals("") ? "" : "bonded to " + hydrogens;
  }

  private String describeHydrogens(IAtom atom) {
    Integer count = atom.getImplicitHydrogenCount();
    switch (count) {
      case 0:
        return "";
      case 1:
        return count.toString() + " hydrogen";
      default:
        return count.toString() + " hydrogens";
    }
  }

  // Bond to speech translation.
  private void bond(RichBond bond) {
    String id = bond.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.BOND,
        this.speechBond(bond));
    this.annotations.addAttribute(id, new SreAttribute(
        SreNamespace.Attribute.ORDER, this.describeBond(bond, false)));
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, bond.getComponents());
  }

  private SreAttribute speechBond(RichBond bond) {
    return speechAttribute(this.describeBond(bond, false) + " bond");
  }

  private String describeBond(RichBond bond, Boolean ignoreSingle) {
    return describeBond(bond.getStructure(), ignoreSingle);
  }

  private String describeBond(IBond bond, Boolean ignoreSingle) {
    IBond.Order order = bond.getOrder();
    if (ignoreSingle && order == IBond.Order.SINGLE) {
      return "";
    } else {
      return describeBondOrder(order);
    }
  }

  private String describeBondOrder(IBond.Order bond) {
    return bond.toString().toLowerCase();
  }

  // AtomSet to speech translation.
  private void atomSet(RichMolecule atomSet) {
    String id = atomSet.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.getPath());
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(RichAtomSet atomSet) {
    String id = atomSet.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atomSet.getSuperSystems());
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.componentPositions);
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(RichAtomSet atomSet, RichAtomSet superSystem) {
    atomSet(atomSet);
    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private void atomSet(RichAtomSet atomSet, RichMolecule superSystem) {
    atomSet(atomSet);
    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private SreAttribute speechAtomSet(RichAtomSet atomSet) {
    String result = describeAtomSet(atomSet);
    switch (atomSet.type) {
      case MOLECULE:
        break;
      case ALIPHATIC:
      case ISOLATED:
        result += " " + this.describeMultiBonds(atomSet);
        result += " " + this.describeSubstitutions(atomSet);
        break;
      case FUSED:
      case FUNCGROUP:
      case SMALLEST:
        break;
    }
    return speechAttribute(result);
  }

  private String describeAtomSet(RichAtomSet atomSet) {
    switch (atomSet.type) {
      case MOLECULE:
        return describeMolecule(atomSet);
      case FUSED:
        return describeFusedRing(atomSet);
      case ALIPHATIC:
        return describeAliphaticChain(atomSet);
      case ISOLATED:
        return describeIsolatedRing(atomSet);
      case FUNCGROUP:
        return describeFunctionalGroup(atomSet);
      default:
        return "";
    }
  }

  private String describeMolecule(RichStructure<?> structure) {
    String id = structure.getId();
    Node element = SreUtil.xpathQueryElement(this.doc.getRootElement(),
        "//cml:atomSet[@id='" + id + "']");
    return SreUtil.xpathValue((Element) element,
        "@sre:name | @sre:iupac | @sre:formula");
  }

  private String describeAliphaticChain(RichAtomSet system) {
    return "Aliphatic chain of length " + system.getStructure().getAtomCount();
  }

  private String describeFusedRing(RichAtomSet system) {
    String descr = "Fused ring system with " + system.getSubSystems().size()
        + " subrings.";
    descr += " " + this.describeReplacements(system);
    return descr;
  }

  private String describeIsolatedRing(RichAtomSet system) {
    String descr = "Ring with " + system.getStructure().getAtomCount()
        + " elements.";
    descr += " " + this.describeReplacements(system);
    return descr;
  }

  private String describeFunctionalGroup(RichAtomSet system) {
    return "Functional group " + system.name + ".";
  }

  private String describeReplacements(RichAtomSet system) {
    String descr = "";
    Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      String value = iterator.next();
      RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        descr += " with " + this.describeAtom(atom) + " at position "
            + system.getPosition(value).toString();
      }
    }
    return descr;
  }

  private String describeSubstitutions(RichAtomSet system) {
    SortedSet<Integer> subst = new TreeSet<Integer>();
    for (String atom : system.getConnectingAtoms()) {
      subst.add(system.getPosition(atom));
    }
    switch (subst.size()) {
      case 0:
        return "";
      case 1:
        return "Substitution at position " + subst.iterator().next();
      default:
        Joiner joiner = Joiner.on(" and ");
        return "Substitutions at position " + joiner.join(subst);
    }
  }

  private String describeMultiBonds(RichAtomSet system) {
    Map<Integer, String> bounded = new TreeMap<Integer, String>();
    for (IBond bond : system.getStructure().bonds()) {
      String order = this.describeBond(bond, true);
      if (order.equals("")) {
        continue;
      }
      // TODO (sorge) Make this one safer!
      Iterator<String> atoms = RichStructureHelper.getRichBond(bond)
          .getComponents().iterator();
      Integer atomA = system.getPosition(atoms.next());
      Integer atomB = system.getPosition(atoms.next());
      if (atomA > atomB) {
        Integer aux = atomA;
        atomA = atomB;
        atomB = aux;
      }
      bounded.put(atomA, order + " bond between position " + atomA + " and "
          + atomB + ".");
    }
    Joiner joiner = Joiner.on(" ");
    return joiner.join(bounded.values());
  }

  private void describeConnections(RichAtomSet system, RichChemObject block,
      String id) {
    Integer count = 0;
    for (Connection connection : block.getConnections()) {
      String connected = connection.getConnected();
      if (!system.componentPositions.contains(connected)) {
        continue;
      }
      count++;
      this.describeConnection(connection, connected, id, count);
    }
  }

  private void describeConnections(RichMolecule system, RichChemObject block,
      String id) {
    Integer count = 0;
    for (Connection connection : block.getConnections()) {
      String connected = connection.getConnected();
      if (!system.getPath().contains(connected)) {
        continue;
      }
      count++;
      this.describeConnection(connection, connected, id, count);
    }
  }

  private void describeConnection(Connection connection, String connected,
      String id, Integer count) {
    // Build the XML elements structure.
    SreElement element = new SreElement(SreNamespace.Tag.NEIGHBOUR);
    SreElement positions = new SreElement(SreNamespace.Tag.POSITIONS);

    // Add type depended attributes.
    describeConnection(connection, element, positions);

    if (RichStructureHelper.isAtom(connected)) {
      element.appendChild(new SreElement(SreNamespace.Tag.ATOM, connected));
    } else {
      element.appendChild(new SreElement(SreNamespace.Tag.ATOMSET, connected));
    }

    // Putting it all together.
    SreElement position = new SreElement(SreNamespace.Tag.POSITION,
        count.toString());
    positions.appendChild(position);
    SreElement via = new SreElement(SreNamespace.Tag.VIA);
    via.appendChild(positions);
    element.appendChild(via);
    this.annotations.appendAnnotation(id, SreNamespace.Tag.NEIGHBOURS, element);
  }

  private void describeConnection(Connection connection, SreElement element,
      SreElement position) {
    String connector = connection.getConnector();
    String connected = connection.getConnected();

    String elementSpeech = "";
    SreAttribute connSpeech;
    SreNamespace.Attribute connAttr;
    switch (connection.getType()) {
      case CONNECTINGBOND:
        elementSpeech = describeConnectingBond(connector, connected);
        connAttr = SreNamespace.Attribute.BOND;
        connSpeech = this
            .speechBond(RichStructureHelper.getRichBond(connector));
        break;
      case BRIDGEATOM:
        elementSpeech = describeBridgeAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this
            .speechAtom(RichStructureHelper.getRichAtom(connector));
        break;
      case SHAREDATOM:
        elementSpeech = describeSharedAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this
            .speechAtom(RichStructureHelper.getRichAtom(connector));
        break;
      case SPIROATOM:
        elementSpeech = describeSpiroAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this
            .speechAtom(RichStructureHelper.getRichAtom(connector));
        break;
      case SHAREDBOND:
        // elementSpeech = ???
        connAttr = SreNamespace.Attribute.BOND;
        connSpeech = this
            .speechBond(RichStructureHelper.getRichBond(connector));
        break;
      default:
        throw (new SreException("Unknown connection type in structure."));
    }
    element.addAttribute(this.speechAttribute(elementSpeech));
    position.addAttribute(new SreAttribute(connAttr, connector));
    position.addAttribute(connSpeech);
    position.addAttribute(new SreAttribute(SreNamespace.Attribute.TYPE,
        connection.getType().toString().toLowerCase()));
  }

  private String describeBridgeAtom(String connector, String connected) {
    String atom = this.describeAtom(RichStructureHelper.getRichAtom(connector));
    RichAtomSet connectedSet = RichStructureHelper.getRichAtomSet(connected);
    String structure = this.describeAtomSet(connectedSet);
    return "bridge atom " + atom + " to " + structure;
  }

  private String describeSpiroAtom(String connector, String connected) {
    String atom = this.describeAtom(RichStructureHelper.getRichAtom(connector));
    RichAtomSet connectedSet = RichStructureHelper.getRichAtomSet(connected);
    String structure = this.describeAtomSet(connectedSet);
    return "spiro atom " + atom + " to " + structure;
  }

  private String describeSharedAtom(String connector, String connected) {
    String atom = this.describeAtom(RichStructureHelper.getRichAtom(connector));
    RichAtomSet connectedSet = RichStructureHelper.getRichAtomSet(connected);
    String structure = this.describeAtomSet(connectedSet);
    return "shared " + atom + " atom with " + structure;
  }

  private String describeConnectingBond(String connector, String connected) {
    String bond = this.describeBond(RichStructureHelper.getRichBond(connector),
        false);
    String structure = RichStructureHelper.isAtom(connected) ? this
        .describeAtomPosition(RichStructureHelper.getRichAtom(connected))
        : this.describeAtomSet(RichStructureHelper.getRichAtomSet(connected));
    return bond + " bonded to " + structure;
  }
}
