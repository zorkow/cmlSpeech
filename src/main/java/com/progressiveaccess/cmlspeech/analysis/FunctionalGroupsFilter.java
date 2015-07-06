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

import com.google.common.collect.Sets;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtering functions to restrict ourselves to the most interesting functional
 * groups. In particular we filter with respect to overlap with other already
 * identified atom sets.
 */

public class FunctionalGroupsFilter {

  private final List<RichAtomSet> existingSets;
  private final Map<String, IAtomContainer> newSets;
  private final Map<String, IAtomContainer> resultSets =
      new HashMap<String, IAtomContainer>();
  // The set that is reduced to distill the interesting functional groups.
  private final List<RichFunctionalGroup> workingSets =
      new ArrayList<RichFunctionalGroup>();

  private final Integer minimalSize = 1;
  private final Integer minimalOverlap = 1;

  FunctionalGroupsFilter(final List<RichAtomSet> existing,
      final Map<String, IAtomContainer> groups) {
    this.existingSets = existing.stream()
        .filter(as -> as.getType() != RichSetType.SMALLEST)
        .collect(Collectors.toList());
    this.newSets = groups;
  }

  // Heuristics to implement:
  // + largest group subsumes subsets
  // - minimal overlap with others.
  // - with same elements: shortest name.
  // - when permutation of elements with same name.
  //
  // - when we have similar content, use the one that has the least overlap.
  //
  // + discard everything of length 1 that has overlap.
  // + eliminate when overlap of two with a single existing set.
  //
  // - At least one (or two?) elements not in another container.
  //

  private boolean considerSize(final IAtomContainer container) {
    return container.getAtomCount() >= this.minimalSize;
  }

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

  private void subsumeSubsets() {
    if (this.workingSets.isEmpty()) {
      return;
    }
    Integer count = 0;
    while (this.workingSets.size() > count) {
      final RichFunctionalGroup outer = this.workingSets.get(count++);
      Integer steps = this.workingSets.size() - 1;
      while (steps >= count) {
        final RichFunctionalGroup inner = this.workingSets.get(steps--);
        if (Sets.difference(inner.getComponents(), outer.getComponents())
            .isEmpty()) {
          this.workingSets.remove(inner);
        }
      }
    }
  }

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

  public Map<String, IAtomContainer> filter() {
    for (final Map.Entry<String, IAtomContainer> entry : this.newSets
        .entrySet()) {
      final IAtomContainer set = entry.getValue();
      if (this.considerSize(set) && this.considerOverlap(set)) {
        this.workingSets.add(new RichFunctionalGroup(set, entry.getKey()));
      }
    }
    // sort by size
    Collections.sort(this.workingSets, new SizeAndNameComparator());
    //this.subsumeSubsets();
    for (final RichFunctionalGroup set : this.workingSets) {
      final String id = set.getId();
      // TODO (sorge) This test should be scrutinised:
      // 1. It removes potentially interesting functional groups.
      // Chemical question is:
      // Should we present functional groups that have
      // multiple atoms as overlap.
      // 2. It does redundant work, as it rechecks overlap as done
      // in the previous loop.
      if (this.considerOverlap(set.getStructure())) {
        this.resultSets.put(id, this.newSets.get(id));
        this.existingSets.add(set);
      }
    }
    return this.resultSets;
  }

}
