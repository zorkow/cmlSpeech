/**
 * @file   TypeComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Aug  4 19:39:56 2014
 * 
 * @brief Rich Structure comparison methods wrt. molecular weight of atom sets.
 * 
 */

//
package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichChemObject;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Comparison of atom sets with respect to their molecular weight.
 */

public class WeightComparator extends DefaultComparator<RichChemObject> {
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        IAtomContainer container1 = set1.getStructure();
        IAtomContainer container2 = set2.getStructure();
        double weightA = AtomContainerManipulator.getNaturalExactMass(container1);
        double weightB = AtomContainerManipulator.getNaturalExactMass(container2);

        return (int)Math.signum(weightB - weightA);
    }

}
