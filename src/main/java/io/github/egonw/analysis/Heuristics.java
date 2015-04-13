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
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Jan 17 18:24:15 2015
 *
 * @brief  Combines basic comparators via a heuristic.
 *
 *
 */

//

package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichStructure;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Combines basic comparators as a single heuristic.
 */

public class Heuristics extends DefaultComparator {

  private final String[] heuristics;
  private final Comparator<RichStructure<?>> weight = new WeightComparator();
  private final Comparator<RichStructure<?>> type = new TypeComparator();
  private final Comparator<RichStructure<?>> size = new SizeComparator();

  /**
   * Constructor of heuristic comparison.
   *
   * @param heuristic
   *          A string representing a list of heuristics.
   */
  public Heuristics(final String heuristic) {
    // TODO (sorge) Do something with non-existent heuristics.
    this.heuristics = heuristic == "" ? new String[] {"type", "weight", "size"}
    : heuristic.split(",");
  }

  @Override
  public int compare(final RichAtomSet set1, final RichAtomSet set2) {
    Integer result = 0;
    for (final String heuristic : this.heuristics) {
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

  @Override
  public int compare(final RichAtom atom1, final RichAtom atom2) {
    if (Arrays.asList(this.heuristics).contains("type")) {
      return this.weight.compare(atom1, atom2);
    }
    return 0;
  }
}
