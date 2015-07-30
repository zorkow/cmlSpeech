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
 * @file StructureVisitor.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date Sat Apr 25 23:36:58 2015
 *
 * @brief Visitor to construct the exploration structure.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import com.progressiveaccess.cmlspeech.connection.Bridge;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.connection.ConnectionComparator;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;
import com.progressiveaccess.cmlspeech.structure.RichSuperSet;

import com.google.common.collect.TreeMultimap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Constructs the exploration structure.
 */

public class StructureVisitor implements XmlVisitor {

  private final TreeMultimap<String, SreElement> annotations =
      TreeMultimap.create(new CmlNameComparator(), new SreComparator());
  private SreElement element = null;
  private RichAtomSet context = null;
  private ComponentsPositions positions = null;
  private final TypeVisitor typeVisitor = new TypeVisitor();
  private final SpeechVisitor expertSpeechVisitor =
      Language.getExpertSpeechVisitor();
  private final SpeechVisitor simpleSpeechVisitor =
      Language.getSimpleSpeechVisitor();
  private boolean internal = false;


  /**
   * Dummy comparator for the tree multi map.
   */
  private class SreComparator implements Comparator<SreElement> {

    @Override
    public int compare(final SreElement element1, final SreElement element2) {
      return 1;
    }
  }


  /**
   * @return The annotation the visitor computes.
   */
  public SreElement getAnnotations() {
    final SreElement annotation = new SreElement(SreNamespace.Tag.ANNOTATIONS);
    for (final String key : this.annotations.keySet()) {
      for (final SreElement value : this.annotations.get(key)) {
        annotation.appendChild(value);
      }
    }
    return annotation;
  }


  @Override
  public void visit(final RichAtom atom) {
    for (final String parent : atom.getSuperSystems()) {
      this.context = RichStructureHelper.getRichAtomSet(parent);
      this.atomStructure(atom);
    }
  }


  @Override
  public void visit(final RichBond bond) {
    this.context = null;
    this.addStructure(bond);
    this.addComponents(bond.getComponents());
    this.addSpeech(bond);
    this.element.appendChild(new SreElement(SreNamespace.Tag.NEIGHBOURS));
  }


  @Override
  public void visit(final RichIsolatedRing ring) {
    this.atomSetStructure(ring);
  }


  @Override
  public void visit(final RichFusedRing ring) {
    this.atomSetStructure(ring);
  }


  @Override
  public void visit(final RichSubRing ring) {
    this.atomSetStructure(ring);
  }


  @Override
  public void visit(final RichAliphaticChain chain) {
    this.atomSetStructure(chain);
  }


  @Override
  public void visit(final RichFunctionalGroup group) {
    this.atomSetStructure(group);
  }


  @Override
  public void visit(final RichMolecule molecule) {
    this.atomSetStructure(molecule);
  }


  @Override
  public void visit(final SpiroAtom spiroAtom) {
    this.makeConnection(spiroAtom);
  }


  @Override
  public void visit(final BridgeAtom bridgeAtom) {
    this.element = this.makeVia(bridgeAtom,
        this.positions.getPosition(bridgeAtom.getConnector()));
  }


  @Override
  public void visit(final ConnectingBond bond) {
    this.element = this.makeNeighbour(bond.getConnected(),
        this.makeVia(bond,
            this.positions.getPosition(this.internal
                ? bond.getConnected() : bond.getOrigin())));
    this.addSpeech(bond);
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.makeConnection(sharedAtom);
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.element = this.makeVia(sharedBond,
        this.positions.getPosition(sharedBond.getConnector()));
  }


  @Override
  public void visit(final Bridge bridge) {
    List<SreElement> vias = new ArrayList<SreElement>();
    for (final Connection connection : bridge.getBridges()) {
      connection.accept(this);
      vias.add(this.element);
    }
    this.element = this.makeNeighbour(bridge.getConnected(), vias);
    this.addSpeech(bridge);
  }


