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
 * @file   SreAttribute.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:29:44 2015
 * 
 * @brief  Attributes for speech annotations.
 * 
 * 
 */

//
package io.github.egonw.sre;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Attribute structure.
 * @extends Attribute
 */

public class SreAttribute extends Attribute {
 
    public SreAttribute(String localName, String value) {
        super(SreNamespace.getInstance().prefix + ":" + localName,
              SreNamespace.getInstance().uri, value);
    }

    public SreAttribute(SreNamespace.Attribute attr, String value) {
        super(SreNamespace.getInstance().prefix + ":" + attr.attribute,
              SreNamespace.getInstance().uri, value);
    }

    public void addValue(String value) {
        if (getValue() == "") {
            setValue(value);
        } else {
            setValue(getValue() + " " + value);
        }
    }

    public void addValue(Element node) {
        String localName = getLocalName();
        String namespace = getNamespaceURI();
        SreAttribute oldAttr = (SreAttribute)node.getAttribute(localName, namespace);
        if (oldAttr == null) {
            node.addAttribute(this);
        } else {
            oldAttr.setValue(oldAttr.getValue() + " " + getValue());
        }
    }

}
