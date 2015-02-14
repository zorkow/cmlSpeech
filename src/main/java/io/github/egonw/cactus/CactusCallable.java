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

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.concurrent.Callable;

/**
 * Callables for Cactus Futures.
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
