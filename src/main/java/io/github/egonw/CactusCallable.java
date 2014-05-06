/**
 * @file   Cactus.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 13:22:37 2014
 * 
 * @brief  Callable class for Cactus Futures.
 * 
 * 
 */


//
package io.github.egonw;

import java.util.concurrent.Callable;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 */

public class CactusCallable implements Callable<SreAttribute> {

    public String id = "";
    private String output = "";
    private IAtomContainer container = null;

    public CactusCallable(String id, String output, IAtomContainer container) {
        super();
        this.id = id;
        this.output = output;
        this.container = container;
    }

    @Override
        public SreAttribute call() throws CactusException {
        String result = "";
        switch (this.output) {
        case "iupac": 
            result = Cactus.getIUPAC(container);
            break;
        case "name":
            result = Cactus.getName(container);
            break;
        }
        return new SreAttribute(this.output, result);
    }

   
}
