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
 * @file   StructuralAnalysis.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jun 11 21:42:42 2014
 *
 * @brief  Main routines for structural analysis.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.analysis;

import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import com.progressiveaccess.cmlspeech.base.Logger;
import com.progressiveaccess.cmlspeech.connection.BridgeAtom;
import com.progressiveaccess.cmlspeech.connection.ConnectingBond;
import com.progressiveaccess.cmlspeech.connection.SharedAtom;
import com.progressiveaccess.cmlspeech.connection.SharedBond;
import com.progressiveaccess.cmlspeech.connection.SpiroAtom;
import com.progressiveaccess.cmlspeech.structure.RichAliphaticChain;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichFusedRing;
import com.progressiveaccess.cmlspeech.structure.RichIsolatedRing;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichSetType;
import com.progressiveaccess.cmlspeech.structure.RichStructure;
import com.progressiveaccess.cmlspeech.structure.RichSubRing;

import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Main functionality for the structural analysis of molecules.
 */

public class StructuralAnalysis {

  private int atomSetCount = 0;
  private final IAtomContainer molecule;

  /**
   * Performs a new structural analysis.
   *
   * @param molecule
   *          The molecule to analyse.
   */
  public StructuralAnalysis(final IAtomContainer molecule) {
    RichStructureHelper.init();
    this.molecule = molecule;

    this.initStructure();

    this.rings();
    this.aliphaticChains();
    this.functionalGroups();

    this.contexts();

    this.atomSetsAttachments();
    this.connectingBonds();
    this.sharedComponents();

    this.makeBottomSet();
    this.makeTopSet();
  }


  /** Initialises the structure from the molecule. */
  private void initStructure() {
    this.molecule.atoms().forEach(RichStructureHelper::setRichAtom);
    for (final IBond bond : this.molecule.bonds()) {
      RichStructureHelper.setRichBond(bond);
      for (final IAtom atom : bond.atoms()) {
        final RichStructure<?> richAtom = RichStructureHelper.getRichAtom(atom
            .getID());
        richAtom.getContexts().add(bond.getID());
        richAtom.getExternalBonds().add(bond.getID());
      }
    }
  }


  /** Create the rich atom set of the molecule. */
  private void makeTopSet() {
    final String id = this.getAtomSetId();
    final RichMolecule richMolecule = new RichMolecule(this.molecule, id);
    RichStructureHelper.getAtoms().forEach(a -> a.getContexts().add(id));
    RichStructureHelper.getBonds().forEach(a -> a.getContexts().add(id));
    RichStructureHelper.getAtomSets().forEach(a -> a.getContexts().add(id));
    RichStructureHelper.setRichMolecule(richMolecule);
    RichStructureHelper.setRichAtomSet(RichStructureHelper.getRichMolecule());
  }


