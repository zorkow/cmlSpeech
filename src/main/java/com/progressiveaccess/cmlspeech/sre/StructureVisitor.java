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
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichChemObject;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;
import com.progressiveaccess.cmlspeech.structure.RichSuperSet;

import com.google.common.collect.TreeMultimap;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    this.makeConnection(bridgeAtom);
  }


  @Override
  public void visit(final ConnectingBond bond) {
    this.element = this.makeNeighbour(bond.getConnected(),
            this.makeVia(bond.getConnector(),
                this.positions.getPosition(bond.getOrigin())));
  }


  @Override
  public void visit(final SharedAtom sharedAtom) {
    this.makeConnection(sharedAtom);
  }


  @Override
  public void visit(final SharedBond sharedBond) {
    this.makeConnection(sharedBond);
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
    final SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    this.element.appendChild(connElement);
    this.element = connElement;
    final Set<Connection> internalConnections = this.connectionsInContext(set);
    for (final Connection connection : internalConnections) {
      this.context = set;
      this.positions = this.context.getComponentsPositions();
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
  }


  /**
   * Computes structure for an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   */
  private void atomStructure(final RichAtom atom) {
    this.positions = this.context.getComponentsPositions();
    this.addStructure(atom);
    final Set<Connection> internalConnections = this.bondsInContext(atom);
    this.addComponents(internalConnections.stream()
        .map(conn -> conn.getConnector())
        .collect(Collectors.toSet()));
    final SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    this.element.appendChild(connElement);
    this.element = connElement;
    final Integer position = this.positions.getPosition(atom.getId());
    if (position > 1) {
      this.addConnectingBond(internalConnections, position - 1);
    }
    if (position < this.positions.size()) {
      this.addConnectingBond(internalConnections, position + 1);
    }
  }


  /**
   * Adds connecting bond element as neighbour of an atom.
   *
   * @param connections
   *          The set of internal connections.
   * @param position
   *          Neighbour position to add.
   */
  private void addConnectingBond(final Set<Connection> connections,
      final Integer position) {
    connections.stream()
        .filter(c -> c.getConnected() == this.positions.get(position))
        .forEach(c -> {
            final SreElement neighbour =
                this.makeNeighbour(c.getConnected(),
                      this.makeVia(c.getConnector(),
                      this.positions.getPosition(c.getConnected())));
            this.element.appendChild(neighbour);
          });
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
    this.element.appendChild(new SreElement(structure.tag(), id));
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
            this.makeVia(connection.getConnector(),
                this.positions.getPosition(connection.getConnector())));
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
  private SreElement makeVia(final String via, final Integer position) {
    final SreElement viaElement = new SreElement(SreNamespace.Tag.VIA);
    viaElement.appendChild(SreUtil.sreElement(via));
    viaElement.appendChild(new SreElement(SreNamespace.Tag.POSITION,
        position == null ? "0" : position.toString()));
    return viaElement;
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
    final SreElement newElement = new SreElement(SreNamespace.Tag.NEIGHBOUR);
    newElement.appendChild(SreUtil.sreElement(neighbour));
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
    final SreElement newElement = new SreElement(SreNamespace.Tag.NEIGHBOUR);
    newElement.appendChild(SreUtil.sreElement(neighbour));
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
  private Set<Connection> bondsInContext(final RichAtom atom) {
    if (!this.context.getConnectingAtoms().contains(atom.getId())) {
      return atom.getConnections();
    }
    final Set<Connection> internal = new HashSet<>();
    for (final Connection connection : atom.getConnections()) {
      if (this.context.getInternalBonds().contains(connection.getConnector())) {
        internal.add(connection);
      }
    }
    return internal;
  }


  /**
   * Computes connections of an atom in the context of a set.
   *
   * @param set
   *          The rich atom set.
   *
   * @return The connections of the atom that belong to the set.
   */
  private Set<Connection> connectionsInContext(final RichAtomSet set) {
    final Set<Connection> internal = new HashSet<>();
    for (final Connection connection : set.getConnections()) {
      if (this.positions.contains(connection.getConnected())) {
        internal.add(connection);
      }
    }
    return internal;
  }

}
