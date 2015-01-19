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
package io.github.egonw;

import java.util.Comparator;

/**
 * All implementing classes have to ensure that ordering is with respect to
 * "interestingness" of structures. The more interesting structure is therefore
 * less than the less interesting one. Or, for example, the larger structure is
 * less than the smaller structure!
 */

public interface RichStructureComparator<RichChemObject> extends Comparator<RichChemObject> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2);

    public int compare(RichAtomSet set1, RichAtom atom2);

    public int compare(RichAtom atom1, RichAtomSet set2);

    public int compare(RichAtom atom1, RichAtom atom2);

}
