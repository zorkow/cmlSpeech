/**
 * @file   SizeComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Aug  4 19:39:56 2014
 * 
 * @brief Rich Structure comparison methods wrt. size of atom sets.
 * 
 */

//
package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichChemObject;

/**
 * Compare atom sets by number of atoms contained.
 */

public class SizeComparator extends DefaultComparator<RichChemObject> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        return -1 * Integer.compare(set1.getStructure().getAtomCount(), 
                                    set2.getStructure().getAtomCount());
    }

}
