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

package io.github.egonw.analysis;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import io.github.egonw.base.Cli;
import io.github.egonw.base.CmlNameComparator;
import io.github.egonw.base.Logger;
import io.github.egonw.connection.BridgeAtom;
import io.github.egonw.connection.ConnectingBond;
import io.github.egonw.connection.SharedAtom;
import io.github.egonw.connection.SharedBond;
import io.github.egonw.connection.SpiroAtom;
import io.github.egonw.structure.RichAliphaticChain;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichFunctionalGroup;
import io.github.egonw.structure.RichFusedRing;
import io.github.egonw.structure.RichIsolatedRing;
import io.github.egonw.structure.RichMolecule;
import io.github.egonw.structure.RichSetType;
import io.github.egonw.structure.RichStructure;
import io.github.egonw.structure.RichSubRing;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Main functionality for the structural analysis of molecules.
 */

public class StructuralAnalysis {

  private int atomSetCount = 0;
  private final IAtomContainer molecule;

  private final List<RichAtom> singletonAtoms = new ArrayList<>();

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

    this.singletonAtoms();

    this.makeTopSet();
    this.makeBottomSet();

    // Important that the top set is only added here as otherwise the
    // previous two methods will result in an infinite loop.
    RichStructureHelper.setRichAtomSet(RichStructureHelper.richMolecule);
    RichStructureHelper.richMolecule.computePositions();
  }

  public List<RichAtom> getSingletonAtoms() {
    return this.singletonAtoms;
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

  /**
   * Adds a context element for a set of structures.
   *
   * @param structures
   *          Set of structure names.
   * @param id
   *          Context element to be added.
   */
  private void setContexts(final Set<String> structures, final String id) {
    for (final String structure : structures) {
      RichStructureHelper.getRichStructure(structure).getContexts().add(id);
    }
  }

  private void makeTopSet() {
    final String id = this.getAtomSetId();
    RichStructureHelper.richMolecule = new RichMolecule(this.molecule, id);
    this.setContexts(RichStructureHelper.richAtoms.keySet(), id);
    this.setContexts(RichStructureHelper.richBonds.keySet(), id);
    this.setContexts(RichStructureHelper.richAtomSets.keySet(), id);
    RichStructureHelper.richMolecule.getContexts().remove(id);
    for (final RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.SMALLEST) {
        continue;
      }
      system.getSuperSystems().add(id);
      RichStructureHelper.richMolecule.getSubSystems().add(system.getId());
    }
    for (final RichAtom atom : this.getSingletonAtoms()) {
      atom.getSuperSystems().add(id);
      RichStructureHelper.richMolecule.getSubSystems().add(atom.getId());
    }
  }

  private void makeBottomSet() {
    for (final RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.FUSED) {
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

  private String valuesToString(final SortedMap<String, RichStructure<?>> map) {
    return Joiner.on("\n").join(
        map.values().stream().map(RichStructure::toString)
        .collect(Collectors.toList()));
  }

  @Override
  public String toString() {
    return this.valuesToString(RichStructureHelper.richAtoms) + "\n"
        + this.valuesToString(RichStructureHelper.richBonds) + "\n"
        + this.valuesToString(RichStructureHelper.richAtomSets);
  }

  /**
   * Computes information on ring systems in the molecule.
   */
  private void rings() {
    final RingSystem ringSystem = new RingSystem(this.molecule);
    final Boolean sub = !Cli.hasOption("s");
    for (final IAtomContainer ring : ringSystem.fusedRings()) {
      final RichStructure<?> fusedRing = RichStructureHelper
          .setRichAtomSet(new RichFusedRing(ring, this.getAtomSetId()));
      if (sub) {
        for (final IAtomContainer subSystem : ringSystem.subRings(ring)) {
          final RichStructure<?> subRing = RichStructureHelper
              .setRichAtomSet(new RichSubRing(subSystem, this.getAtomSetId()));
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
      final RichAtomSet set = RichStructureHelper
          .setRichAtomSet(new RichFunctionalGroup(groups.get(key), this
              .getAtomSetId()));
      set.name = key.split("-")[0];
      Logger.logging(set.name + ": " + container.getAtomCount() + " atoms "
          + container.getBondCount() + " bonds");
    }
  }

  /** Computes the contexts of single atoms. */
  private void contexts() {
    for (final String key : RichStructureHelper.richAtomSets.keySet()) {
      this.setContexts(RichStructureHelper.getRichAtomSet(key).getComponents(),
          key);
    }
  }

  private void atomSetsAttachments() {
    RichStructureHelper.richAtomSets.values().forEach(
        as -> this.atomSetAttachments((RichAtomSet) as));
  }

  /**
   * Computes the external bonds and connecting atoms for an atom set.
   *
   * @param atomSet
   *          A rich atom set.
   */
  private void atomSetAttachments(final RichAtomSet atomSet) {
    final IAtomContainer container = atomSet.getStructure();
    final Set<IBond> externalBonds = this.externalBonds(container);
    for (final IBond bond : externalBonds) {
      atomSet.getExternalBonds().add(bond.getID());
    }
    final Set<IAtom> connectingAtoms = this.connectingAtoms(container,
        externalBonds);
    for (final IAtom atom : connectingAtoms) {
      atomSet.getConnectingAtoms().add(atom.getID());
    }
  }

  /**
   * Compute the bonds that connects this atom container to the rest of the
   * molecule.
   *
   * @param container
   *          The substructure under consideration.
   * @return List of bonds attached to but not contained in the container.
   */
  private Set<IBond> externalBonds(final IAtomContainer container) {
    final Set<IBond> internalBonds = Sets.newHashSet(container.bonds());
    final Set<IBond> allBonds = Sets.newHashSet();
    for (final IAtom atom : container.atoms()) {
      allBonds.addAll(this.molecule.getConnectedBondsList(atom));
    }
    return Sets.difference(allBonds, internalBonds);
  }

  /**
   * Compute the atoms that have bonds not internal to the molecule.
   *
   * @param container
   *          The substructure under consideration.
   * @param bonds
   *          External bonds.
   * @return List of atoms with external connections.
   */
  private Set<IAtom> connectingAtoms(final IAtomContainer container,
      final Set<IBond> bonds) {
    final Set<IAtom> allAtoms = Sets.newHashSet(container.atoms());
    final Set<IAtom> connectedAtoms = Sets.newHashSet();
    for (final IBond bond : bonds) {
      connectedAtoms.addAll(Lists.newArrayList(bond.atoms()).stream()
          .filter(a -> allAtoms.contains(a)).collect(Collectors.toSet()));
    }
    return connectedAtoms;
  }

  /**
   * Compute the connecting bonds for the atom container from the set of
   * external bonds.
   *
   * @param container
   *          The substructure under consideration.
   * @param externalBonds
   *          Bonds external to the substructure.
   * @return List of connecting bonds, i.e., external but not part of another
   *         substructure.
   */
  private void connectingBonds() {
    for (final String bond : RichStructureHelper.richBonds.keySet()) {
      final RichStructure<?> richBond = RichStructureHelper.getRichBond(bond);
      final String first = ((TreeSet<String>) richBond.getComponents()).first();
      final String last = ((TreeSet<String>) richBond.getComponents()).last();
      if (richBond.getContexts().isEmpty()) {
        // We assume each bond has two atoms only!
        this.addSetConnections(bond, first, last);
      }
      this.addConnectingBond(RichStructureHelper.getRichStructure(first), bond,
          last);
      this.addConnectingBond(RichStructureHelper.getRichStructure(last), bond,
          first);
    }
  }

  private void addConnectingBond(final RichStructure<?> structure,
      final String bond,
      final String connected) {
    structure.getConnections().add(new ConnectingBond(bond, connected));
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
        .getRichAtom(atom).getContexts(), RichStructureHelper.richAtomSets
        .keySet());
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
      final RichStructure<?> richStructureA = RichStructureHelper
          .getRichStructure(contextA);
      for (final String contextB : contextAtomB) {
        final RichStructure<?> richStructureB = RichStructureHelper
            .getRichStructure(contextB);
        this.addConnectingBond(richStructureA, bond, contextB);
        this.addConnectingBond(richStructureB, bond, contextA);
      }
    }
  }

  /** Computes bridge atoms and bonds for structures that share components. */
  private void sharedComponents() {
    for (final String atomSet : RichStructureHelper.richAtomSets.keySet()) {
      final TreeMultimap<String, String> connectionsSet = TreeMultimap.create(
          new CmlNameComparator(), new CmlNameComparator());
      final RichAtomSet richAtomSet = RichStructureHelper
          .getRichAtomSet(atomSet);
      for (final String component : richAtomSet.getComponents()) {
        final RichStructure<?> richComponent = RichStructureHelper
            .getRichStructure(component);
        final Set<String> contexts = Sets.intersection(
            richComponent.getContexts(),
            RichStructureHelper.richAtomSets.keySet());
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
        for (final IAtom atom : RichStructureHelper.getRichBond(bond)
            .getStructure()
            .atoms()) {
          sharedAtoms.add(atom.getID());
        }
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

  /**
   * Computes the siblings of this atom set if it is a subring.
   *
   * @param atomSet
   *          The given atom set.
   * @return A list of siblings.
   */
  public Set<String> siblingsNew(final RichAtomSet atomSet) {
    final Set<String> result = new HashSet<String>();
    if (atomSet.getType() == RichSetType.SMALLEST) {
      for (final String superSystem : atomSet.getSuperSystems()) {
        result.addAll((RichStructureHelper.getRichAtomSet(superSystem))
            .getSubSystems());
      }
    }
    result.remove(atomSet);
    return result;
  }

  /**
   * Computes the siblings of this atom set if it is a subring.
   *
   * @param atomSet
   *          The given atom set.
   * @return A list of siblings.
   */
  public Set<RichStructure<?>> siblings(final RichAtomSet atomSet) {
    final Set<String> result = new HashSet<String>();
    if (atomSet.getType() == RichSetType.SMALLEST) {
      for (final String superSystem : atomSet.getSuperSystems()) {
        result.addAll(RichStructureHelper.getRichAtomSet(superSystem)
            .getSubSystems());
      }
    }
    result.remove(atomSet.getId());
    return result.stream().map(RichStructureHelper::getRichAtomSet)
        .collect(Collectors.toSet());
  }

  private void singletonAtoms() {
    final Set<String> atomSetComponents = new HashSet<String>();
    RichStructureHelper.richAtomSets.values().forEach(
        as -> atomSetComponents.addAll(as.getComponents()));
    for (final RichAtom atom : RichStructureHelper.getAtoms()) {
      if (!atomSetComponents.contains(atom.getId())) {
        this.singletonAtoms.add(atom);
      }
    }
  }

  // Comparison in terms of "interestingness". The most interesting is sorted to
  // the front.
  public class AnalysisCompare implements Comparator<String> {

    String heur = Cli.hasOption("m") ? Cli.getOptionValue("m") : "";

    @Override
    public int compare(final String vertexA, final String vertexB) {
      final Comparator<RichStructure<?>> comparator = new Heuristics(this.heur);

      final Integer aux = comparator.compare(
          RichStructureHelper.getRichStructure(vertexA),
          RichStructureHelper.getRichStructure(vertexB));
      return aux;
    }
  }

}