  /** Finalise all the atom sets on the bottom layer. */
  private void makeBottomSet() {
    for (final RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.FUSED
          || system.getType() == RichSetType.MOLECULE) {
        continue;
      }
      for (final String component : system.getComponents()) {
        if (RichStructureHelper.isAtom(component)) {
          RichStructureHelper.getRichAtom(component).getSuperSystems()
          .add(system.getId());
          system.getSubSystems().add(component);
        }
      }
    }
  }


  /**
   * Returns atom set id and increments id counter.
   *
   * @return A new unique atom set id.
   */
  private String getAtomSetId() {
    this.atomSetCount++;
    return "as" + this.atomSetCount;
  }


  /**
   * Computes information on ring systems in the molecule.
   */
  private void rings() {
    final RingSystem ringSystem = new RingSystem(this.molecule);
    final Boolean sub = !Cli.hasOption("s");
    for (final IAtomContainer ring : ringSystem.fusedRings()) {
      final RichAtomSet fusedRing = new RichFusedRing(
          ring, this.getAtomSetId());
      RichStructureHelper.setRichAtomSet(fusedRing);
      if (sub) {
        for (final IAtomContainer subSystem : ringSystem.subRings(ring)) {
          final RichAtomSet subRing = new RichSubRing(
              subSystem, this.getAtomSetId());
          RichStructureHelper.setRichAtomSet(subRing);
          final String ringId = fusedRing.getId();
          final String subRingId = subRing.getId();
          subRing.getSuperSystems().add(ringId);
          subRing.getContexts().add(ringId);
          fusedRing.getSubSystems().add(subRingId);
        }
      }
    }
    for (final IAtomContainer ring : ringSystem.isolatedRings()) {
      RichStructureHelper.setRichAtomSet(new RichIsolatedRing(ring, this
          .getAtomSetId()));
    }
  }


  /**
   * Computes the longest aliphatic chain for the molecule.
   */
  private void aliphaticChains() {
    if (this.molecule == null) {
      return;
    }
    final AliphaticChain chain = new AliphaticChain(3);
    chain.calculate(this.molecule);
    for (final IAtomContainer set : chain.extract()) {
      RichStructureHelper.setRichAtomSet(new RichAliphaticChain(set, this
          .getAtomSetId()));
    }
  }


  /**
   * Computes functional groups.
   */
  private void functionalGroups() {
    final FunctionalGroups fg = new FunctionalGroups(this.molecule);
    final FunctionalGroupsFilter filter = new FunctionalGroupsFilter(
        RichStructureHelper.getAtomSets(), fg.getGroups());
    final Map<String, IAtomContainer> groups = filter.filter();
    for (final String key : groups.keySet()) {
      final IAtomContainer container = groups.get(key);
      final RichAtomSet set = new RichFunctionalGroup(
          groups.get(key), this.getAtomSetId());
      RichStructureHelper.setRichAtomSet(set);
      set.setName(key.split("-")[0]);
      Logger.logging(set.getName() + ": " + container.getAtomCount() + " atoms "
          + container.getBondCount() + " bonds");
    }
  }


  /** Computes the contexts of single atoms. */
  private void contexts() {
    for (final RichAtomSet atomSet : RichStructureHelper.getAtomSets()) {
      String id = atomSet.getId();
      for (final String structure : atomSet.getComponents()) {
        RichStructureHelper.getRichStructure(structure).getContexts().add(id);
      }
    }
  }


  /**
   * Computes external bonds and connecting atoms for all atom sets. */
  private void atomSetsAttachments() {
    RichStructureHelper.getAtomSets().forEach(this::atomSetAttachments);
  }


  /**
   * Computes the external bonds and connecting atoms for an atom set.
   *
   * @param atomSet
   *          A rich atom set.
   */
  private void atomSetAttachments(final RichAtomSet atomSet) {
    final Set<String> internalBonds = atomSet.getInternalBonds();
    final Set<String> externalBonds = atomSet.getExternalBonds();
    final Set<String> connectingAtoms = atomSet.getConnectingAtoms();
    for (final String atom : atomSet.getComponents()) {
      if (RichStructureHelper.isAtom(atom)) {
        for (final String bond : RichStructureHelper.getRichAtom(atom)
               .getExternalBonds()) {
          if (!internalBonds.contains(bond)) {
            externalBonds.add(bond);
            connectingAtoms.add(atom);
          }
        }
      }
    }
  }


  /**
   * Compute the connecting bonds for the atom container from the set of
   * external bonds.
   */
  private void connectingBonds() {
    for (final RichBond richBond : RichStructureHelper.getBonds()) {
      final String id = richBond.getId();
      final String first = ((TreeSet<String>) richBond.getComponents()).first();
      final String last = ((TreeSet<String>) richBond.getComponents()).last();
      if (richBond.getContexts().isEmpty()) {
        // We assume each bond has two atoms only!
        this.addSetConnections(id, first, last);
      }
      this.addConnectingBond(first, id, last, first);
      this.addConnectingBond(last, id, first, last);
    }
  }


  /**
   * Adds a connecting bond for a structures.
   *
   * @param structure
   *          Name of structure with the connections.
   * @param bond
   *          The connecting bond.
   * @param connected
   *          The structure the bond connects to.
   * @param origin
   *          The atom the bond originates in.
   */
  private void addConnectingBond(final String structure, final String bond,
                                 final String connected, final String origin) {
    RichStructureHelper.getRichStructure(structure).getConnections()
      .add(new ConnectingBond(bond, connected, origin));
  }


  /**
   * Creates the context cloud for an atom, that is the list of all atom sets in
   * its context.
   *
   * @param atom
   *          The input atom.
   * @return The resulting context cloud.
   */
  private Set<String> contextCloud(final String atom) {
    Set<String> contextAtom = Sets.intersection(RichStructureHelper
        .getRichAtom(atom).getContexts(), RichStructureHelper.getAtomSetIds());
    if (contextAtom.isEmpty()) {
      contextAtom = new HashSet<String>();
      contextAtom.add(atom);
    }
    return contextAtom;
  }


  /**
   * Adds connections to atom set structures.
   *
   * @param bond
   *          The bond.
   * @param atomA
   *          The first atom in the bond.
   * @param atomB
   *          The second atom in the bond.
   */
  private void addSetConnections(final String bond, final String atomA,
      final String atomB) {
    final Set<String> contextAtomA = this.contextCloud(atomA);
    final Set<String> contextAtomB = this.contextCloud(atomB);
    for (final String contextA : contextAtomA) {
      for (final String contextB : contextAtomB) {
        this.addConnectingBond(contextA, bond, contextB, atomA);
        this.addConnectingBond(contextB, bond, contextA, atomB);
      }
    }
  }


  /** Computes bridge atoms and bonds for structures that share components. */
  private void sharedComponents() {
    for (final String atomSet : RichStructureHelper.getAtomSetIds()) {
      final TreeMultimap<String, String> connectionsSet = TreeMultimap.create(
          new CmlNameComparator(), new CmlNameComparator());
      final RichAtomSet richAtomSet = RichStructureHelper
          .getRichAtomSet(atomSet);
      for (final String component : richAtomSet.getComponents()) {
        final RichStructure<?> richComponent = RichStructureHelper
            .getRichStructure(component);
        final Set<String> contexts = Sets.intersection(
            richComponent.getContexts(),
            RichStructureHelper.getAtomSetIds());
        for (final String context : contexts) {
          if (richAtomSet.getSubSystems().contains(context)
              || richAtomSet.getSuperSystems().contains(context)
              || context.equals(atomSet)) {
            continue;
          }
          connectionsSet.put(context, component);
        }
      }
      this.makeConnections(richAtomSet, connectionsSet);
    }
  }


  /**
   * Makes shared connections (sprio, shared, bridge atoms or shared bonds) for
   * an atom set.
   *
   * @param atomSet
   *          The atom set.
   * @param connectionsSet
   *          A multi map specifying connections. That is, elements from the
   *          atom set and the chemical structure it connects to. An element can
   *          be represented multiple times. For example, an atom can be shared
   *          between three sub-rings.
   */
  private void makeConnections(final RichAtomSet atomSet,
      final TreeMultimap<String, String> connectionsSet) {
    for (final String key : connectionsSet.keySet()) {
      final NavigableSet<String> allConnections = connectionsSet.get(key);
      final SortedSet<String> sharedAtoms = new TreeSet<String>(
          new CmlNameComparator());
      for (final String bond : allConnections.descendingSet()) {
        if (!RichStructureHelper.isBond(bond)) {
          break;
        }
        atomSet.getConnections().add(new SharedBond(bond, key));
        sharedAtoms.addAll(RichStructureHelper.getRichBond(bond)
                           .getComponents());
      }
      for (final String shared : sharedAtoms) {
        atomSet.getConnections().add(new BridgeAtom(shared, key));
      }
      for (final String connection : Sets.difference(allConnections,
          sharedAtoms)) {
        if (!RichStructureHelper.isAtom(connection)) {
          break;
        }
        if (atomSet.isRing()
            && RichStructureHelper.getRichAtomSet(key).isRing()) {
          atomSet.getConnections().add(new SpiroAtom(connection, key));
        } else {
          atomSet.getConnections().add(new SharedAtom(connection, key));
        }
      }
    }
  }

}
