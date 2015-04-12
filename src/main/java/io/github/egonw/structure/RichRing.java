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
 * @file   RichRing.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Fri Mar 20 21:57:45 2015
 * 
 * @brief  An abstract class for ring structures.
 * 
 * 
 */

//

package io.github.egonw.structure;

import java.io.IOException;

import org.openscience.cdk.tools.AtomicProperties;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Comparator;
import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.analysis.WeightComparator;
import io.github.egonw.connection.Connection;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import java.util.SortedSet;
import com.google.common.collect.Lists;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import io.github.egonw.connection.ConnectionType;
import org.openscience.cdk.interfaces.IBond;
import java.util.stream.Collectors;

/**
 * Abstract class for ring structures.
 */
public abstract class RichRing extends RichAtomSet {

  protected Set<IAtom> rim = null;

  public RichRing(IAtomContainer container, String id, RichSetType type) {
    super(container, id, type);
    this.rim = Sets.newHashSet(container.atoms());
  }

  @Override
  public final boolean isRing() {
    return true;
  }

  public final Set<IAtom> getRim() {
    return this.rim;
  }

  protected class InternalSubstComparator implements Comparator<IAtom> {
    public int compare(IAtom atom1, IAtom atom2) {
      String symbol1 = atom1.getSymbol();
      String symbol2 = atom2.getSymbol();
      if (symbol1 == "O") {
        return -1;
      }
      if (symbol2 == "O") {
        return 1;
      }
      try {
        double weightA = AtomicProperties.getInstance().getMass(symbol1);
        double weightB = AtomicProperties.getInstance().getMass(symbol2);
        return (int) Math.signum(weightB - weightA);
      } catch (IOException e) {
        return 0;
      }
    }
  }

  // TODO (sorge) This comparator contains a lot of redundancy. This could be
  // simplified.
  protected class ExternalSubstComparator implements Comparator<Connection> {
    private WeightComparator weightCompare = new WeightComparator();

    public int compare(Connection con1, Connection con2) {
      RichStructure<?> connected1 = RichStructureHelper.getRichStructure(con1
          .getConnected());
      RichStructure<?> connected2 = RichStructureHelper.getRichStructure(con2
          .getConnected());
      if (this.isHydroxylGroup(connected1)) {
        return -1;
      }
      if (this.isHydroxylGroup(connected2)) {
        return 1;
      }
      return weightCompare.compare(connected1, connected2);
    }

    private boolean isHydroxylGroup(RichStructure<?> structure) {
      if (!RichStructureHelper.isAtomSet(structure.getId())) {
        return false;
      }
      IAtomContainer container = ((RichAtomSet) structure).getStructure();
      if (container.getAtomCount() == 1) {
        return this.isHydroxyl(container.getAtom(0));
      }
      if (container.getAtomCount() == 2) {
        if (RichRing.this.getComponents()
            .contains(container.getAtom(0).getID())) {
          return this.isHydroxyl(container.getAtom(1));
        }
        if (RichRing.this.getComponents()
            .contains(container.getAtom(1).getID())) {
          return this.isHydroxyl(container.getAtom(0));
        }
        return false;
      }
      return false;
    }

    private boolean isHydroxyl(IAtom atom) {
      return atom.getSymbol() == "O" && atom.getImplicitHydrogenCount() == 1;
    }
  }

  protected void walk() {
    List<IAtom> internalSubst = this.getInternalSubsts();
    if (internalSubst.size() > 1) {
      this.walkOnSubst(internalSubst.get(0), internalSubst);
      return;
    }
    this.getExternalConnections();
    if (this.externalConnections.size() == 0) {
      if (internalSubst.size() == 1) {
        this.walkStraight(internalSubst.get(0));
        return;
      }
      this.walkTrivial();
      return;
    }
    List<IAtom> externalSubst = this.getExternalSubsts();
    if (internalSubst.size() == 0) {
      if (externalSubst.size() == 1) {
        this.walkStraight(externalSubst.get(0));
        return;
      }
      this.walkOnSubst(externalSubst.get(0), externalSubst);
      return;
    }
    this.walkOnSubst(internalSubst.get(0), externalSubst);
  }