  /**
   * Computes structure for an atom set in the context given context.
   *
   * @param set
   *          The rich atom set.
   */
  private void atomSetStructure(final RichAtomSet set) {
    this.context = RichStructureHelper.getRichAtomSet(
        set.getSuperSystems().iterator().next());
    this.positions = ((RichSuperSet) this.context).getPath();
    this.addStructure(set);
    this.addComponents(set.getComponents());
    this.addSpeech(set);
    final SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    this.element.appendChild(connElement);
    this.element = connElement;
    final SortedSet<Connection> internalConnections =
        this.connectionsInContext(set);
    this.context = set;
    this.positions = this.context.getComponentsPositions();
    for (final Connection connection : internalConnections) {
      connection.accept(this);
      connElement.appendChild(this.element);
    }
  }


  /**
   * Computes structure for an atom set in the context given context.
   *
   * @param set
   *          The rich atom set.
   */
  private void atomSetStructure(final RichMolecule set) {
    this.context = null;
    this.addStructure(set);
    this.addComponents(set.getComponents());
    final SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    this.element.appendChild(connElement);
    this.addSpeech(set);
  }


  /**
   * Computes structure for an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   */
  private void atomStructure(final RichAtom atom) {
    this.positions = RichStructureHelper.isMolecule(this.context.getId())
        ? ((RichMolecule) this.context).getPath()
        : this.context.getComponentsPositions();
    this.addStructure(atom);
    this.addSpeech(atom);
    final SortedSet<Connection> internalConnections = this.bondsInContext(atom);
    this.addComponents(internalConnections.stream()
                       .map(conn -> conn.getConnector())
                       .collect(Collectors.toSet()));
    final SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    this.element.appendChild(connElement);
    this.internal = true;
    for (Connection connection : atom.getConnections()) {
      connection.accept(this);
      connElement.appendChild(this.element);
      this.element.addAttribute(new SreAttribute(
          SreNamespace.Attribute.LOCATION,
          internalConnections.contains(connection) ? "internal" : "external"));
    }
    this.internal = false;
  }


  /**
   * Creates an annotation element and adds the structural components.
   *
   * @param structure
   *          A rich chemical object.
   */
  private void addStructure(final RichChemObject structure) {
    final String id = structure.getId();
    this.element = new SreElement(SreNamespace.Tag.ANNOTATION);
    this.annotations.put(id, this.element);
    final SreElement structureElement = new SreElement(structure.tag(), id);
    this.element.appendChild(structureElement);
    structure.accept(this.typeVisitor);
    this.addTypeAttribute(structureElement);
    final SreElement parent = new SreElement(SreNamespace.Tag.PARENTS);
    this.element.appendChild(parent);
    Integer position = 1;
    if (this.context != null) {
      position = this.positions.getPosition(id);
      parent.appendChild(SreUtil.sreElement(this.context.getId()));
    }
    this.element.appendChild(new SreElement(SreNamespace.Tag.POSITION,
        position.toString()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CHILDREN,
        structure.getSubSystems()));
  }


