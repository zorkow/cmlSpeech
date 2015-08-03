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
 * @file   AliphaticChain.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:54:55 2015
 *
 * @brief  Computations of Aliphatic Chains.
 *
 *
 */

//
// This is based on the LongestAliphaticChainDescriptor class from the CDK.  The
// intention is to get the actual chains out, not just a number for the longest
// chain.
//

package com.progressiveaccess.cmlspeech.analysis;

import com.google.common.collect.Lists;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that returns a list of aliphatic chains in a given container.
 *
 */
public class AliphaticChain {

  private static final Integer FLOYD_MAX = 999999999;
  private final List<IAtomContainer> chains = new ArrayList<>();
  private Integer minLength = 2;
  private IAtomContainer molecule;

  /**
   * Constructor for the AliphaticChainDescriptor object.
   *
   * @param molecule
   *          The atom container of the molecule to consider.
   * @param length
   *          Minimum length of chains to consider.
   */
  public AliphaticChain(final IAtomContainer molecule, final Integer length) {
    this.molecule = molecule;
    this.minLength = length;
    this.calculate();
  }


  /**
   * Calculate the aliphatic chains in the given atom container.
   */
  private void calculate() {

    final IAtomContainer container = this.molecule;
    IRingSet rs;
    try {
      rs = new SpanningTree(container).getBasicRings();
    } catch (final NoSuchAtomException e) {
      return;
    }
    for (int i = 0; i < container.getAtomCount(); i++) {
      if (rs.contains(container.getAtom(i))) {
        container.getAtom(i).setFlag(CDKConstants.ISINRING, true);
      }
      container.getAtom(i).setFlag(CDKConstants.VISITED, false);
    }

    List<IAtom> startSphere;
    List<IAtom> path;

    for (int i = 0; i < container.getAtomCount(); i++) {
      final IAtom atomi = container.getAtom(i);
      if (atomi.getSymbol().equals("H")
          || atomi.getFlag(CDKConstants.ISAROMATIC)
          || atomi.getFlag(CDKConstants.ISINRING)
          || !atomi.getSymbol().equals("C")
          || atomi.getFlag(CDKConstants.VISITED)) {
        continue;
      }

      startSphere = new ArrayList<IAtom>();
      path = new ArrayList<IAtom>();
      startSphere.add(atomi);
      try {
        this.breadthFirstSearch(startSphere, path);
      } catch (final CDKException e) {
        return;
      }
      final IAtomContainer aliphaticChain =
          this.createAtomContainerFromPath(container, path);
      if (aliphaticChain.getAtomCount() < this.minLength) {
        continue;
      }
      final double[][] conMat = ConnectionMatrix.getMatrix(aliphaticChain);
      final Integer[][] pathMatrix = new Integer[conMat.length][conMat.length];
      final int[][] apsp = this.computeFloydApsp(conMat, pathMatrix);
      final int[] pathCoordinates = this.getLongestChainPath(apsp);
      final IAtomContainer longestAliphaticChain =
          this.createAtomContainerFromPath(aliphaticChain,
               this.longestPath(pathMatrix, pathCoordinates[0],
                                pathCoordinates[1], aliphaticChain));
      // The longest chain container.
      this.chains.add(longestAliphaticChain);
    }
  }


  /**
   * Computes the longest path in the current path matrix for potential
   * aliphatic chain.
   *
   * @param pathMatrix
   *          The path matrix.
   * @param pathStart
   *          The start position of the path.
   * @param pathEnd
   *          The end position of the path.
   * @param container
   *          The atom container of the chain under consideration.
   *
   * @return The path for the aliphatc chain.
   */
  private List<IAtom> longestPath(final Integer[][] pathMatrix,
        final int pathStart, final int pathEnd,
        final IAtomContainer container) {
    final List<IAtom> path = new ArrayList<>();
    if (pathMatrix[pathStart][pathEnd] == null) {
      return path;
    }
    final List<IAtom> chainAtoms = Lists.newArrayList(container.atoms());
    path.add(chainAtoms.get(pathStart));
    int nextPathStart = pathStart;
    while (nextPathStart != pathEnd) {
      nextPathStart = pathMatrix[nextPathStart][pathEnd];
      path.add(chainAtoms.get(nextPathStart));
    }
    return path;
  }


