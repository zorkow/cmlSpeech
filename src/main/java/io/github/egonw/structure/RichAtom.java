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

import io.github.egonw.sre.SreNamespace;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Atoms with admin information.
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
    
    @Override
    public SreNamespace.Tag tag() {
        return SreNamespace.Tag.ATOM;
    }

}
