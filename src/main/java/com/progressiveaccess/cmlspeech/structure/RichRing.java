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
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Fri Mar 20 21:57:45 2015
 *
 * @brief  An abstract class for ring structures.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;


import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.analysis.WeightComparator;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.connection.ConnectionType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.AtomicProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

/**
 * Abstract class for ring structures.
 */
public abstract class RichRing extends RichAtomSet {

  private Set<IAtom> rim = null;

  public RichRing(final IAtomContainer container, final String id,
      final RichSetType type) {
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

  public final void setRim(final Set<IAtom> rim) {
    this.rim = rim;
  }

  protected class InternalSubstComparator implements Comparator<IAtom> {
    @Override
    public int compare(final IAtom atom1, final IAtom atom2) {
      final String symbol1 = atom1.getSymbol();
      final String symbol2 = atom2.getSymbol();
      if (symbol1 == "O") {
        return -1;
      }
      if (symbol2 == "O") {
        return 1;
      }
      try {
        final double weightA = AtomicProperties.getInstance().getMass(symbol1);
        final double weightB = AtomicProperties.getInstance().getMass(symbol2);
        return (int) Math.signum(weightB - weightA);
      } catch (final IOException e) {
        return 0;
      }
    }
  }

  // TODO (sorge) This comparator contains a lot of redundancy. This could be
  // simplified.
  protected class ExternalSubstComparator implements Comparator<Connection> {
    private final WeightComparator weightCompare = new WeightComparator();

    @Override
    public int compare(final Connection con1, final Connection con2) {
      final RichStructure<?> connected1 = RichStructureHelper
          .getRichStructure(con1
              .getConnected());
      final RichStructure<?> connected2 = RichStructureHelper
          .getRichStructure(con2
              .getConnected());
      if (this.isHydroxylGroup(connected1)) {
        return -1;
      }
      if (this.isHydroxylGroup(connected2)) {
        return 1;
      }
      return this.weightCompare.compare(connected1, connected2);
    }

    private boolean isHydroxylGroup(final RichStructure<?> structure) {
      if (!RichStructureHelper.isAtomSet(structure.getId())) {
        return false;
      }
      final IAtomContainer container = ((RichAtomSet) structure).getStructure();
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

    private boolean isHydroxyl(final IAtom atom) {
      return atom.getSymbol() == "O" && atom.getImplicitHydrogenCount() == 1;
    }
  }

  @Override
  protected void walk() {
    final List<IAtom> internalSubst = this.getInternalSubsts();
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
    final List<IAtom> externalSubst = this.getExternalSubsts();
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
    final SortedSet<String> components = this.getComponents();
    final String smallestName = components.first();
    final IAtom startAtom = Lists.newArrayList(this.getStructure().atoms())
        .stream()
        .filter(x -> x.getID().equals(smallestName)).findFirst().get();
    final List<IAtom> connected = this.getConnectedAtomsList(startAtom);
    final IAtom nextLeft = connected.get(0);
    final IAtom nextRight = connected.get(1);
    final List<IAtom> path = new ArrayList<IAtom>();
    path.add(startAtom);
    if (components.headSet(nextLeft.getID()).size() <= components.headSet(
        nextRight.getID()).size()) {
      this.walkFinalise(nextLeft, path);
    } else {
      this.walkFinalise(nextRight, path);
    }
  }


  /**
   * Walks on the ring with respect to the substitutions that were found.
   *
   * <p>Given a start atom (i.e., the element with the most important
   * substitution) and set of (internal or external) substitutions, the method
   * will walk around the ring in both directions, until it has found the first
   * relevant substitution, and then finalise the walk in that direction,
   * i.e. assign the positions.</p>
   *
   * @param startAtom
   *          The start atom for the walk (most important substitution).
   * @param reference
   *          A list of substitutions.
   */
  private void walkOnSubst(final IAtom startAtom, final List<IAtom> reference) {
    final List<IAtom> queueLeft = new ArrayList<>();
    final List<IAtom> queueRight = new ArrayList<>();
    queueLeft.add(startAtom);
    queueRight.add(startAtom);
    final List<IAtom> connected = this.getConnectedAtomsList(startAtom);
    IAtom nextLeft = connected.get(0);
    IAtom nextRight = connected.get(1);
    while (nextLeft != null && nextRight != null) {
      final boolean containsLeft = reference.contains(nextLeft);
      final boolean containsRight = reference.contains(nextRight);
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
      nextLeft = this.chooseNext(queueLeft, nextLeft);
      nextRight = this.chooseNext(queueRight, nextRight);
    }
  }

  private void walkFinalise(final IAtom endAtom, final List<IAtom> path) {
    for (final IAtom atom : path) {
      this.getComponentsPositions().addNext(atom.getID());
    }
    this.walkStraight(endAtom, path);
  }


  private static Boolean isCarbon(final IAtom atom) {
    return atom.getSymbol().equals("C");
  }

  // TODO (sorge) Eventually this needs to be rewritten to work with the rim.
  private List<IAtom> getInternalSubsts() {
    final Queue<IAtom> substs = new PriorityQueue<>(
        new InternalSubstComparator());
    for (final IAtom atom : this.getStructure().atoms()) {
      if (!RichRing.isCarbon(atom)) {
        substs.add(atom);
      }
    }
    final List<IAtom> result = new ArrayList<>();
    while (substs.peek() != null) {
      result.add(substs.poll());
    }
    return result;
  }

  private final Queue<Connection> externalConnections = new PriorityQueue<>(
      new ExternalSubstComparator());

  private void getExternalConnections() {
    for (final Connection connection : this.getConnections()) {
      final ConnectionType type = connection.getType();
      if (type == ConnectionType.SPIROATOM || type == ConnectionType.SHAREDATOM
          || type == ConnectionType.CONNECTINGBOND) {
        this.externalConnections.add(connection);
      }
    }

  }

  private List<IAtom> getExternalSubsts() {
    final List<IAtom> result = new ArrayList<>();
    while (this.externalConnections.peek() != null) {
      final Connection connection = this.externalConnections.poll();
      final ConnectionType type = connection.getType();
      if (type == ConnectionType.SPIROATOM
          || type == ConnectionType.SHAREDATOM) {
        result.add(RichStructureHelper.getRichAtom(connection.getConnector())
            .getStructure());
      }
      if (type == ConnectionType.CONNECTINGBOND) {
        final IBond bond = RichStructureHelper.getRichBond(
            connection.getConnector())
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
