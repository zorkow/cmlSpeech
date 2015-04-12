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

  private List<RichAtom> singletonAtoms = new ArrayList<>();

  public StructuralAnalysis(IAtomContainer molecule) {
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
    for (IBond bond : this.molecule.bonds()) {
      RichStructureHelper.setRichBond(bond);
      for (IAtom atom : bond.atoms()) {
        RichStructure<?> richAtom = RichStructureHelper.getRichAtom(atom
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
  private void setContexts(Set<String> structures, String id) {
    for (String structure : structures) {
      RichStructureHelper.getRichStructure(structure).getContexts().add(id);
    }
  }

  private void makeTopSet() {
    String id = this.getAtomSetId();
    RichStructureHelper.richMolecule = new RichMolecule(this.molecule, id);
    this.setContexts(RichStructureHelper.richAtoms.keySet(), id);
    this.setContexts(RichStructureHelper.richBonds.keySet(), id);
    this.setContexts(RichStructureHelper.richAtomSets.keySet(), id);
    RichStructureHelper.richMolecule.getContexts().remove(id);
    for (RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.SMALLEST) {
        continue;
      }
      system.getSuperSystems().add(id);
      RichStructureHelper.richMolecule.getSubSystems().add(system.getId());
    }
    for (RichAtom atom : this.getSingletonAtoms()) {
      atom.getSuperSystems().add(id);
      RichStructureHelper.richMolecule.getSubSystems().add(atom.getId());
    }
  }

  private void makeBottomSet() {
    for (RichAtomSet system : RichStructureHelper.getAtomSets()) {
      if (system.getType() == RichSetType.FUSED) {
        continue;
      }
      for (String component : system.getComponents()) {
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
    atomSetCount++;
    return "as" + atomSetCount;
  }

  private String valuesToString(SortedMap<String, RichStructure<?>> map) {
    return Joiner.on("\n").join(
        map.values().stream().map(RichStructure::toString)
            .collect(Collectors.toList()));
  }

  @Override
  public String toString() {
    return valuesToString(RichStructureHelper.richAtoms) + "\n"
        + valuesToString(RichStructureHelper.richBonds) + "\n"
        + valuesToString(RichStructureHelper.richAtomSets);
  }

  /**
   * Computes information on ring systems in the molecule.
   */
  private void rings() {
    RingSystem ringSystem = new RingSystem(this.molecule);
    Boolean sub = !Cli.hasOption("s");
    for (IAtomContainer ring : ringSystem.fusedRings()) {
      RichStructure<?> fusedRing = RichStructureHelper
          .setRichAtomSet(new RichFusedRing(ring, this.getAtomSetId()));
      if (sub) {
        for (IAtomContainer subSystem : ringSystem.subRings(ring)) {
          RichStructure<?> subRing = RichStructureHelper
              .setRichAtomSet(new RichSubRing(subSystem, this.getAtomSetId()));
          String ringId = fusedRing.getId();
          String subRingId = subRing.getId();
          subRing.getSuperSystems().add(ringId);
          subRing.getContexts().add(ringId);
          fusedRing.getSubSystems().add(subRingId);
        }
      }
    }
    for (IAtomContainer ring : ringSystem.isolatedRings()) {
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
    AliphaticChain chain = new AliphaticChain(3);
    chain.calculate(this.molecule);
    for (IAtomContainer set : chain.extract()) {
      RichStructureHelper.setRichAtomSet(new RichAliphaticChain(set, this
          .getAtomSetId()));
    }
  }

  /**
   * Computes functional groups.
   */
  private void functionalGroups() {
    FunctionalGroups fg = new FunctionalGroups(this.molecule);
    FunctionalGroupsFilter filter = new FunctionalGroupsFilter(
        RichStructureHelper.getAtomSets(), fg.getGroups());
    Map<String, IAtomContainer> groups = filter.filter();
    for (String key : groups.keySet()) {
      IAtomContainer container = groups.get(key);
      RichAtomSet set = RichStructureHelper
          .setRichAtomSet(new RichFunctionalGroup(groups.get(key), this
              .getAtomSetId()));
      set.name = key.split("-")[0];
      Logger.logging(set.name + ": " + container.getAtomCount() + " atoms "
          + container.getBondCount() + " bonds");
    }
  }

  /** Computes the contexts of single atoms. */
  private void contexts() {
    for (String key : RichStructureHelper.richAtomSets.keySet()) {
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
  private void atomSetAttachments(RichAtomSet atomSet) {
    IAtomContainer container = atomSet.getStructure();
    Set<IBond> externalBonds = externalBonds(container);
    for (IBond bond : externalBonds) {
      atomSet.getExternalBonds().add(bond.getID());
    }
    Set<IAtom> connectingAtoms = connectingAtoms(container, externalBonds);
    for (IAtom atom : connectingAtoms) {
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
  private Set<IBond> externalBonds(IAtomContainer container) {
    Set<IBond> internalBonds = Sets.newHashSet(container.bonds());
    Set<IBond> allBonds = Sets.newHashSet();
    for (IAtom atom : container.atoms()) {
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
  private Set<IAtom> connectingAtoms(IAtomContainer container, Set<IBond> bonds) {
    Set<IAtom> allAtoms = Sets.newHashSet(container.atoms());
    Set<IAtom> connectedAtoms = Sets.newHashSet();
    for (IBond bond : bonds) {
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
    for (String bond : RichStructureHelper.richBonds.keySet()) {
      RichStructure<?> richBond = RichStructureHelper.getRichBond(bond);
      String first = ((TreeSet<String>) richBond.getComponents()).first();
      String last = ((TreeSet<String>) richBond.getComponents()).last();
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

  private void addConnectingBond(RichStructure<?> structure, String bond,
      String connected) {
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
  private Set<String> contextCloud(String atom) {
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
  private void addSetConnections(String bond, String atomA, String atomB) {
    Set<String> contextAtomA = this.contextCloud(atomA);
    Set<String> contextAtomB = this.contextCloud(atomB);
    for (String contextA : contextAtomA) {
      RichStructure<?> richStructureA = RichStructureHelper
          .getRichStructure(contextA);
      for (String contextB : contextAtomB) {
        RichStructure<?> richStructureB = RichStructureHelper
            .getRichStructure(contextB);
        this.addConnectingBond(richStructureA, bond, contextB);
        this.addConnectingBond(richStructureB, bond, contextA);
      }
    }
  }

  /** Computes bridge atoms and bonds for structures that share components. */
  private void sharedComponents() {
    for (String atomSet : RichStructureHelper.richAtomSets.keySet()) {
      TreeMultimap<String, String> connectionsSet = TreeMultimap.create(
          new CmlNameComparator(), new CmlNameComparator());
      RichAtomSet richAtomSet = RichStructureHelper.getRichAtomSet(atomSet);
      for (String component : richAtomSet.getComponents()) {
        RichStructure<?> richComponent = RichStructureHelper
            .getRichStructure(component);
        Set<String> contexts = Sets.intersection(richComponent.getContexts(),
            RichStructureHelper.richAtomSets.keySet());
        for (String context : contexts) {
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

  private void makeConnections(RichAtomSet atomSet,
      TreeMultimap<String, String> connectionsSet) {
    for (String key : connectionsSet.keySet()) {
      NavigableSet<String> allConnections = connectionsSet.get(key);
      SortedSet<String> sharedAtoms = new TreeSet<String>(
          new CmlNameComparator());
      for (String bond : allConnections.descendingSet()) {
        if (!RichStructureHelper.isBond(bond)) {
          break;
        }
        atomSet.getConnections().add(new SharedBond(bond, key));
        for (IAtom atom : RichStructureHelper.getRichBond(bond).getStructure()
            .atoms()) {
          sharedAtoms.add(atom.getID());
        }
      }
      for (String shared : sharedAtoms) {
        atomSet.getConnections().add(new BridgeAtom(shared, key));
      }
      for (String connection : Sets.difference(allConnections, sharedAtoms)) {
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
  public Set<String> siblingsNew(RichAtomSet atomSet) {
    Set<String> result = new HashSet<String>();
    if (atomSet.getType() == RichSetType.SMALLEST) {
      for (String superSystem : atomSet.getSuperSystems()) {
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
  public Set<RichStructure<?>> siblings(RichAtomSet atomSet) {
    Set<String> result = new HashSet<String>();
    if (atomSet.getType() == RichSetType.SMALLEST) {
      for (String superSystem : atomSet.getSuperSystems()) {
        result.addAll(RichStructureHelper.getRichAtomSet(superSystem)
            .getSubSystems());
      }
    }
    result.remove(atomSet.getId());
    return result.stream().map(RichStructureHelper::getRichAtomSet)
        .collect(Collectors.toSet());
  }

  private void singletonAtoms() {
    Set<String> atomSetComponents = new HashSet<String>();
    RichStructureHelper.richAtomSets.values().forEach(
        as -> atomSetComponents.addAll(as.getComponents()));
    for (RichAtom atom : RichStructureHelper.getAtoms()) {
      if (!atomSetComponents.contains(atom.getId())) {
        this.singletonAtoms.add(atom);
      }
    }
  }

  // Comparison in terms of "interestingness". The most interesting is sorted to
  // the front.
  public class AnalysisCompare implements Comparator<String> {

    String heur = Cli.hasOption("m") ? Cli.getOptionValue("m") : "";

    public int compare(String vertexA, String vertexB) {
      Comparator<RichStructure<?>> comparator = new Heuristics(heur);

      Integer aux = comparator.compare(
          RichStructureHelper.getRichStructure(vertexA),
          RichStructureHelper.getRichStructure(vertexB));
      return aux;
    }
  }

}
