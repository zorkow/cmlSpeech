package io.github.egonw;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class Heuristics extends DefaultComparator<RichChemObject> {

    private boolean h1;
    private boolean h2;
    private boolean h3;

    public Heuristics(boolean h1, boolean h2, boolean h3) {
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
    }
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        if (this.h1) {
            return compare1(set1, set2);
        }
        if (this.h2) {
            return compare2(set1, set2);
        }
        if (this.h3) {
            return compare3(set1, set2);
        }
        return compare1(set1, set2);
    }
    
    /**
     * Simple compare using type
     * 
     * @param set1
     * @param set2
     * @return
     */
    private int compare1(RichAtomSet set1, RichAtomSet set2) {
        RichAtomSet.Type typeA = set1.getType();
        RichAtomSet.Type typeB = set2.getType();
        if (typeA == RichAtomSet.Type.ALIPHATIC
                && (typeB == RichAtomSet.Type.FUSED ||
                typeB == RichAtomSet.Type.ISOLATED || 
                typeB == RichAtomSet.Type.SMALLEST)) {
            return 1;
        }
        if (typeB == RichAtomSet.Type.ALIPHATIC
                && (typeA == RichAtomSet.Type.FUSED ||
                typeA == RichAtomSet.Type.ISOLATED ||
                typeA == RichAtomSet.Type.SMALLEST)) {
            return -1;
        }
        return -1 * Integer.compare(set1.getStructure().getAtomCount(), set2.getStructure().getAtomCount());
    }

    

    /**
     * Compares using only weight for RichAtomSets
     * 
     * @param set1
     * @param set2
     * @return
     */
    private int compare2(RichAtomSet set1, RichAtomSet set2) {

        IAtomContainer container1 = set1.getStructure();
        IAtomContainer container2 = set2.getStructure();

        double weightA = AtomContainerManipulator.getNaturalExactMass(container1);
        double weightB = AtomContainerManipulator.getNaturalExactMass(container2);

        if (weightA > weightB) {
            return 1;
        }
        if (weightB > weightA) {
            return -1;
        }
        
        return 0;
    }

    /**
     * Compares by comparing type and then weight
     * 
     * @param set1
     * @param set2
     * @return
     */
    private int compare3(RichAtomSet set1, RichAtomSet set2) {

        RichAtomSet.Type typeA = set1.getType();
        RichAtomSet.Type typeB = set2.getType();

        IAtomContainer container1 = set1.getStructure();
        IAtomContainer container2 = set2.getStructure();

        double weightA = AtomContainerManipulator.getNaturalExactMass(container1);
        double weightB = AtomContainerManipulator.getNaturalExactMass(container2);

        if (typeA == RichAtomSet.Type.ALIPHATIC
                && (typeB == RichAtomSet.Type.FUSED ||
                typeB == RichAtomSet.Type.ISOLATED ||
                typeB == RichAtomSet.Type.SMALLEST)) {
            return -1;
        }

        if (typeA == typeB) {
            if (weightA > weightB) {
                return 1;
            }
            if (weightB > weightA) {
                return -1;
            }
        }

        return -1 * Integer.compare(set1.getStructure().getAtomCount(), set2.getStructure().getAtomCount());
    }

    

}
