/**
 * @file   CMLNameComparator.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 12:19:41 2014
 * 
 * @brief  A comparator for CML naming conventions.
 * 
 * 
 */

//
package io.github.egonw;

import java.util.Comparator;

/**
 *
 */

public class CMLNameComparator implements Comparator<String> {
    
    public int compare(String name1, String name2) {
        String reg1 = "[0-9]*";
        String alpha1 = name1.replaceAll(reg1, "");
        String alpha2 = name2.replaceAll(reg1, "");
        if (alpha1.equals(alpha2)) {
            String reg2 = "[a-z]*";
            Integer numer1 = Integer.parseInt(name1.replaceAll(reg2, ""));
            Integer numer2 = Integer.parseInt(name2.replaceAll(reg2, ""));
            if (numer1 == numer2) {
                return 0;
            }
            if (numer1 < numer2) {
                return -1;
            }
            return 1;
        }
        if (alpha1.equals("as") && alpha2.equals("b")) {
            return 1;
        }
        if (alpha1.equals("b") && alpha2.equals("as")) {
            return -1;
        }
        return alpha1.compareTo(alpha2);
    }

}
