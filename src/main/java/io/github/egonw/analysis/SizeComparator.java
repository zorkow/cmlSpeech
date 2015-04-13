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
 * @file   SizeComparator.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Aug  4 19:39:56 2014
 *
 * @brief Rich Structure comparison methods wrt. size of atom sets.
 *
 */

//

package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;

/**
 * Compare atom sets by number of atoms contained.
 */

public class SizeComparator extends DefaultComparator {

  @Override
  public int compare(final RichAtomSet set1, final RichAtomSet set2) {
    return -1
        * Integer.compare(set1.getStructure().getAtomCount(), set2
            .getStructure().getAtomCount());
  }

}
