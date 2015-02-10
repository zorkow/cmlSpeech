/**
 * @file   Cactus.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 13:22:37 2014
 * 
 * @brief  Callable class for Cactus Futures.
 * 
 */


//
package io.github.egonw.cactus;

import io.github.egonw.sre.SreAttribute;

import java.util.concurrent.Callable;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 */
public class CactusCallable implements Callable<SreAttribute> {

    public String id = "";
    private Cactus.Type type;
    private IAtomContainer container = null;

    public CactusCallable(String id, Cactus.Type type, IAtomContainer container) {
        super();
        this.id = id;
        this.type = type;
        this.container = container;
    }

    @Override
        public SreAttribute call() throws CactusException {
        String result = this.type.caller.apply(this.container);
        return new SreAttribute(this.type.tag, result);
    }

   
}
