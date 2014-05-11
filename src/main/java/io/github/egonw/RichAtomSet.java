
//
package io.github.egonw;

import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.AtomContainer;

/**
 *
 */

public class RichAtomSet extends CMLAtomSet {
    
    public IAtomContainer container;

    public RichAtomSet (IAtomContainer container) {
        super();
        this.container = container;
    }
}
