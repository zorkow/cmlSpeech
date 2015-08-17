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
 * @file   StructuralFormula.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 13:04:20 2015
 *
 * @brief  Computation of structural formulas.
 *
 *
 */

package com.progressiveaccess.cmlspeech.analysis;

import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;

import org.openscience.cdk.interfaces.IAtom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class which takes a RichAtomSet or an IAtomContainer and returns a string
 * with the structural formula.
 *
 * @author Joshie
 */

public class StructuralFormula {

  private static final Integer SUBSCRIPT_MAX = 9;
  private static final Integer SUBSCRIPT_ZERO = 0x2080;

  private ComponentsPositions componentPositions;
  private boolean useSubScripts;
  private final ArrayList<String> allConnectingAtoms = new ArrayList<String>();
  private final ArrayList<String> appendedAtoms = new ArrayList<String>();

  private String structuralFormula = "";

  /**
   * Computes the Structural Formulas for the atom sets.
   */
  public StructuralFormula() {
    this.useSubScripts = Cli.hasOption("sub");
    RichAtomSet molecule = RichStructureHelper.getRichMolecule();
    if (Cli.hasOption("sf")) {
      this.computeAnalysis();
      molecule.setStructuralFormula(this.structuralFormula);
    }
    this.allConnectingAtoms.clear();
    for (RichAtomSet richAtomSet : RichStructureHelper.getAtomSets()) {
      if (richAtomSet == molecule) {
        continue;
      }
      this.appendedAtoms.clear();
      this.structuralFormula = "";
      this.isolatedRichAtomSet(richAtomSet);
      richAtomSet.setStructuralFormula(this.structuralFormula);
    }
  }


  // TODO (sorge) The complex formula for the molecule does not work properly.
  /**
   * Computes a structural formula using a Structural Analysis.
   */
  public void computeAnalysis() {
    final List<RichAtomSet> atomSets = RichStructureHelper.getAtomSets();

    // Adds all connectingAtoms from all RichAtomSets to a list
    // for checking when adding neighbours
    for (final RichAtomSet set : atomSets) {
      for (final String connectingAtom : set.getConnectingAtoms()) {
        this.allConnectingAtoms.add(connectingAtom);
      }
    }

    // Computes the structural formula for each RichAtomSet
    for (final RichAtomSet richAtomSet : atomSets) {
      this.computeRichAtomSet(richAtomSet);
    }
  }


  /**
   * Computes the structural formula for a RichAtomSet.
   *
   * @param richAtomSet
   *          The RichAtomSet to be computed
   */
  public void computeRichAtomSet(final RichAtomSet richAtomSet) {
    // Set of atoms in the richAtomSet which connect to a
    // subStructures or superStructures
    final Set<String> connectingAtoms = richAtomSet.getConnectingAtoms();

    // The atom positions of the current RichAtomSet
    this.componentPositions = richAtomSet.getComponentsPositions();

    // For each atom in the atomPositions
    for (final String currentAtom : this.componentPositions) {
      // Get data of the current atom
      final RichAtom currentRichAtom = RichStructureHelper
          .getRichAtom(currentAtom);
      // Check if the current atom is connected to a subStructure
      // If not then simply "print" the atom
      if (!connectingAtoms.contains(currentAtom)) {
        this.appendAtom(currentAtom);
      } else {
        // If the atom does have a connecting atom then we print
        // the atom and we also print its connecting atoms
        this.appendAtom(currentAtom);
        this.addSubStructure(currentRichAtom, connectingAtoms);
      }
    }
  }


  /**
   * Adds a substructure to the structuralFormula to be printed.
   *
   * @param currentRichAtom
   *          The currently considered atom.
   * @param connectingAtoms
   *          A set of connecting atoms.
   */
  private void addSubStructure(final RichAtom currentRichAtom,
      final Set<String> connectingAtoms) {
    // This is where the subStructure is printed
    // We get every connecting atom to the current atom
    final Set<Connection> connections = currentRichAtom.getConnections();
    for (final Connection connection : connections) {
      // Assign the connected atom in question
      final String currentSubAtom = connection.getConnected();

      // Check for duplicate branches being printed
      if (!this.appendedAtoms.contains(currentSubAtom)) {
        // We check if this currentSubAtom is a member of the current
        // RichAtomSet
        if (!connectingAtoms.contains(currentSubAtom)
            && !this.componentPositions.contains(currentSubAtom)) {

          this.appendSymbol("(");
          this.appendAtom(currentSubAtom);
          this.addNeighbours(currentSubAtom, connectingAtoms);

        }
      }
    }

    this.structuralFormula += ")";

  }


  /**
   * Method to print atoms which are in a subStructure and not part of a atom
   * set or connected to an atom set.
   *
   * @param atomId
   *          The atom in the subStructure
   * @param connectingAtoms
   *          Set of connectingAtoms in the richAtomSet
   */
  private void addNeighbours(final String atomId,
      final Set<String> connectingAtoms) {

    final RichAtom currentRichSubAtom = RichStructureHelper.getRichAtom(atomId);
    RichStructureHelper.getAtoms();

    for (final Connection connection : currentRichSubAtom.getConnections()) {
      // This is a atom or atom set connected to the atom in question
      final String neighbour = connection.getConnected();

      // If this connection is not a connectingAtom or an atomSet then will
      // append
      if (!connectingAtoms.contains(neighbour)
          && !(RichStructureHelper.getRichAtom(neighbour) == null)
          && !this.allConnectingAtoms.contains(neighbour)) {
        this.appendAtom(neighbour);
      }
    }
  }


  /**
   * Adds the atom and its Hydrogens to the structuralFormula.
   *
   * @param atomId
   *          Name of the atom to append.
   */
  private void appendAtom(final String atomId) {
    if (this.appendedAtoms.contains(atomId)) {
      return;
    }
    this.appendedAtoms.add(atomId);
    final IAtom atom = RichStructureHelper.getRichAtom(atomId).getStructure();
    this.appendSymbol(atom.getSymbol());
    final int hydrogens = atom.getImplicitHydrogenCount();
    if (hydrogens > 0) {
      this.appendSymbol("H" + (hydrogens == 1 ? ""
                               : this.getSubScript(hydrogens)));
    }
  }


  /**
   * Adds an atom symbol to the structural formula.
   *
   * @param atom
   *          The atom symbol to add.
   */
  private void appendSymbol(final String atom) {
    if (this.structuralFormula.equals("")) {
      this.structuralFormula = atom;
      return;
    }
    if (this.structuralFormula.substring(
            this.structuralFormula.length() - 1).equals("(")) {
      this.structuralFormula += atom;
      return;
    }
    this.structuralFormula += " " + atom;
  }


  /**
   * Computes the structural formula for a RichAtomSet.
   *
   * @param richAtomSet
   *          The RichAtomSet to be computed
   */
  public void isolatedRichAtomSet(final RichAtomSet richAtomSet) {
    this.componentPositions = richAtomSet.getComponentsPositions();
    for (final String currentAtom : this.componentPositions) {
      this.appendAtom(currentAtom);
    }
  }


  /**
   * Gets the subscript for the inserted number.
   *
   * @param number
   *          The number to be translated
   * @return Returns the subscript of the inserted number
   */
  private String getSubScript(final Integer number) {
    if (this.useSubScripts && number <= SUBSCRIPT_MAX) {
      return Character.toString((char) (SUBSCRIPT_ZERO + number));
    }
    return number.toString();
  }

}