  /**
   * Adds the components of a structure to the global element.
   *
   * @param components
   *          Set of component elements to be added.
   */
  private void addComponents(final Set<String> components) {
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.COMPONENT,
        components));
  }


  /**
   * Creates a neighbour element for connections on the block level.
   *
   * @param connection
   *          The block connection.
   */
  private void makeConnection(final Connection connection) {
    this.element = this.makeNeighbour(connection.getConnected(),
        this.makeVia(connection,
            this.positions.getPosition(connection.getConnector())));
    this.addSpeech(connection);
  }


  /**
   * Creates a via element.
   *
   * @param via
   *          The connector.
   * @param position
   *          The position of the connected element in the current context.
   *
   * @return The newly create via element.
   */
  private SreElement makeVia(final Connection via, final Integer position) {
    final SreElement viaElement = new SreElement(SreNamespace.Tag.VIA);
    final SreElement connectorElement = SreUtil.sreElement(via.getConnector());
    ((RichChemObject) RichStructureHelper.getRichStructure(via.getConnector()))
    .accept(this.typeVisitor);
    this.addTypeAttribute(connectorElement);
    viaElement.appendChild(connectorElement);
    viaElement.appendChild(new SreElement(SreNamespace.Tag.POSITION,
        position == null ? "0" : position.toString()));
    via.accept(this.typeVisitor);
    this.addTypeAttribute(viaElement);
    return viaElement;
  }


  /**
   * Creates a neighbour element.
   *
   * @param neighbour
   *          The connected element.
   *
   * @return The newly create neighbour element.
   */
  private SreElement makeNeighbour(final String neighbour) {
    final SreElement newElement = new SreElement(SreNamespace.Tag.NEIGHBOUR);
    final SreElement neighbourElement = SreUtil.sreElement(neighbour);
    ((RichChemObject) RichStructureHelper.getRichStructure(neighbour))
    .accept(this.typeVisitor);
    this.addTypeAttribute(neighbourElement);
    newElement.appendChild(neighbourElement);
    return newElement;
  }


  /**
   * Creates a neighbour element.
   *
   * @param neighbour
   *          The connected element.
   * @param via
   *          The via element representing how the element is connected.
   *
   * @return The newly create neighbour element.
   */
  private SreElement makeNeighbour(final String neighbour,
      final SreElement via) {
    final SreElement newElement = this.makeNeighbour(neighbour);
    newElement.appendChild(via);
    return newElement;
  }


  /**
   * Creates a neighbour element.
   *
   * @param neighbour
   *          The connector.
   * @param vias
   *          The list of via elements representing how the element is
   *          connected.
   *
   * @return The newly create neighbour element.
   */
  private SreElement makeNeighbour(final String neighbour,
      final List<SreElement> vias) {
    final SreElement newElement = this.makeNeighbour(neighbour);
    vias.stream().forEach(newElement::appendChild);
    return newElement;
  }


  /**
   * Computes connections of an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   *
   * @return The connections of the atom that belong to the set.
   */
  private SortedSet<Connection> bondsInContext(final RichAtom atom) {
    if (!this.context.getConnectingAtoms().contains(atom.getId())) {
      return atom.getConnections();
    }
    final SortedSet<Connection> internalConnection =
        new TreeSet<>(new ConnectionComparator());
    for (final Connection connection : atom.getConnections()) {
      if (this.context.getInternalBonds().contains(connection.getConnector())) {
        internalConnection.add(connection);
      }
    }
    return internalConnection;
  }


  /**
   * Computes connections of an atom in the context of a set.
   *
   * @param set
   *          The rich atom set.
   *
   * @return The connections of the atom that belong to the set.
   */
  private SortedSet<Connection> connectionsInContext(final RichAtomSet set) {
    final SortedSet<Connection> internalConnection =
        new TreeSet<>(new ConnectionComparator());
    for (final Connection connection : set.getConnections()) {
      if (this.positions.contains(connection.getConnected())) {
        internalConnection.add(connection);
      }
    }
    return internalConnection;
  }


  /**
   * Adds a computed type attribute to the given element.
   *
   * @param structure
   *          The structural element.
   */
  private void addTypeAttribute(final SreElement structure) {
    structure.addAttribute(
        new SreAttribute(SreNamespace.Attribute.TYPE,
            this.typeVisitor.getType()));
  }


  /**
   * Adds a computed speech attribute to the given element.
   *
   * @param structure
   *          The structural element.
   */
  private void addSpeechAttribute(final SreElement structure) {
    structure.addAttribute(
        new SreAttribute(SreNamespace.Attribute.SPEECH,
            this.expertSpeechVisitor.getSpeech()));
  }


  /**
   * Adds a computed simple speech attribute to the given element.
   *
   * @param structure
   *          The structural element.
   */
  private void addSimpleSpeechAttribute(final SreElement structure) {
    String speech = this.simpleSpeechVisitor.getSpeech();
    if (speech != "") {
      structure.addAttribute(
          new SreAttribute(SreNamespace.Attribute.SPEECH2,
                           speech));
    }
  }


  /**
   * Adds the speech attributes for a structure or connection.
   *
   * @param visitable
   *          The visitable object to describe.
   */
  private void addSpeech(final XmlVisitable visitable) {
    if (Cli.hasOption("r")) {
      this.expertSpeechVisitor.setContextPositions(this.positions);
      visitable.accept(this.expertSpeechVisitor);
      this.addSpeechAttribute(this.element);
    }
    if (Cli.hasOption("r0")) {
      this.simpleSpeechVisitor.setContextPositions(this.positions);
      visitable.accept(this.simpleSpeechVisitor);
      this.addSimpleSpeechAttribute(this.element);
    }
  }

}
