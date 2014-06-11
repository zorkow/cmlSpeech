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
package io.github.egonw;

import org.openscience.cdk.interfaces.IAtom;

/**
 *
 */

public class RichAtom extends RichChemObject {

    RichAtom(IAtom structure) {
        super(structure);
    };

}
