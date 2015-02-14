// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/**
 * @file   MolecularFormula.java
 * @author Volker Sorge <sorge@zorkstomp>
 * @date   Fri Jan 30 01:41:35 2015
 * 
 * @brief  Utility class for molecular formula computation.
 * 
 * 
 */

//
package io.github.egonw.analysis;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import io.github.egonw.structure.RichAtomSet;

import java.util.List;

/**
 * Utility class for molecular formula computation.
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
