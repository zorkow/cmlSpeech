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
 * @file   RichAtomSet.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:19:38 2015
 * 
 * @brief  Rich atom set structures.
 * 
 * 
 */

//

package io.github.egonw.structure;

import com.google.common.base.Joiner;

import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.base.CMLNameComparator;
import io.github.egonw.base.Logger;
import io.github.egonw.graph.StructuralGraph;
import io.github.egonw.sre.SreElement;
import io.github.egonw.sre.SreNamespace;
import io.github.egonw.sre.SreUtil;

import nu.xom.Document;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Base class for all atom sets with admin information.
 */

public abstract class RichAtomSet extends RichChemObject implements RichSet {

  public RichSetType type;
  public CMLAtomSet cml;

  public String iupac = "";
  public String name = "";
  public String molecularFormula = "";
  public String structuralFormula = "";

  private SortedSet<String> connectingAtoms = new TreeSet<String>(
      new CMLNameComparator());

  public ComponentsPositions componentPositions = new ComponentsPositions();
  public Integer offset = 0;

  public RichAtomSet(IAtomContainer container, String id, RichSetType type) {
    super(container);
    this.type = type;
    this.getStructure().setID(id);
    for (IAtom atom : this.getStructure().atoms()) {
      this.getComponents().add(atom.getID());
    }
    for (IBond bond : this.getStructure().bonds()) {
      this.getComponents().add(bond.getID());
    }
    this.makeCML();
  }

  @Override
  public IAtomContainer getStructure() {
    return (IAtomContainer) this.structure;
  }

  @Override
  public RichSetType getType() {
    return this.type;
  }

  @Override
  public SortedSet<String> getConnectingAtoms() {
    return this.connectingAtoms;
  }

  /**
   * Walks the structure and computes the positions of its elements,
   * substructures etc.
   */
  protected abstract void walk();

  /**
   * Returns a list with two elements that are the connected atoms that lie on
   * the rim of the ring.
   * 
   * @param atom
   * 
   * @return List of connected atoms.
   */
  protected List<IAtom> getConnectedAtomsList(IAtom atom) {
    return this.getStructure().getConnectedAtomsList(atom);
  }

  protected final void walkStraight(IAtom atom) {
    this.walkStraight(atom, new ArrayList<IAtom>());
  }

  protected final void walkStraight(IAtom atom, List<IAtom> visited) {
    if (visited.contains(atom)) {
      return;
    }
    this.componentPositions.addNext(atom.getID());
    visited.add(atom);
    for (IAtom connected : this.getConnectedAtomsList(atom)) {
      if (!visited.contains(connected)) {
        walkStraight(connected, visited);
        return;
      }
    }
  }

  /**
   * Depth first traversal of structure.
   * 
   * @param atom
   *          The start atom.
   */
  protected final void walkDepthFirst(IAtom atom) {
    if (atom == null) {
      return;
    }
    List<IAtom> visited = new ArrayList<IAtom>();
    Stack<IAtom> frontier = new Stack<IAtom>();
    frontier.push(atom);
    while (!frontier.empty()) {
      IAtom current = frontier.pop();
      if (visited.contains(current)) {
        continue;
      }
      visited.add(current);
      this.componentPositions.addNext(current.getID());
      this.getConnectedAtomsList(current).stream()
          .forEach(a -> frontier.push(a));
    }
  }

  @Override
  public String getAtom(Integer position) {
    return this.componentPositions.getAtom(position);
  }

  @Override
  public Integer getPosition(String atom) {
    return this.componentPositions.getPosition(atom);
  }

  @Override
  public Iterator<String> iterator() {
    return componentPositions.iterator();
  }

  public void printPositions() {
    Logger.logging(this.getId() + "\n" + componentPositions.toString());
  }

  public final List<String> orderedAtomNames() {
    if (this.componentPositions.size() == 0) {
      return null;
    }
    List<String> result = new ArrayList<>();
    while (result.size() < this.getStructure().getAtomCount()) {
      result.add("");
    }
    for (IAtom atom : this.getStructure().atoms()) {
      String id = atom.getID();
      result.set(this.getPosition(id) - 1, atom.getSymbol());
    }
    return result;
  }

  @Override
  public String toString() {
    String structure = super.toString();
    Joiner joiner = Joiner.on(" ");
    return structure + "\nSuper Systems:" + joiner.join(this.getSuperSystems())
        + "\nSub Systems:" + joiner.join(this.getSubSystems())
        + "\nConnecting Atoms:" + joiner.join(this.getConnectingAtoms());
  }

  private void makeCML() {
    this.cml = new CMLAtomSet();
    this.cml.setTitle(this.type.name);
    this.cml.setId(this.getId());
  }

  // This should only ever be called once!
  // Need a better solution!
  @Override
  public CMLAtomSet getCML(Document doc) {
    for (IAtom atom : this.getStructure().atoms()) {
      String atomId = atom.getID();
      CMLAtom node = (CMLAtom) SreUtil.getElementById(doc, atomId);
      this.cml.addAtom(node);
    }
    return this.cml;
  }

  public boolean isRing() {
    return false;
  }

  public void visualize() {
    StructuralGraph graph = new StructuralGraph(this.getSubSystems());
    graph.visualize(this.getId());
  }

  @Override
  public SreNamespace.Tag tag() {
    return SreNamespace.Tag.ATOMSET;
  }

  @Override
  public SreElement annotation() {
    SreElement element = super.annotation();
    element.appendChild(SreUtil.sreSet(SreNamespace.Tag.INTERNALBONDS,
        this.getComponents().stream().filter(RichStructureHelper::isBond)
            .collect(Collectors.toSet())));
    element.appendChild(SreUtil.sreSet(SreNamespace.Tag.SUBSYSTEM,
        this.getSubSystems()));
    element.appendChild(SreUtil.sreSet(SreNamespace.Tag.SUPERSYSTEM,
        this.getSuperSystems()));
    element.appendChild(SreUtil.sreSet(SreNamespace.Tag.CONNECTINGATOMS,
        this.getConnectingAtoms()));
    return element;
  }

}
