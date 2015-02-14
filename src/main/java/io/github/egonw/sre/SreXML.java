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
 * @file   SreXML.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Thu Jun 19 16:34:40 2014
 * 
 * @brief  Abstract class to handle SRE annotations.
 * 
 * 
 */

//
package io.github.egonw.sre;

import io.github.egonw.analysis.StructuralAnalysis;
import io.github.egonw.structure.ComponentsPositions;

import java.util.Set;

/**
 * Abstract class for XML like Sre annotations.
 */

public abstract class SreXML {

    SreAnnotations annotations;
    StructuralAnalysis analysis;
    
    SreXML(StructuralAnalysis analysis) {
        this.analysis = analysis;
        this.annotations = new SreAnnotations(); 
    }

    public SreAnnotations getAnnotations() {
        this.finalize();
        return this.annotations;
    }

    abstract void compute();

    public void finalize() {
        this.annotations.finalize();
    };

    public void toSreSet(String annotate, SreNamespace.Tag tag, Set<String> set) {
        for (String element : set) {
            this.annotations.appendAnnotation(annotate, tag, this.toSreElement(element));
        }
    }

    public void toSreSet(String annotate, SreNamespace.Tag tag, ComponentsPositions positions) {
        for (String element : positions) {
            this.annotations.appendAnnotation(annotate, tag, this.toSreElement(element));
        }
    }

    public SreElement toSreElement(String name) {
        if (this.analysis.isAtom(name)) {
            return new SreElement(SreNamespace.Tag.ATOM, name);
        }
        if (this.analysis.isBond(name)) {
            return new SreElement(SreNamespace.Tag.BOND, name);
        }
        if (this.analysis.isAtomSet(name)) {
            return new SreElement(SreNamespace.Tag.ATOMSET, name);
        }
        return new SreElement(SreNamespace.Tag.UNKNOWN, name);
    }

}

