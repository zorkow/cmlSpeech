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
 * @file   RichAliphaticChain.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich aliphatic chain.
 * 
 * 
 */

//
package io.github.egonw.structure;

import io.github.egonw.sre.SreException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;

/**
 * Atom sets that are rich aliphatic chains.
 */

public class RichAliphaticChain extends RichAtomSet {

        
    public RichAliphaticChain(IAtomContainer container, String id) {
        super(container, id, RichSetType.ALIPHATIC);
    }


    @Override
    public RichSetType getType() {
        return this.type;
    }
    

    protected final void walk() {
        // TODO (sorge) Choose start wrt. replacements, preferring O, then by
        // weight. Then wrt. position OH substitution then other substitutions
        // by weight. For direction: choose second by smallest distance to
        // first!
        IAtom startAtom = this.getSinglyConnectedAtom();
        if (startAtom == null) {
            throw new SreException("Aliphatic chain without start atom!");
        }
        this.walkStraight(startAtom);
    }
    
}
