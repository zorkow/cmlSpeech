/**
 * @file   TypeComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Aug  4 19:39:56 2014
 * 
 * @brief Rich Structure comparison methods wrt. types of atom sets. It
 *        basically promotes rings.
 * 
 * 
 */

//
package io.github.egonw;

/**
 *
 */

public class TypeComparator extends DefaultComparator<RichChemObject> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        RichAtomSet.Type typeA = set1.getType();
        RichAtomSet.Type typeB = set2.getType();
        if (typeA == RichAtomSet.Type.ALIPHATIC && 
            (typeB == RichAtomSet.Type.FUSED || 
             typeB == RichAtomSet.Type.ISOLATED ||
             typeB == RichAtomSet.Type.SMALLEST)) {
            return 1;
        }
        if (typeB == RichAtomSet.Type.ALIPHATIC && 
            (typeA == RichAtomSet.Type.FUSED || 
             typeA == RichAtomSet.Type.ISOLATED ||
             typeA == RichAtomSet.Type.SMALLEST)) {
            return -1;
        }
        return 0;
    }

}
