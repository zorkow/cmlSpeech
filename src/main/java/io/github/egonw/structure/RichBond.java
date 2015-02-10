/**
 * @file   RichBond.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 15:14:55 2014
 * 
 * @brief  Annotated Bond structure.
 * 
 * 
 */


//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 *
 */

public class RichBond extends RichChemObject {

    public RichBond(IBond structure) {
        super(structure);

        for (IAtom atom : structure.atoms()) {
            this.getComponents().add(atom.getID());
        }

    };

    @Override
    public IBond getStructure() {
        return (IBond)this.structure;
    }

}
