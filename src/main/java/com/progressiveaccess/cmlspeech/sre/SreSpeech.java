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

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSetType;

import com.google.common.base.Joiner;

import nu.xom.Document;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Constructs the Sre speech annotations.
 */

public class SreSpeech extends SreXml {

  private Document doc;
  private RichMolecule molecule = RichStructureHelper.getRichMolecule();

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
    this.atomSet(this.molecule);

    // Describe the first level.
    for (final String structure : this.molecule.getPath()) {
      if (RichStructureHelper.isAtom(structure)) {
        this.atom(RichStructureHelper.getRichAtom(structure), this.molecule);
      } else {
        final RichAtomSet atomSet = RichStructureHelper
            .getRichAtomSet(structure);
        this.atomSet(atomSet, this.molecule);
        if (atomSet.getType() == RichSetType.FUSED) {
          for (final String subRing : atomSet.getSubSystems()) {
            final RichAtomSet subRingSet = RichStructureHelper
              .getRichAtomSet(subRing);
            this.atomSet(subRingSet, (RichFusedRing) atomSet);
          }
        }
        // TODO (sorge) Deal with FUSED rings here.
        // Describe the bottom level.
        for (final String atom : atomSet.getComponentsPositions()) {
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
    this.getAnnotations().registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAttribute(atom.longSimpleDescription(this.molecule)));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());

    Set<Connection> internalConnections = this.connectionsInContext(atom, system);
    this.toSreSet(id, SreNamespace.Tag.COMPONENT,
                  internalConnections.stream().map(conn -> conn.getConnector())
                  .collect(Collectors.toSet()));

    ComponentsPositions positions = system.getComponentsPositions();
    Integer position = positions.getPosition(id);
    this.getAnnotations().appendAnnotation(id, SreNamespace.Tag.POSITION, position.toString());

    this.describeConnections(system, atom, id);
  }

  /**
   * Computes connections of an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   *
   * @return The connections of the atom that belong to the set.
   */
  private Set<Connection> connectionsInContext(final RichAtom atom, final RichAtomSet atomSet) {
    if (!atomSet.getConnectingAtoms().contains(atom.getId())) {
      return atom.getConnections();
    }
    Set<Connection> internal = new HashSet<>();
    for (Connection connection : atom.getConnections()) {
      if (atomSet.getInternalBonds().contains(connection.getConnector())) {
        internal.add(connection);
      }
    }
    return internal;
  }


  private void atom(final RichAtom atom, final RichMolecule system) {
    final String id = atom.getId();
    this.getAnnotations().registerAnnotation(id, SreNamespace.Tag.ATOM,
        this.speechAttribute(atom.longSimpleDescription(system)));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
    this.describeConnections(system, atom, id);
  }

