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

  // Containers of chains.
  private final List<IAtomContainer> chain = new ArrayList<>();
  // Minimum length of aliphatic chain to extract.
  private Integer minLength = 2;

  /**
   * Constructor for the AliphaticChainDescriptor object.
   */
  public AliphaticChain() {
  }

  public AliphaticChain(final Integer minLength) {
    this.minLength = minLength;
  }

  // The longest chain container.
  public List<IAtomContainer> extract() {
    return this.chain;
  }

  /**
   * Calculate the count of atoms of the longest aliphatic chain in the supplied
   * {@link IAtomContainer}.
   *
   * <p>
   * The method require one parameter: if checkRingSyste is true the
   * CDKConstant.ISINRING will be set
   * </p>
   *
   * @param atomContainer
   *          The {@link IAtomContainer} for which this descriptor is to be
   *          calculated
   * @return the number of atoms in the longest aliphatic chain of this
   *         AtomContainer
   * @see #setParameters
   */
  public void calculate(final IAtomContainer atomContainer) {

    final IAtomContainer container = atomContainer;
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
    }

    int longestChainAtomsCount = 0;
    int tmpLongestChainAtomCount;
    List<IAtom> startSphere;
    List<IAtom> path;

    for (int i = 0; i < container.getAtomCount(); i++) {
      container.getAtom(i).setFlag(CDKConstants.VISITED, false);
    }

    for (int i = 0; i < container.getAtomCount(); i++) {
      final IAtom atomi = container.getAtom(i);
      if (atomi.getSymbol().equals("H")) {
        continue;
      }

      if (!atomi.getFlag(CDKConstants.ISAROMATIC)
          && !atomi.getFlag(CDKConstants.ISINRING)
          && atomi.getSymbol().equals("C")
          && !atomi.getFlag(CDKConstants.VISITED)) {

        startSphere = new ArrayList<IAtom>();
        path = new ArrayList<IAtom>();
        startSphere.add(atomi);
        try {
          this.breadthFirstSearch(container, startSphere, path);
        } catch (final CDKException e) {
          return;
        }
        final IAtomContainer aliphaticChain = this.createAtomContainerFromPath(
            container,
            path);
        if (aliphaticChain.getAtomCount() >= this.minLength) {
          final double[][] conMat = ConnectionMatrix.getMatrix(aliphaticChain);
          final Integer[][] pathMatrix =
              new Integer[conMat.length][conMat.length];
          final int[][] apsp = this.computeFloydApsp(conMat, pathMatrix);
          final int[] pathCoordinates = new int[] {0, 0};
          tmpLongestChainAtomCount = this.getLongestChainPath(apsp,
              pathCoordinates);
          final IAtomContainer longestAliphaticChain = this
              .createAtomContainerFromPath(
                  aliphaticChain,
                  this.longestPath(pathMatrix, pathCoordinates[0],
                      pathCoordinates[1],
                      aliphaticChain));
          // The longest chain container.
          this.chain.add(longestAliphaticChain);
          if (tmpLongestChainAtomCount >= longestChainAtomsCount) {
            longestChainAtomsCount = tmpLongestChainAtomCount;
          }
        }
      }
    }
  }

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


  public int[][] computeFloydApsp(final double[][] costMatrix,
      final Integer[][] pathMatrix) {
    final int nrow = costMatrix.length;
    final int[][] distMatrix = new int[nrow][nrow];
    // logger.debug("Matrix size: " + n);
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

  private void printAtomMatrix(final IAtom[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] != null) {
          System.out.println("i,j: " + i + "," + j + ": "
              + matrix[i][j].getID());
        }
      }
    }
  }

  private void printIntMatrix(final int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
      }
    }
  }

  private void printIntMatrix(final Integer[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
      }
    }
  }

  private void printDoubMatrix(final double[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
      }
    }
  }

  private void printAtomList(final List<IAtom> atoms) {
    int count = 0;
    for (final IAtom atom : atoms) {
      System.out.println("Atom " + count + ": " + atom.getID());
      count++;
    }
  }

  private int getLongestChainPath(final int[][] apsp, final int[] path) {
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
    return longestPath;
  }

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
        if (bond != null) {
          if (!aliphaticChain.contains(secondAtom)) {
            aliphaticChain.addAtom(secondAtom);
          }
          if (!aliphaticChain.contains(bond)) {
            aliphaticChain.addBond(bond);
          }
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
  private void breadthFirstSearch(final IAtomContainer container,
      final List<IAtom> sphere,
      final List<IAtom> path) throws CDKException {
    IAtom nextAtom;
    final List<IAtom> newSphere = new ArrayList<IAtom>();
    for (final IAtom atom : sphere) {
      final List<IBond> bonds = container.getConnectedBondsList(atom);
      for (final IBond bond : bonds) {
        nextAtom = bond.getConnectedAtom(atom);
        if ((!nextAtom.getFlag(CDKConstants.ISAROMATIC) && !nextAtom
            .getFlag(CDKConstants.ISINRING) & nextAtom.getSymbol().equals("C"))
            & !nextAtom.getFlag(CDKConstants.VISITED)) {
          path.add(nextAtom);
          nextAtom.setFlag(CDKConstants.VISITED, true);
          if (container.getConnectedBondsCount(nextAtom) > 1) {
            newSphere.add(nextAtom);
          }
        } else {
          nextAtom.setFlag(CDKConstants.VISITED, true);
        }
      }
    }
    if (newSphere.size() > 0) {
      this.breadthFirstSearch(container, newSphere, path);
    }
  }

}
