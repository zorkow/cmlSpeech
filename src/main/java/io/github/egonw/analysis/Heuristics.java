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
 * @file   Heuristics.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Jan 17 18:24:15 2015
 * 
 * @brief  Combines basic comparators via a heuristic.
 * 
 * 
 */

//
package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichChemObject;

import java.util.Comparator;
import io.github.egonw.structure.RichStructure;
import io.github.egonw.structure.RichAtom;
import java.util.Arrays;

/**
 * Combines basic comparators as a single heuristic.
 */

public class Heuristics extends DefaultComparator {

    private String[] heuristics;
    private Comparator<RichStructure<?>> weight = new WeightComparator();
    private Comparator<RichStructure<?>> type = new TypeComparator();
    private Comparator<RichStructure<?>> size = new SizeComparator();

    public Heuristics(String heuristic) {
        // TODO (sorge) Do something with non-existent heuristics.
        this.heuristics = heuristic == "" ?
            new String[] {"type", "weight", "size"} : heuristic.split(",");
    }
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        Integer result = 0;
        for (String heuristic : this.heuristics) {
            switch (heuristic) {
            case "size":
                result = this.size.compare(set1, set2);
                break;
            case "type":
                result = this.type.compare(set1, set2);
                break;
            case "weight":
                result = this.weight.compare(set1, set2);
                break;
            default:
                break;
            }
            if (result != 0) {
                break;
            }
        }
        return result;
    }

    public int compare(RichAtom atom1, RichAtom atom2) {
        if (Arrays.asList(this.heuristics).contains("type")) {
            return this.weight.compare(atom1, atom2);
        }
        return 0;
    }
}
