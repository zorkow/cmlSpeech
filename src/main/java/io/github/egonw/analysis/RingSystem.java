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
 * @file   RingSystem.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Aug  2 01:56:57 2014
 * 
 * @brief  Computing information on ring systems.
 * 
 * 
 */

//

package io.github.egonw.analysis;

import io.github.egonw.base.CMLNameComparator;
import io.github.egonw.base.Cli;

import com.google.common.collect.Lists;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.ringsearch.SSSRFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Computes ring systems using ring search algorithms.
 */

public class RingSystem {

  RingSearch ringSearch = null;

  public RingSystem(IAtomContainer container) {
    this.ringSearch = new RingSearch(container);
  }

  /**
   * Computes Isolated rings.
   */
  public List<IAtomContainer> isolatedRings() {
    return this.ringSearch.isolatedRingFragments();
  }

  /**
   * Computes fused rings without subsystems.
   */
  public List<IAtomContainer> fusedRings() {
    return this.ringSearch.fusedRingFragments();
  }

  /**
   * Computes fused rings and their subsystems.
   * 
   * @param fusedRing
   *          A fused ring system.
   */
  public List<IAtomContainer> subRings(IAtomContainer fusedRing) {
    return this.subRings(fusedRing,
        Cli.hasOption("sssr") ? this::smallestSubRings : this::sssrSubRings);
  }

  /**
   * Computes fused rings and their subsystems.
   * 
   * @param fusedRing
   *          A fused ring system.
   * @param subRingMethod
   *          Method to compute subrings.
   */
  public List<IAtomContainer> subRings(IAtomContainer fusedRing,
      Function<IAtomContainer, List<IAtomContainer>> subRingMethod) {
    return subRingMethod.apply(fusedRing);
  }

  /**
   * Predicate that tests if a particular ring has no other ring as proper
   * subset.
   * 
   * This does NOT produce the list of essential rings! It retains rings that
   * are rims of ring compositions of three of more rings. Example is
   * 1H-indeno[7,1-bc]azepine.
   * 
   * @param ring
   *          The ring to be tested.
   * @param restRings
   *          The other rings (possibly including the first ring).
   * 
   * @return True if ring has smallest coverage.
   */
  // This is quadratic and should be done better!
  // All the iterator to list operations should be done exactly once!
  private static boolean isSmallest(IAtomContainer ring,
      List<IAtomContainer> restRings) {
    List<IAtom> ringAtoms = Lists.newArrayList(ring.atoms());
    for (IAtomContainer restRing : restRings) {
      if (ring == restRing) {
        continue;
      }
      List<IAtom> restRingAtoms = Lists.newArrayList(restRing.atoms());
      if (ringAtoms.containsAll(restRingAtoms)) {
        return false;
      }
      ;
    }
    return true;
  };

  /**
   * Method to compute smallest rings via subset coverage.
   * 
   * @param ring
   *          Fused ring to be broken up.
   * 
   * @return Subrings as atom containers.
   */
  private List<IAtomContainer> smallestSubRings(IAtomContainer ring) {
    AllRingsFinder arf = new AllRingsFinder();
    List<IAtomContainer> subRings = new ArrayList<IAtomContainer>();
    IRingSet rs;
    try {
      rs = arf.findAllRings(ring);
    } catch (CDKException e) {
      return subRings;
    }
    List<IAtomContainer> allRings = Lists.newArrayList(rs.atomContainers());
    for (IAtomContainer subRing : allRings) {
      if (isSmallest(subRing, allRings)) {
        subRings.add(subRing);
      }
    }
    return subRings;
  };

  protected class AtomComparator implements Comparator<IAtom> {
    public int compare(IAtom atom1, IAtom atom2) {
      CMLNameComparator comparator = new CMLNameComparator();
      return comparator.compare(atom1.getID(), atom2.getID());
    }
  }

  protected class RingComparator implements Comparator<IAtomContainer> {
    public int compare(IAtomContainer container1, IAtomContainer container2) {
      AtomComparator comparator = new AtomComparator();
      SortedSet<IAtom> atoms1 = new TreeSet<>(comparator);
      SortedSet<IAtom> atoms2 = new TreeSet<>(comparator);
      container1.atoms().forEach(x -> atoms1.add(x));
      container2.atoms().forEach(x -> atoms2.add(x));
      Iterator<IAtom> iterator1 = atoms1.iterator();
      Iterator<IAtom> iterator2 = atoms2.iterator();
      while (iterator1.hasNext() && iterator2.hasNext()) {
        int result = comparator.compare(iterator1.next(), iterator2.next());
        if (result != 0)
          return result;
      }
      return 0;
    }
  }

  /**
   * Method to compute smallest rings via SSSR finder.
   * 
   * @param ring
   *          Fused ring to be broken up.
   * 
   * @return Subrings as atom containers.
   */
  private List<IAtomContainer> sssrSubRings(IAtomContainer ring) {
    SSSRFinder sssr = new SSSRFinder(ring);
    IRingSet essentialRings = sssr.findSSSR();
    // We need to sort the subrings as essential ring finding is not
    // necessarily deterministic.
    SortedSet<IAtomContainer> subRings = new TreeSet<>(new RingComparator());
    for (IAtomContainer subRing : essentialRings.atomContainers()) {
      subRings.add(subRing);
    }
    return Lists.newArrayList(subRings);
  }

}
