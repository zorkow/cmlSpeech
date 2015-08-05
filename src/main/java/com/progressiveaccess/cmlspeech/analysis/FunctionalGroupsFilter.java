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
 * @file   FunctionalGroupsFilter.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jan 20 01:12:31 2015
 *
 * @brief Filters for functional groups by interestingness.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.analysis;

import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichFunctionalGroup;
import com.progressiveaccess.cmlspeech.structure.RichSetType;
import com.progressiveaccess.cmlspeech.structure.RichStructure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.TreeMap;

/**
 * Filtering functions to restrict ourselves to the most interesting functional
 * groups. In particular we filter with respect to overlap with other already
 * identified atom sets.
 */

public class FunctionalGroupsFilter {

  private final List<RichAtomSet> existingSets;
  private final Map<String, IAtomContainer> newSets;
  private final Map<String, IAtomContainer> resultSets =
      new TreeMap<String, IAtomContainer>();
  // The set that is reduced to distill the interesting functional groups.
  private final SortedSet<RichFunctionalGroup> workingSets =
      new TreeSet<RichFunctionalGroup>(new SizeAndNameComparator());
  private final Integer minimalOverlap = 1;


  /**
   * Provides a functional group filter.
   *
   * @param existing
   *          The list of already existing atom sets, i.e., rings and chains.
   * @param groups
   *          The mapping of potential functional groups.
   */
  FunctionalGroupsFilter(final List<RichAtomSet> existing,
      final Map<String, IAtomContainer> groups) {
    this.existingSets = existing.stream()
        .filter(as -> as.getType() != RichSetType.SMALLEST)
        .collect(Collectors.toList());
    this.newSets = groups;
  }


  /**
   * Predicate that considers overlap of a functional group with other atoms
   * sets.
   *
   * @param container
   *          The functional group to consider.
   *
   * @return True if the overlap with other sets is at most one atom per set and
   *      it has at least one atom without overlap.
   */
  // Heuristics to implement:
  // + largest group subsumes subsets
  // - minimal overlap with others.
  // - with same elements: shortest name.
  // - when permutation of elements with same name.
  //
  // TODO (sorge)
  // - when we have similar content, use the one that has the least overlap.
  //
  // + discard everything of length 1 that has overlap.
  // + eliminate when overlap of two with a single existing set.
  //
  // - At least one elements not in another container.
  //
  private boolean considerOverlap(final IAtomContainer container) {
    for (final RichAtomSet old : this.existingSets) {
      Integer count = 0;
      final Set<String> components = old.getComponents();
      for (final IAtom atom : container.atoms()) {
        if (components.contains(atom.getID())) {
          count++;
        }
        if (count > this.minimalOverlap) {
          return false;
        }
      }
      if (container.getAtomCount() == 1 && count == 1) {
        return false;
      }
    }
    return true;
  }


  /**
   * Comparison by size of structures and length of names, prefering groups with
   * short names.
   */
  private class SizeAndNameComparator extends DefaultComparator {

    private final Comparator<RichStructure<?>> sizeComparator =
        new SizeComparator();

    @Override
    public int compare(final RichAtomSet as1, final RichAtomSet as2) {
      Integer size = this.sizeComparator.compare(as1, as2);
      if (size != 0) {
        return size;
      }
      final String name1 = as1.getId().split("-")[0];
      final String name2 = as2.getId().split("-")[0];
      final String[] parts1 = name1.split(" ");
      final String[] parts2 = name2.split(" ");
      size = Integer.compare(parts1.length, parts2.length);
      if (size != 0) {
        return size;
      }
      return Integer.compare(name1.length(), name2.length());
    }
  }


  /**
   * The actual filter for the map of functional groups. It maintains a list of
   * potential functional groups ordered by size, as to guarantee that the
   * larger ones subsume the smaller ones.
   *
   * @return The cleaned map.
   */
  public Map<String, IAtomContainer> filter() {
    for (final Map.Entry<String, IAtomContainer> entry : this.newSets
        .entrySet()) {
      final IAtomContainer set = entry.getValue();
      if (this.considerOverlap(set)) {
        this.workingSets.add(new RichFunctionalGroup(set, entry.getKey()));
      }
    }
    this.existingSets.clear();
    for (final RichFunctionalGroup set : this.workingSets) {
      final String id = set.getId();
      // TODO (sorge) This test should be scrutinised:
      // It removes potentially interesting functional groups.
      // Chemical question is:
      // Should we present functional groups that have
      // multiple atoms as overlap.
      if (this.considerOverlap(set.getStructure())) {
        this.resultSets.put(id, this.newSets.get(id));
        this.existingSets.add(set);
      }
    }
    return this.resultSets;
  }

}
