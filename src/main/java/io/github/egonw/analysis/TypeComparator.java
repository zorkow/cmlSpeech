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
 * @file   TypeComparator.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Aug  4 19:39:56 2014
 * 
 * @brief Rich Structure comparison methods wrt. types of atom sets. It
 *        basically promotes rings.
 * 
 * 
 */

//

package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichSetType;

/**
 * Rich Structure comparison methods wrt. types of atom sets. Promotes rings
 * over chains over functional groups.
 */

public class TypeComparator extends DefaultComparator {

  @Override
  public int compare(RichAtomSet set1, RichAtomSet set2) {
    RichSetType typeA = set1.getType();
    RichSetType typeB = set2.getType();
    if (typeA == RichSetType.FUNCGROUP
        && (typeB == RichSetType.ALIPHATIC || typeB == RichSetType.FUSED
            || typeB == RichSetType.ISOLATED || typeB == RichSetType.SMALLEST)) {
      return 1;
    }
    if (typeA == RichSetType.ALIPHATIC
        && (typeB == RichSetType.FUSED || typeB == RichSetType.ISOLATED
            || typeB == RichSetType.SMALLEST)) {
      return 1;
    }
    if (typeB == RichSetType.FUNCGROUP
        && (typeA == RichSetType.ALIPHATIC || typeA == RichSetType.FUSED
            || typeA == RichSetType.ISOLATED || typeA == RichSetType.SMALLEST)) {
      return -1;
    }
    if (typeB == RichSetType.ALIPHATIC
        && (typeA == RichSetType.FUSED || typeA == RichSetType.ISOLATED
            || typeA == RichSetType.SMALLEST)) {
      return -1;
    }
    return 0;
  }

}
