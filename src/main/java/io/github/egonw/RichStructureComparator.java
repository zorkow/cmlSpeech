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
 *
 */

public interface RichStructureComparator<RichChemObject> extends Comparator<RichChemObject> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2);

    public int compare(RichAtomSet set1, RichAtom atom2);

    public int compare(RichAtom atom1, RichAtomSet set2);

    public int compare(RichAtom atom1, RichAtom atom2);

}
