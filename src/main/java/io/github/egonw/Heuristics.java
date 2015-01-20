/**
 * @file   Heuristics.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Jan 17 18:24:15 2015
 * 
 * @brief  Combines basic comparators via a heuristic.
 * 
 * 
 */
package io.github.egonw;

import java.util.Comparator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class Heuristics extends DefaultComparator<RichChemObject> {

    private String[] heuristics;
    private Comparator<RichChemObject> weight = new WeightComparator();
    private Comparator<RichChemObject> type = new TypeComparator();
    private Comparator<RichChemObject> size = new SizeComparator();

    public Heuristics(String heuristic) {
        this.heuristics = heuristic == "" ?
            new String[] {"type", "weight", "size"} : heuristic.split(",");
    }
    
    public int compare(RichAtomSet set1, RichAtomSet set2) {
        Integer result = 0;
        for (String heuristic : this.heuristics) {
            switch (heuristic) {
            case "size":
                result = this.size.compare(set1, set2);
                break;
            case "type":
                result = this.type.compare(set1, set2);
                break;
            case "weight":
                result = this.weight.compare(set1, set2);
                break;
            default:
                break;
            }
            if (result != 0) {
                break;
            }
        }
        return result;
    }
}
