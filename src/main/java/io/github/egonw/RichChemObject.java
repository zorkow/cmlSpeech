/**
 * @file   RichChemObject.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Jun 11 15:14:55 2014
 * 
 * @brief  Annotated ChemObject structure.
 * 
 * 
 */

//
package io.github.egonw;

import org.openscience.cdk.interfaces.IChemObject;

/**
 *
 */

public class RichChemObject extends AbstractRichStructure<IChemObject> implements RichStructure<IChemObject> {
    

    RichChemObject(IChemObject structure) {
        super(structure);
    };


    @Override
    public String getId() {
        return this.structure.getID();
    }
    
}