  // Bond to speech translation.
  private void bond(final RichBond bond) {
    final String id = bond.getId();
    this.getAnnotations().registerAnnotation(id, SreNamespace.Tag.BOND,
        this.speechAttribute(bond.shortSimpleDescription()));
    this.getAnnotations().addAttribute(id, new SreAttribute(
        SreNamespace.Attribute.ORDER, bond.orderDescription()));
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, bond.getComponents());
  }

  // AtomSet to speech translation.
  private void atomSet(final RichMolecule atomSet) {
    final String id = atomSet.getId();
    this.getAnnotations().registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    this.getAnnotations().appendAnnotation(id, SreNamespace.Tag.POSITION, "1");
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.getPath());
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(final RichAtomSet atomSet) {
    final String id = atomSet.getId();
    this.getAnnotations().registerAnnotation(id, SreNamespace.Tag.ATOMSET,
        this.speechAtomSet(atomSet));
    this.toSreSet(id, SreNamespace.Tag.PARENTS, atomSet.getSuperSystems());
    // Children are given in the order of their positions!
    this.toSreSet(id, SreNamespace.Tag.CHILDREN,
                  atomSet.getComponentsPositions());
    this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
  }

  private void atomSet(final RichAtomSet atomSet,
                       final RichAtomSet superSystem) {
    this.atomSet(atomSet);

    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private void atomSet(final RichAtomSet atomSet,
                       final RichMolecule superSystem) {
    this.atomSet(atomSet);

    String id = atomSet.getId();
    ComponentsPositions positions = superSystem.getPath();
    Integer position = positions.getPosition(id);
    this.getAnnotations().appendAnnotation(id, SreNamespace.Tag.POSITION, position.toString());

    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private void atomSet(final RichAtomSet atomSet,
                       final RichFusedRing superSystem) {
    this.atomSet(atomSet);

    String id = atomSet.getId();
    ComponentsPositions positions = superSystem.getPath();
    Integer position = positions.getPosition(id);
    this.getAnnotations().appendAnnotation(id, SreNamespace.Tag.POSITION, position.toString());

    this.describeConnections(superSystem, atomSet, atomSet.getId());
  }

  private SreAttribute speechAtomSet(final RichAtomSet atomSet) {
    String result = this.describeAtomSet(atomSet);
    switch (atomSet.getType()) {
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
    switch (atomSet.getType()) {
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
      case SMALLEST:
        return this.describeSubRing(atomSet);
      default:
        return "";
    }
  }

  private String describeMolecule(final RichAtomSet structure) {
    // TODO (sorge) Needs to be adjusted to take names into account.
    return structure.getMolecularFormula();
  }

  private String describeAliphaticChain(final RichAtomSet system) {
    return "Aliphatic chain of length " + system.getStructure().getAtomCount();
  }

  private String describeSubRing(final RichAtomSet system) {
    String descr = "Subring with " + system.getStructure().getAtomCount()
        + " elements.";
    String replacements = this.describeReplacements(system);
    return replacements == "" ? descr : descr + " " + replacements;
  }

  private String describeFusedRing(final RichAtomSet system) {
    String descr = "Fused ring system with " + system.getSubSystems().size()
        + " subrings.";
    String replacements = this.describeReplacements(system);
    return replacements == "" ? descr : descr + " " + replacements;
  }

  private String describeIsolatedRing(final RichAtomSet system) {
    String descr = "Ring with " + system.getStructure().getAtomCount()
        + " elements.";
    String replacements = this.describeReplacements(system);
    return replacements == "" ? descr : descr + " " + replacements;
  }

  private String describeFunctionalGroup(final RichAtomSet system) {
    return "Functional group " + system.getName() + ".";
  }

  private String describeReplacements(final RichAtomSet system) {
    String descr = "";
    final Iterator<String> iterator = system.iterator();
    while (iterator.hasNext()) {
      final String value = iterator.next();
      final RichAtom atom = RichStructureHelper.getRichAtom(value);
      if (!atom.isCarbon()) {
        descr += " with " + atom.shortSimpleDescription() + " at position "
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
    for (final String component : system.getComponents()) {
      if (!RichStructureHelper.isBond(component)) {
        continue;
      }
      RichBond bond = RichStructureHelper.getRichBond(component);
      if (bond.isSingle()) {
        continue;
      }
      // TODO (sorge) Make this one safer!
      final Iterator<String> atoms = bond.getComponents().iterator();
      Integer atomA = system.getPosition(atoms.next());
      Integer atomB = system.getPosition(atoms.next());
      if (atomA > atomB) {
        final Integer aux = atomA;
        atomA = atomB;
        atomB = aux;
      }
      bounded.put(atomA, bond.shortSimpleDescription() + " between position "
          + atomA + " and " + atomB + ".");
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
      if (!system.getComponentsPositions().contains(connected)) {
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
    this.getAnnotations()
      .appendAnnotation(id, SreNamespace.Tag.NEIGHBOURS, element);
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
        connSpeech = this.speechAttribute(RichStructureHelper
                                          .getRichBond(connector)
                                          .shortSimpleDescription());
        break;
      case BRIDGEATOM:
        elementSpeech = this.describeBridgeAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this.speechAttribute(RichStructureHelper
          .getRichAtom(connector).shortSimpleDescription(this.molecule));
        break;
      case SHAREDATOM:
        elementSpeech = this.describeSharedAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this.speechAttribute(RichStructureHelper
          .getRichAtom(connector).longSimpleDescription(this.molecule));
        break;
      case SPIROATOM:
        elementSpeech = this.describeSpiroAtom(connector, connected);
        connAttr = SreNamespace.Attribute.ATOM;
        connSpeech = this.speechAttribute(RichStructureHelper
          .getRichAtom(connector).shortSimpleDescription(this.molecule));
        break;
      case SHAREDBOND:
        // elementSpeech = ???
        connAttr = SreNamespace.Attribute.BOND;
        connSpeech = this.speechAttribute(RichStructureHelper
                                          .getRichBond(connector)
                                          .shortSimpleDescription());
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
    final String atom = RichStructureHelper.getRichAtom(connector).shortSimpleDescription();
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "bridge atom " + atom + " to " + structure;
  }

  private String describeSpiroAtom(final String connector,
      final String connected) {
    final String atom = RichStructureHelper.getRichAtom(connector).shortSimpleDescription();
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "spiro atom " + atom + " to " + structure;
  }

  private String describeSharedAtom(final String connector,
      final String connected) {
    final String atom = RichStructureHelper.getRichAtom(connector).shortSimpleDescription();
    final RichAtomSet connectedSet = RichStructureHelper
        .getRichAtomSet(connected);
    final String structure = this.describeAtomSet(connectedSet);
    return "shared " + atom + " atom with " + structure;
  }

  private String describeConnectingBond(final String connector,
      final String connected) {
    final String bond = RichStructureHelper.getRichBond(connector)
        .shortSimpleDescription();
    final String structure = RichStructureHelper.isAtom(connected)
        ? RichStructureHelper.getRichAtom(connected).shortSimpleDescription(this.molecule)
        : this.describeAtomSet(RichStructureHelper.getRichAtomSet(connected));
    return bond + "ed to " + structure;
  }
}
