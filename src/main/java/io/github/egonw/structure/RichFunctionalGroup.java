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
 * @file   RichFunctionalGroup.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich functional groups.
 * 
 * 
 */

//
package io.github.egonw.structure;

import io.github.egonw.sre.SreException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.List;

/**
 * Atom sets that are rich functional groups.
 */

public class RichFunctionalGroup extends RichAtomSet {

    
    public RichFunctionalGroup(IAtomContainer container, String id) {
        super(container, id, RichSetType.FUNCGROUP);
    }

    
    protected final void walk() {
        List<IAtom> atoms = this.getExternallyConnectedAtoms();
        if (atoms.size() == 0) {
            atoms = this.getSinglyConnectedAtoms();
        }
        if (atoms.size() == 0) {
            throw new SreException("Functional group without start atom!");
        }
        this.walkDepthFirst(atoms.get(0));
    }

}
