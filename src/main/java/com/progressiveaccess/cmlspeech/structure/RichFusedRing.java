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
 * @file   RichFusedRing.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 17:13:29 2015
 *
 * @brief  Implementation of rich fused ring.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.ConnectionType;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Atom sets that are rich fused rings.
 */
public class RichFusedRing extends RichRing implements RichSuperSet {

  private final Set<String> sharedBonds = new HashSet<>();
  private final ComponentsPositions path = new ComponentsPositions();
  private Set<RichAtomSet> richSubSystems = null;


  /**
   * Generates the rich fused ring.
   *
   * @param container
   *          The atom container of the ring.
   * @param id
   *          The name of the structure.
   */
  public RichFusedRing(final IAtomContainer container, final String id) {
    super(container, id, RichSetType.FUSED);
  }


  @Override
  protected final void walk() {
    this.computeRichSubSystems();
    this.computeSharedBonds();
    this.computeRim();
    super.walk();
    this.richSubSystems.stream().forEach(s -> s.walk());
    this.setPath();
  }


  /** Computes the shared bonds inside the fused ring. */
  private void computeSharedBonds() {
    for (final RichAtomSet subRing : this.richSubSystems) {
      this.sharedBonds.addAll(subRing.getConnections().stream()
          .filter(c -> c.getType().equals(ConnectionType.SHAREDBOND))
          .map(c -> c.getConnector()).collect(Collectors.toSet()));
    }
  }


  /** Computes the atoms on the rim of the fused ring. */
  private void computeRim() {
    final IAtomContainer container = this.getStructure();
    this.setRim(new HashSet<>());
    for (final IBond bond : container.bonds()) {
      if (!this.sharedBonds.contains(bond.getID())) {
        for (final IAtom atom : bond.atoms()) {
          this.getRim().add(atom);
        }
      }
    }
  }


  @Override
  protected List<IAtom> getConnectedAtomsList(final IAtom atom) {
    final List<IBond> rimBonds = this.getStructure()
        .getConnectedBondsList(atom)
        .stream().filter(b -> !this.sharedBonds.contains(b.getID()))
        .collect(Collectors.toList());
    final List<IAtom> rimAtoms = new ArrayList<>();
    for (final IBond bond : rimBonds) {
      for (final IAtom batom : bond.atoms()) {
        if (atom != batom) {
          rimAtoms.add(batom);
        }
      }
    }
    return rimAtoms;
  }


  /** Computes the set of rich sub rings. */
  private void computeRichSubSystems() {
    this.richSubSystems = this.getSubSystems().stream()
        .map(s -> RichStructureHelper.getRichAtomSet(s))
        .collect(Collectors.toSet());
  }


  @Override
  public ComponentsPositions getPath() {
    return this.path;
  }


  /**
   * Finds the first atom set in a list that contains a particular atom.
   *
   * @param sets
   *          The list of atom sets.
   * @param atom
   *          The atom to search.
   *
   * @return The first set that contains the atom.
   */
  private RichAtomSet findAtom(final List<RichAtomSet> sets,
                               final RichAtom atom) {
    for (final RichAtomSet set : sets) {
      if (set.getComponents().contains(atom.getId())) {
        return set;
      }
    }
    return null;
  }


  @Override
  public void setPath() {
    final List<RichAtomSet> newSystem = new ArrayList<>(this.richSubSystems);
    RichAtomSet lastSystem = null;
    for (final String atomName : this) {
      final RichAtom atom = RichStructureHelper.getRichAtom(atomName);
      final RichAtomSet container = this.findAtom(newSystem, atom);
      if (container != null) {
        lastSystem = container;
        newSystem.remove(container);
        this.path.addNext(container.getId());
      }
    }
    while (newSystem.size() > 0) {
      for (final String atomName : lastSystem) {
        final RichAtom atom = RichStructureHelper.getRichAtom(atomName);
        final RichAtomSet container = this.findAtom(newSystem, atom);
        if (container != null) {
          lastSystem = container;
          newSystem.remove(container);
          this.path.addNext(container.getId());
          continue;
        }
      }
    }
  }

}
