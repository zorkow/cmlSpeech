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

public class SreSpeech extends SreXml {

  public Document doc;

  SreSpeech() {
    super();
    this.compute();
  }

  public SreSpeech(final Document document) {
    super();
    this.doc = document;
    this.compute();
  }

  @Override
  public void compute() {
    final RichMolecule molecule = RichStructureHelper.richMolecule;
    this.atomSet(molecule);

    // Describe the first level.
    for (final String structure : molecule.getPath()) {
      if (RichStructureHelper.isAtom(structure)) {
        this.atom(RichStructureHelper.getRichAtom(structure), molecule);
      } else {
        final RichAtomSet atomSet = RichStructureHelper
            .getRichAtomSet(structure);
        this.atomSet(atomSet, molecule);
        // TODO (sorge) Deal with FUSED rings here.
        // Describe the bottom level.
        for (final String atom : atomSet.componentPositions) {
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
  private SreAttribute speechAttribute(final String speech) {
    return new SreAttribute(SreNamespace.Attribute.SPEECH, speech);
  }

  // Atom to speech translation.
  private void atom(final RichAtom atom, final RichAtomSet system) {
    final String id = atom.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAtom(atom));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
    this.describeConnections(system, atom, id);
  }

  private void atom(final RichAtom atom, final RichMolecule system) {
    final String id = atom.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAtom(atom));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
    this.describeConnections(system, atom, id);
  }

  private SreAttribute speechAtom(final RichAtom atom) {
    return this.speechAttribute(this.describeAtomPosition(atom) + " "
        + this.describeHydrogenBonds(atom.getStructure()));
  }

  private String describeAtom(final RichAtom atom) {
    return AtomTable.lookup(atom.getStructure());
  }

  private String describeAtomPosition(final RichAtom atom) {
    final Integer position = RichStructureHelper.richMolecule.getPosition(atom
        .getId());
    if (position == null) {
      return this.describeAtom(atom) + " unknown position.";
    }
    return this.describeAtom(atom) + " " + position.toString();
  }

  private String describeHydrogenBonds(final IAtom atom) {
    final String hydrogens = this.describeHydrogens(atom);
    return hydrogens.equals("") ? "" : "bonded to " + hydrogens;
  }

  private String describeHydrogens(final IAtom atom) {
    final Integer count = atom.getImplicitHydrogenCount();
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
  private void bond(final RichBond bond) {
    final String id = bond.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.BOND,
        this.speechBond(bond));
    this.annotations.addAttribute(id, new SreAttribute(
        SreNamespace.Attribute.ORDER, this.describeBond(bond, false)));
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, bond.getComponents());
  }

  private SreAttribute speechBond(final RichBond bond) {
    return this.speechAttribute(this.describeBond(bond, false) + " bond");
  }

  private String describeBond(final RichBond bond, final Boolean ignoreSingle) {
    return this.describeBond(bond.getStructure(), ignoreSingle);
  }

  private String describeBond(final IBond bond, final Boolean ignoreSingle) {
    final IBond.Order order = bond.getOrder();
    if (ignoreSingle && order == IBond.Order.SINGLE) {
      return "";
    } else {
      return this.describeBondOrder(order);
    }
  }

  private String describeBondOrder(final IBond.Order bond) {
    return bond.toString().toLowerCase();
  }

  // AtomSet to speech translation.
  private void atomSet(final RichMolecule atomSet) {
    final String id = atomSet.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.getPath());
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(final RichAtomSet atomSet) {
    final String id = atomSet.getId();
    this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atomSet.getSuperSystems());
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.componentPositions);
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(final RichAtomSet atomSet, final RichAtomSet superSystem) {
    this.atomSet(atomSet);
    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private void atomSet(final RichAtomSet atomSet, final RichMolecule superSystem) {
    this.atomSet(atomSet);
    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private SreAttribute speechAtomSet(final RichAtomSet atomSet) {
    String result = this.describeAtomSet(atomSet);
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
      default:
        break;
    }
    return this.speechAttribute(result);
  }

  private String describeAtomSet(final RichAtomSet atomSet) {
    switch (atomSet.type) {
      case MOLECULE:
        return this.describeMolecule(atomSet);
      case FUSED:
        return this.describeFusedRing(atomSet);
      case ALIPHATIC:
        return this.describeAliphaticChain(atomSet);
      case ISOLATED:
        return this.describeIsolatedRing(atomSet);
      case FUNCGROUP:
        return this.describeFunctionalGroup(atomSet);
      default:
        return "";
    }
  }

  private String describeMolecule(final RichStructure<?> structure) {
    final String id = structure.getId();
    final Node element = SreUtil.xpathQueryElement(this.doc.getRootElement(),
        "//cml:atomSet[@id='" + id + "']");
    return SreUtil.xpathValue((Element) element,
        "@sre:name | @sre:iupac | @sre:formula");
  }

  private String describeAliphaticChain(final RichAtomSet system) {
    return "Aliphatic chain of length " + system.getStructure().getAtomCount();
  }

  private String describeFusedRing(final RichAtomSet system) {
    String descr = "Fused ring system with " + system.getSubSystems().size()
        + " subrings.";
    descr += " " + this.describeReplacements(system);
    return descr;
  }

  private String describeIsolatedRing(final RichAtomSet system) {
    String descr = "Ring with " + system.getStructure().getAtomCount()
        + " elements.";
    descr += " " + this.describeReplacements(system);
    return descr;
  }

  private String describeFunctionalGroup(final RichAtomSet system) {
    return "Functional group " + system.name + ".";
  }

  private String describeReplacements(final RichAtomSet system) {
    String descr = "";
    final Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      final RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        descr += " with " + this.describeAtom(atom) + " at position "
            + system.getPosition(value).toString();
      }
    }
    return descr;
  }

  private String describeSubstitutions(final RichAtomSet system) {
    final SortedSet<Integer> subst = new TreeSet<Integer>();
    for (final String atom : system.getConnectingAtoms()) {
      subst.add(system.getPosition(atom));
    }
    switch (subst.size()) {
      case 0:
        return "";
      case 1:
        return "Substitution at position " + subst.iterator().next();
      default:
        final Joiner joiner = Joiner.on(" and ");
        return "Substitutions at position " + joiner.join(subst);
    }
  }

  private String describeMultiBonds(final RichAtomSet system) {
    final Map<Integer, String> bounded = new TreeMap<Integer, String>();
    for (final IBond bond : system.getStructure().bonds()) {
      final String order = this.describeBond(bond, true);
      if (order.equals("")) {
        continue;
      }
      // TODO (sorge) Make this one safer!
      final Iterator<String> atoms = RichStructureHelper.getRichBond(bond)
          .getComponents().iterator();
      Integer atomA = system.getPosition(atoms.next());
      Integer atomB = system.getPosition(atoms.next());
      if (atomA > atomB) {
        final Integer aux = atomA;
        atomA = atomB;
        atomB = aux;
      }
      bounded.put(atomA, order + " bond between position " + atomA + " and "
          + atomB + ".");
    }
    final Joiner joiner = Joiner.on(" ");
    return joiner.join(bounded.values());
  }

  private void describeConnections(final RichAtomSet system,
      final RichChemObject block,
      final String id) {
    Integer count = 0;
    for (final Connection connection : block.getConnections()) {
      final String connected = connection.getConnected();
      if (!system.componentPositions.contains(connected)) {
        continue;
      }
      count++;
      this.describeConnection(connection, connected, id, count);
    }
  }

  private void describeConnections(final RichMolecule system,
      final RichChemObject block,
      final String id) {
    Integer count = 0;
    for (final Connection connection : block.getConnections()) {
      final String connected = connection.getConnected();
      if (!system.getPath().contains(connected)) {
        continue;
      }
      count++;
      this.describeConnection(connection, connected, id, count);
    }
  }

  private void describeConnection(final Connection connection,
      final String connected,
      final String id, final Integer count) {
    // Build the XML elements structure.
    final SreElement element = new SreElement(SreNamespace.Tag.NEIGHBOUR);
    final SreElement positions = new SreElement(SreNamespace.Tag.POSITIONS);

    // Add type depended attributes.
    this.describeConnection(connection, element, positions);

    if (RichStructureHelper.isAtom(connected)) {
      element.appendChild(new SreElement(SreNamespace.Tag.ATOM, connected));
    } else {
      element.appendChild(new SreElement(SreNamespace.Tag.ATOMSET, connected));
    }

    // Putting it all together.
    final SreElement position = new SreElement(SreNamespace.Tag.POSITION,
        count.toString());
    positions.appendChild(position);
    final SreElement via = new SreElement(SreNamespace.Tag.VIA);
    via.appendChild(positions);
    element.appendChild(via);
    this.annotations.appendAnnotation(id, SreNamespace.Tag.NEIGHBOURS, element);
  }

  private void describeConnection(final Connection connection,
      final SreElement element,
      final SreElement position) {
    final String connector = connection.getConnector();
    final String connected = connection.getConnected();

    String elementSpeech = "";
    SreAttribute connSpeech;
    SreNamespace.Attribute connAttr;
    switch (connection.getType()) {
      case CONNECTINGBOND:
        elementSpeech = this.describeConnectingBond(connector, connected);
        connAttr = SreNamespace.Attribute.BOND;
        connSpeech = this
            .speechBond(RichStructureHelper.getRichBond(connector));
        break;
      case BRIDGEATOM:
        elementSpeech = this.describeBridgeAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this
            .speechAtom(RichStructureHelper.getRichAtom(connector));
        break;
      case SHAREDATOM:
        elementSpeech = this.describeSharedAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this
            .speechAtom(RichStructureHelper.getRichAtom(connector));
        break;
      case SPIROATOM:
        elementSpeech = this.describeSpiroAtom(connector, connected);
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

  private String describeBridgeAtom(final String connector,
      final String connected) {
    final String atom = this.describeAtom(RichStructureHelper
        .getRichAtom(connector));
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "bridge atom " + atom + " to " + structure;
  }

  private String describeSpiroAtom(final String connector,
      final String connected) {
    final String atom = this.describeAtom(RichStructureHelper
        .getRichAtom(connector));
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "spiro atom " + atom + " to " + structure;
  }

  private String describeSharedAtom(final String connector,
      final String connected) {
    final String atom = this.describeAtom(RichStructureHelper
        .getRichAtom(connector));
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "shared " + atom + " atom with " + structure;
  }

  private String describeConnectingBond(final String connector,
      final String connected) {
    final String bond = this.describeBond(
        RichStructureHelper.getRichBond(connector),
        false);
    final String structure = RichStructureHelper.isAtom(connected) ? this
        .describeAtomPosition(RichStructureHelper.getRichAtom(connected))
        : this.describeAtomSet(RichStructureHelper.getRichAtomSet(connected));
        return bond + " bonded to " + structure;
  }
}
