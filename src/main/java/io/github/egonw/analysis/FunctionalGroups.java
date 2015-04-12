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
 * @file   FunctionalGroups.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:58:54 2015
 * 
 * @brief  Computation of Functional Groups using smarts.
 * 
 * 
 */

//

package io.github.egonw.analysis;

import io.github.egonw.base.Logger;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Computes functions groups using smarts patterns.
 */
public class FunctionalGroups {

  private Map<String, IAtomContainer> groups = new HashMap<>();
  private Integer groupCounter = 1;

  private IAtomContainer molecule;

  public FunctionalGroups(IAtomContainer molecule) {
    this.molecule = molecule;
    this.compute();
  }

  /**
   * Goes through the file of smarts patterns and checks each pattern against
   * the atom container.
   */
  private void compute() {
    for (Map.Entry<String, String> smarts : SmartsPatterns.getPatterns()) {
      try {
        this.checkMolecule(smarts.getValue(), smarts.getKey(),
            this.molecule.clone());
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Checks a pattern against a molecule and puts them in atom sets.
   * 
   * @param pattern
   *          the pattern to check against the molecule
   * @param name
   *          The name of the functional group
   * @param mol
   *          The molecule being checked against
   */
  private void checkMolecule(String pattern, String name, IAtomContainer mol) {
    // deals with illegal smarts strings
    try {
      SMARTSQueryTool query = new SMARTSQueryTool(pattern,
          DefaultChemObjectBuilder.getInstance());
      boolean matchesFound = false;
      matchesFound = query.matches(mol);
      // If there are matches, uses the getMatchingAtoms method to process
      // the matches
      if (matchesFound) {
        List<List<Integer>> mappings = query.getMatchingAtoms();
        this.getMappedAtoms(mappings, name);
      }
    } catch (IllegalArgumentException e) {
      // Shows which (if any) functional groups have illegal smarts
      // patterns in the file
      Logger.error("SMARTS Error: " + name);
    } catch (CDKException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Retrieves matched atoms from the molecule container by position and adds
   * them to the functional group container.
   * 
   * @param mappings
   *          A list of the list of matched atom positions for each separate
   *          match
   * @param name
   *          The name of the functional group
   */
  private void getMappedAtoms(List<List<Integer>> mappings, String name) {
    // Goes through each match for the pattern
    for (List<Integer> mappingList : mappings) {
      IAtomContainer funcGroup = new AtomContainer();
      // Adds the matched molecule to the atomcontainer
      for (Integer i : mappingList) {
        funcGroup.addAtom(this.molecule.getAtom(i));
      }
      getMappedBonds(funcGroup);
      groups.put(name + "-" + groupCounter++, funcGroup);
    }
  }

  /**
   * Retrieves the necessary bonds for a functional group from the molecule
   * container and adds them to the functional group container.
   * 
   * @param fg
   *          Functonal group container.
   */
  private void getMappedBonds(IAtomContainer fg) {
    for (IAtom atom : fg.atoms()) {
      for (IBond bond : this.molecule.getConnectedBondsList(atom)) {
        if (!fg.contains(bond)
            && StreamSupport.stream(bond.atoms().spliterator(), false)
                .allMatch(a -> fg.contains(a))) {
          fg.addBond(bond);
        }
      }
    }
  }

  public void logGroups() {
    for (Map.Entry<String, IAtomContainer> entry : groups.entrySet()) {
      Logger.logging(entry.getKey() + ": ");
      entry.getValue().atoms().forEach(a -> Logger.logging(a.getID() + " "));
      Logger.logging("\n");
    }
  }

  public Map<String, IAtomContainer> getGroups() {
    return groups;
  }
}
