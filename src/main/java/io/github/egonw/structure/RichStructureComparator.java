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
 * @file   RichStructureComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Aug  4 19:42:18 2014
 * 
 * @brief Interface for RichStructure comparators. It ensures that we can
 *        compare rich atoms and atomsets.
 * 
 * 
 */

//
package io.github.egonw.structure;

import java.util.Comparator;

/**
 * All implementing classes have to ensure that ordering is with respect to
 * "interestingness" of structures. The more interesting structure is therefore
 * less than the less interesting one. Or, for example, the larger structure is
 * less than the smaller structure!
 */

public interface RichStructureComparator<T> extends Comparator<RichStructure<?>> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2);

    public int compare(RichAtomSet set1, RichAtom atom2);

    public int compare(RichAtom atom1, RichAtomSet set2);

    public int compare(RichAtom atom1, RichAtom atom2);

}
