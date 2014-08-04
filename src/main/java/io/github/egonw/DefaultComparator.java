/**
 * @file   DefaultComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Aug  4 19:40:18 2014
 * 
 * @brief  Abstract superclass for all comparators on Rich Structures.
 * 
 * 
 */

//
package io.github.egonw;

/**
 * The base compare method redistributes wrt to object sub classes.
 * It should not be overwritten!
 */

abstract class DefaultComparator<RichChemObject> implements RichStructureComparator<RichChemObject> {


    public int compare(RichChemObject obj1, RichChemObject obj2) {
        if (obj1 instanceof RichAtomSet && obj2 instanceof RichAtomSet) {
            return this.compare((RichAtomSet)obj1, (RichAtomSet)obj2);
        }
        if (obj1 instanceof RichAtomSet && obj2 instanceof RichAtom) {
            return this.compare((RichAtomSet)obj1, (RichAtom)obj2);
        }
        if (obj1 instanceof RichAtom && obj2 instanceof RichAtomSet) {
            return this.compare((RichAtom)obj1, (RichAtomSet)obj2);
        }
        if (obj1 instanceof RichAtom && obj2 instanceof RichAtom) {
            return this.compare((RichAtom)obj1, (RichAtom)obj2);
        }
        return 0;
    };


    public int compare(RichAtomSet set1, RichAtomSet set2) {
        return -1 * Integer.compare(set1.getStructure().getAtomCount(), 
                                    set2.getStructure().getAtomCount());
    }

    public int compare(RichAtomSet set1, RichAtom atom2) {
        return -1;
    }

    public int compare(RichAtom atom1, RichAtomSet set2) {
        return 1;
    }

    public int compare(RichAtom atom1, RichAtom atom2) {
        return 0;
    }

}
