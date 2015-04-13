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
 * @file   DefaultComparator.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Aug  4 19:40:18 2014
 *
 * @brief  Abstract superclass for all comparators on Rich Structures.
 *
 *
 */

//

package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichStructure;
import io.github.egonw.structure.RichStructureComparator;

/**
 * The base compare method redistributes wrt to object sub classes. It should
 * not be overwritten!
 */

abstract class DefaultComparator implements
RichStructureComparator<RichStructure<?>> {

  @Override
  public int compare(final RichStructure<?> obj1, final RichStructure<?> obj2) {
    if (obj1 instanceof RichAtomSet && obj2 instanceof RichAtomSet) {
      return this.compare((RichAtomSet) obj1, (RichAtomSet) obj2);
    }
    if (obj1 instanceof RichAtomSet && obj2 instanceof RichAtom) {
      return this.compare((RichAtomSet) obj1, (RichAtom) obj2);
    }
    if (obj1 instanceof RichAtom && obj2 instanceof RichAtomSet) {
      return this.compare((RichAtom) obj1, (RichAtomSet) obj2);
    }
    if (obj1 instanceof RichAtom && obj2 instanceof RichAtom) {
      return this.compare((RichAtom) obj1, (RichAtom) obj2);
    }
    return 0;
  }

  @Override
  public abstract int compare(RichAtomSet set1, RichAtomSet set2);

  @Override
  public int compare(final RichAtomSet set1, final RichAtom atom2) {
    return -1;
  }

  @Override
  public int compare(final RichAtom atom1, final RichAtomSet set2) {
    return 1;
  }

  @Override
  public int compare(final RichAtom atom1, final RichAtom atom2) {
    return 0;
  }

}
