/**
 * @file   RichAtom.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 15:14:55 2014
 * 
 * @brief  Annotated Atom structure.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtom;

/**
 *
 */

public class RichAtom extends RichChemObject {

    public RichAtom(IAtom structure) {
        super(structure);
    };

    @Override
    public IAtom getStructure() {
        return (IAtom)this.structure;
    }

    public Boolean isCarbon() {
        return this.getStructure().getSymbol().equals("C");
    }
    
}
