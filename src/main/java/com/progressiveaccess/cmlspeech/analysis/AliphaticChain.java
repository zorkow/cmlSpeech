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
// TODO (sorge): This file is badly in need of refactoring!
//

package com.progressiveaccess.cmlspeech.analysis;

import com.google.common.collect.Lists;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that returns a list of aliphatic chains in a given container.
 *
 */
// TODO (sorge): Refactor this to return the chains rather than a descriptor.
public class AliphaticChain extends AbstractMolecularDescriptor implements
    IMolecularDescriptor {

  private boolean checkRingSystem = true;

  private static final String[] names = { "allLAC" };

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
   * Returns a <code>Map</code> which specifies which descriptor is implemented
   * by this class.
   *
   * <p>
   * These fields are used in the map:
   * <ul>
   * <li>Specification-Reference: refers to an entry in a unique dictionary
   * <li>Implementation-Title: anything
   * <li>Implementation-Identifier: a unique identifier for this version of this
   * class
   * <li>Implementation-Vendor: CDK, JOELib, or anything else
   * </ul>
   * </p>
   *
   * @return An object containing the descriptor specification
   */
  @Override
  @TestMethod("testGetSpecification")
  public DescriptorSpecification getSpecification() {
    return new DescriptorSpecification(
        "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#longestAliphaticChain",
        this.getClass().getName(), "The Chemistry Development Kit");
  }

  /**
   * Sets the parameters attribute of the LongestAliphaticChainDescriptor
   * object.
   *
   * <p>
   * This descriptor takes one parameter, which should be Boolean to indicate
   * whether aromaticity has been checked (TRUE) or not (FALSE).
   * </p>
   *
   * @param params
   *          The new parameters value
   * @exception CDKException
   *              if more than one parameter or a non-Boolean parameter is
   *              specified
   * @see #getParameters
   */
  @Override
  @TestMethod("testSetParameters_arrayObject")
  public void setParameters(final Object[] params) throws CDKException {
    if (params.length > 1) {
      throw new CDKException(
          "AliphaticChainDescriptor only expects one parameter");
    }
    if (!(params[0] instanceof Boolean)) {
      throw new CDKException("Both parameters must be of type Boolean");
    }
    // ok, all should be fine
    this.checkRingSystem = (Boolean) params[0];
  }

  /**
   * Gets the parameters attribute of the AliphaticChainDescriptor object.
   *
   * @return The parameters value
   * @see #setParameters
   */
  @Override
  @TestMethod("testGetParameters")
  public Object[] getParameters() {
    // return the parameters as used for the descriptor calculation
    final Object[] params = new Object[1];
    params[0] = this.checkRingSystem;
    return params;
  }

  @Override
  @TestMethod(value = "testNamesConsistency")
  public String[] getDescriptorNames() {
    return names;
  }

  private DescriptorValue getDummyDescriptorValue(final Exception exception) {
    return new DescriptorValue(this.getSpecification(),
        this.getParameterNames(),
        this.getParameters(), new IntegerResult((int) Double.NaN),
        this.getDescriptorNames(), exception);
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
  @Override
  @TestMethod("testCalculate_IAtomContainer")
  public DescriptorValue calculate(final IAtomContainer atomContainer) {

    final IAtomContainer container = atomContainer;
    IRingSet rs;
    if (this.checkRingSystem) {
      try {
        rs = new SpanningTree(container).getBasicRings();
      } catch (final NoSuchAtomException e) {
        return this.getDummyDescriptorValue(e);
      }
      for (int i = 0; i < container.getAtomCount(); i++) {
        if (rs.contains(container.getAtom(i))) {
          container.getAtom(i).setFlag(CDKConstants.ISINRING, true);
        }
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
          return this.getDummyDescriptorValue(e);
        }
        final IAtomContainer aliphaticChain = this.createAtomContainerFromPath(
            container,
            path);
        if (aliphaticChain.getAtomCount() >= this.minLength) {
          final double[][] conMat = ConnectionMatrix.getMatrix(aliphaticChain);
          final Integer[][] pathMatrix = new Integer[conMat.length][conMat.length];
          final int[][] apsp = this.computeFloydApsp(conMat, pathMatrix);
          final int[] pathCoordinates = new int[] { 0, 0 };
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

    return new DescriptorValue(this.getSpecification(),
        this.getParameterNames(),
        this.getParameters(), new IntegerResult(longestChainAtomsCount),
        this.getDescriptorNames());
  }

  public List<IAtom> longestPath(final Integer[][] pathMatrix, int pathStart,
      final int pathEnd, final IAtomContainer chain) {
    final List<IAtom> path = new ArrayList<>();
    if (pathMatrix[pathStart][pathEnd] == null) {
      return path;
    }
    final List<IAtom> chainAtoms = Lists.newArrayList(chain.atoms());
    path.add(chainAtoms.get(pathStart));
    while (pathStart != pathEnd) {
      pathStart = pathMatrix[pathStart][pathEnd];
      path.add(chainAtoms.get(pathStart));
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
          distMatrix[i][j] = 999999999;
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
   * Returns the specific type of the DescriptorResult object.
   * <p/>
   * The return value from this method really indicates what type of result will
   * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue}
   * object. Note that the same result can be achieved by interrogating the
   * {@link org.openscience.cdk.qsar.DescriptorValue} object; this method allows
   * you to do the same thing, without actually calculating the descriptor.
   *
   * @return an object that implements the
   *         {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface
   *         indicating the actual type of values returned by the descriptor in
   *         the {@link org.openscience.cdk.qsar.DescriptorValue} object
   */
  @Override
  @TestMethod("testGetDescriptorResultType")
  public IDescriptorResult getDescriptorResultType() {
    return new IntegerResult(1);
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
   * @exception org.openscience.cdk.exception.CDKException
   *              Description of the Exception
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

  /**
   * Gets the parameterNames attribute of the AliphaticChainDescriptor object.
   *
   * @return The parameterNames value
   */
  @Override
  @TestMethod("testGetParameterNames")
  public String[] getParameterNames() {
    final String[] params = new String[1];
    params[0] = "checkRingSystem";
    return params;
  }

  /**
   * Gets the parameterType attribute of the AliphaticChainDescriptor object.
   *
   * @param name
   *          Description of the Parameter
   * @return An Object of class equal to that of the parameter being requested
   */
  @Override
  @TestMethod("testGetParameterType_String")
  public Object getParameterType(final String name) {
    return true;
  }
}
