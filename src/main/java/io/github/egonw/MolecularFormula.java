/**
 * @file   MolecularFormula.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Fri Jan 30 01:41:35 2015
 * 
 * @brief  Utility class for molecular formula computation.
 * 
 * 
 */

package io.github.egonw;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import java.util.List;

/**
 *
 */

public class MolecularFormula {

    public static String compute(RichAtomSet system) {
        IMolecularFormula form = MolecularFormulaManipulator.
            getMolecularFormula(system.getStructure());
        return MolecularFormulaManipulator.getString(form);
    }


    public static void set(RichAtomSet system) {
        system.molecularFormula = MolecularFormula.compute(system);
    }
    
    public static void set(List<RichAtomSet> systems) {
        systems.stream().forEach(MolecularFormula::set);
    }
    
}
