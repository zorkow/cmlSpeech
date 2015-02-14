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
 * @file   SreElement.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:31:09 2015
 * 
 * @brief  Sre Elements
 * 
 * 
 */

//
package io.github.egonw.sre;

import nu.xom.Element;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Basic elements for Sre annotations.
 * @extends Element
 */

public class SreElement extends Element {

    SreElement(String tag) {
        super(SreNamespace.getInstance().prefix + ":" + tag,
              SreNamespace.getInstance().uri);
    }

    SreElement(SreNamespace.Tag tag) {
        super(tag.tag, SreNamespace.getInstance().uri);
    }

    SreElement(SreNamespace.Tag tag, String text) {
        super(tag.tag, SreNamespace.getInstance().uri);
        this.appendChild(text);
    }

    SreElement(IAtom obj) {
        this(SreNamespace.Tag.ATOM);
        this.appendChild(obj.getID());
    }

    SreElement(IBond obj) {
        this(SreNamespace.Tag.BOND);
        this.appendChild(obj.getID());
    }

    SreElement(IAtomContainer obj) {
        this(SreNamespace.Tag.ATOMSET);
        this.appendChild(obj.getID());
    }

    SreElement(SreNamespace.Tag tag, Element child1, Element child2) {
        super(tag.tag, SreNamespace.getInstance().uri);
        this.appendChild(child1);
        this.appendChild(child2);
    }

}