  /**
   * Floyd Warshall all pair shortest path algorithm.
   *
   * @param costMatrix
   *          The cost Matrix.
   * @param pathMatrix
   *          The path Matrix.
   *
   * @return The computed distance matrix.
   */
  public int[][] computeFloydApsp(final double[][] costMatrix,
      final Integer[][] pathMatrix) {
    final int nrow = costMatrix.length;
    final int[][] distMatrix = new int[nrow][nrow];
    for (int i = 0; i < nrow; i++) {
      for (int j = 0; j < nrow; j++) {
        if (costMatrix[i][j] == 0) {
          distMatrix[i][j] = FLOYD_MAX;
          pathMatrix[i][j] = null;
        } else {
          distMatrix[i][j] = 1;
          pathMatrix[i][j] = j;
        }
      }
    }
    for (int i = 0; i < nrow; i++) {
      distMatrix[i][i] = 0;
    }
    for (int k = 0; k < nrow; k++) {
      for (int i = 0; i < nrow; i++) {
        for (int j = 0; j < nrow; j++) {
          if (distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j]) {
            distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
            pathMatrix[i][j] = pathMatrix[i][k];
          }
        }
      }
    }
    return distMatrix;
  }


  /**
   * Computes the longest path in the all pair shortest path distance matrix.
   *
   * @param apsp
   *          The distance matrix.
   *
   * @return The longest path from the distance matrix.
   */
  private int[] getLongestChainPath(final int[][] apsp) {
    final int[] path = new int[] {0, 0};
    int longestPath = 0;
    for (int i = 0; i < apsp.length; i++) {
      for (int j = 0; j < apsp.length; j++) {
        if (apsp[i][j] + 1 > longestPath) {
          path[0] = i;
          path[1] = j;
          longestPath = apsp[i][j] + 1;
        }
      }
    }
    return path;
  }


  /**
   * Creates a new atom containers for an aliphatic chain.
   *
   * @param container
   *          The original container of the molecule.
   * @param path
   *          The path in the molecule representing the chain.
   *
   * @return The newly created container for the alephatic chain.
   */
  private IAtomContainer createAtomContainerFromPath(
      final IAtomContainer container,
      final List<IAtom> path) {
    final IAtomContainer aliphaticChain = container.getBuilder().newInstance(
        IAtomContainer.class);
    for (int i = 0; i < path.size() - 1; i++) {
      final IAtom firstAtom = path.get(i);
      if (!aliphaticChain.contains(firstAtom)) {
        aliphaticChain.addAtom(firstAtom);
      }
      for (int j = 1; j < path.size(); j++) {
        final IAtom secondAtom = path.get(j);
        final IBond bond = container.getBond(firstAtom, secondAtom);
        if (bond == null) {
          continue;
        }
        if (!aliphaticChain.contains(secondAtom)) {
          aliphaticChain.addAtom(secondAtom);
        }
        if (!aliphaticChain.contains(bond)) {
          aliphaticChain.addBond(bond);
        }
      }
    }
    if (aliphaticChain.getBondCount() == 0) {
      aliphaticChain.removeAllElements();
    }
    return aliphaticChain;
  }


  /**
   * Performs a breadthFirstSearch in an AtomContainer starting with a
   * particular sphere, which usually consists of one start atom, and searches
   * for a pi system.
   *
   * @param container
   *          The AtomContainer to be searched
   * @param sphere
   *          A sphere of atoms to start the search with
   * @param path
   *          A vector which stores the atoms belonging to the pi system
   * @throws CDKException
   *          Description of the Exception
   */
  private void breadthFirstSearch(final List<IAtom> sphere, final List<IAtom> path)
      throws CDKException {
    IAtom nextAtom;
    final List<IAtom> newSphere = new ArrayList<IAtom>();
    for (final IAtom atom : sphere) {
      final List<IBond> bonds = this.molecule.getConnectedBondsList(atom);
      for (final IBond bond : bonds) {
        nextAtom = bond.getConnectedAtom(atom);
        if ((!nextAtom.getFlag(CDKConstants.ISAROMATIC) && !nextAtom
            .getFlag(CDKConstants.ISINRING) & nextAtom.getSymbol().equals("C"))
            & !nextAtom.getFlag(CDKConstants.VISITED)) {
          path.add(nextAtom);
          nextAtom.setFlag(CDKConstants.VISITED, true);
          if (this.molecule.getConnectedBondsCount(nextAtom) > 1) {
            newSphere.add(nextAtom);
          }
        } else {
          nextAtom.setFlag(CDKConstants.VISITED, true);
        }
      }
    }
    if (newSphere.size() > 0) {
      this.breadthFirstSearch(newSphere, path);
    }
  }


  /**
   * @return All aliphatic chains of the molecule that are at least as long as
   *          the given minimum length.
   */
  public List<IAtomContainer> getChains() {
    return this.chains;
  }

}
