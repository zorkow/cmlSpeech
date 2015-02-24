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
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Jan 20 01:12:31 2015
 * 
 * @brief Filters for functional groups by interestingness.
 * 
 * 
 */

//
package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichChemObject;
import io.github.egonw.structure.RichSetType;

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

    private List<RichAtomSet> existingSets;
    private Map<String, IAtomContainer> newSets;
    private Map<String, IAtomContainer> resultSets = new HashMap<String, IAtomContainer>();
    // The set that is reduced to distil the interesting functional groups.
    private List<RichAtomSet> workingSets = new ArrayList<RichAtomSet>();

    static private Integer minimalSize = 2;
    static private Integer minimalOverlap = 1;
    
    FunctionalGroupsFilter(List<RichAtomSet> existing, Map<String, IAtomContainer> groups) {
        existingSets = existing.stream().
            filter(as -> as.type != RichSetType.SMALLEST).
            collect(Collectors.toList());
        newSets = groups;
    }
    // Heuristics to implements:
    // + largest group subsumes subsets
    // - minimal overlap with others.
    // - with same elements: shortest name.
    // - when permutation of elements with same name.
    //
    // - when we have similar content, use the one that has the least overlap.
    //
    // + discard everything of length 1.
    // + eliminate when overlap of two with a single existing set.
    //
    // - At least one (or two?) elements not in another container.
    //

    private boolean considerSize(IAtomContainer container) {
        return container.getAtomCount() >= minimalSize;
    }
    

    private boolean considerOverlap(IAtomContainer container) {
        for (RichAtomSet old : existingSets) {
            Integer count = 0;
            Set<String> components = old.getComponents();
            for (IAtom atom : container.atoms()) {
                if (components.contains(atom.getID())) {
                    count++;
                }
                if (count > minimalOverlap) {
                    return false;
                }
            }
        }
        return true;
    }


    private void subsumeSubsets() {
        if (workingSets.isEmpty()) {
            return;
        }
        Integer count = 0;
        while (workingSets.size() > count) {
            RichAtomSet outer = workingSets.get(count++);
            Integer i = workingSets.size() - 1;
            while (i >= count) {
                RichAtomSet inner = workingSets.get(i--);
                if (Sets.difference(inner.getComponents(), outer.getComponents()).isEmpty()) {
                    workingSets.remove(inner);
                }
            }
        }
    }

    private class SizeAndNameComparator extends DefaultComparator<RichChemObject> {

        private Comparator<RichChemObject> sizeComparator = new SizeComparator();
        
        public int compare(RichAtomSet as1, RichAtomSet as2) {
            Integer size = sizeComparator.compare(as1, as2);
            if (size != 0) {
                return size;
            }
            String name1 = as1.getId().split("-")[0];
            String name2 = as2.getId().split("-")[0];
            String[] parts1 = name1.split(" ");
            String[] parts2 = name2.split(" ");
            size = Integer.compare(parts1.length, parts2.length);
            if (size != 0) {
                return size;
            }
            return Integer.compare(name1.length(), name2.length());
        }
    }

    
    public Map<String, IAtomContainer> filter() {
        for (Map.Entry<String, IAtomContainer> entry : newSets.entrySet()) {
            IAtomContainer set = entry.getValue();
            if (considerSize(set) &&
                considerOverlap(set)) {
                workingSets.add(new RichAtomSet(set, RichSetType.FUNCGROUP, entry.getKey()));
            }
        }

        // sort by size
        Collections.sort(workingSets, new SizeAndNameComparator());
        subsumeSubsets();
        
        for (RichAtomSet set : workingSets){
            String id = set.getId();
            resultSets.put(id, newSets.get(id));
        }
        return resultSets;
    }


    

}