  /**
   * Walking a ring without any substitutions. This is necessary to keep stable
   * tests as the choice of first element is non-deterministic.
   */
  private void walkTrivial() {
    // Choose lexicographically smallest start atom.
    SortedSet<String> components = this.getComponents();
    String smallestName = components.first();
    IAtom startAtom = Lists.newArrayList(this.getStructure().atoms()).stream()
        .filter(x -> x.getID().equals(smallestName)).findFirst().get();
    List<IAtom> connected = this.getConnectedAtomsList(startAtom);
    IAtom nextLeft = connected.get(0);
    IAtom nextRight = connected.get(1);
    List<IAtom> path = new ArrayList<IAtom>();
    path.add(startAtom);
    if (components.headSet(nextLeft.getID()).size() <= components.headSet(
        nextRight.getID()).size()) {
      this.walkFinalise(nextLeft, path);
    } else {
      this.walkFinalise(nextRight, path);
    }
  }

  private void walkOnSubst(IAtom startAtom, List<IAtom> reference) {
    List<IAtom> queueLeft = new ArrayList<>();
    List<IAtom> queueRight = new ArrayList<>();
    queueLeft.add(startAtom);
    queueRight.add(startAtom);
    List<IAtom> connected = this.getConnectedAtomsList(startAtom);
    IAtom nextLeft = connected.get(0);
    IAtom nextRight = connected.get(1);
    while (nextLeft != null && nextRight != null) {
      boolean containsLeft = reference.contains(nextLeft);
      boolean containsRight = reference.contains(nextRight);
      if (containsLeft && containsRight) {
        if (reference.indexOf(nextLeft) <= reference.indexOf(nextRight)) {
          this.walkFinalise(nextLeft, queueLeft);
          return;
        }
        this.walkFinalise(nextRight, queueRight);
        return;
      }
      if (containsLeft) {
        this.walkFinalise(nextLeft, queueLeft);
        return;
      }
      if (containsRight) {
        this.walkFinalise(nextRight, queueRight);
        return;
      }
      nextLeft = chooseNext(queueLeft, nextLeft);
      nextRight = chooseNext(queueRight, nextRight);
    }
  }

  private void walkFinalise(IAtom endAtom, List<IAtom> path) {
    for (IAtom atom : path) {
      this.componentPositions.addNext(atom.getID());
    }
    this.walkStraight(endAtom, path);
  }

  /**
   * Finds the next atom in the ring that has not yet been visited.
   * 
   * @param atom
   * 
   * @return
   */
  private IAtom chooseNext(List<IAtom> visited, IAtom atom) {
    visited.add(atom);
    List<IAtom> connected = this.getConnectedAtomsList(atom);
    if (!visited.contains(connected.get(0))) {
      return connected.get(0);
    }
    if (visited.size() > 1 && !visited.contains(connected.get(1))) {
      return connected.get(1);
    }
    return null;
  }

  private static Boolean isCarbon(IAtom atom) {
    return atom.getSymbol().equals("C");
  }

  // TODO (sorge) Eventually this needs to be rewritten to work with the rim.
  private List<IAtom> getInternalSubsts() {
    Queue<IAtom> substs = new PriorityQueue<>(new InternalSubstComparator());
    for (IAtom atom : this.getStructure().atoms()) {
      if (!RichRing.isCarbon(atom)) {
        substs.add(atom);
      }
    }
    List<IAtom> result = new ArrayList<>();
    while (substs.peek() != null) {
      result.add(substs.poll());
    }
    return result;
  }

  private Queue<Connection> externalConnections = new PriorityQueue<>(
      new ExternalSubstComparator());

  private void getExternalConnections() {
    for (Connection connection : this.getConnections()) {
      ConnectionType type = connection.getType();
      if (type == ConnectionType.SPIROATOM || type == ConnectionType.SHAREDATOM
          || type == ConnectionType.CONNECTINGBOND) {
        this.externalConnections.add(connection);
      }
    }

  }

  private List<IAtom> getExternalSubsts() {
    List<IAtom> result = new ArrayList<>();
    while (this.externalConnections.peek() != null) {
      Connection connection = this.externalConnections.poll();
      ConnectionType type = connection.getType();
      if (type == ConnectionType.SPIROATOM || type == ConnectionType.SHAREDATOM) {
        result.add(RichStructureHelper.getRichAtom(connection.getConnector())
            .getStructure());
      }
      if (type == ConnectionType.CONNECTINGBOND) {
        IBond bond = RichStructureHelper.getRichBond(connection.getConnector())
            .getStructure();
        if (this.getComponents().contains(bond.getAtom(0).getID())) {
          result.add(bond.getAtom(0));
        } else {
          result.add(bond.getAtom(1));
        }
      }
    }
    return result;
  }

}
