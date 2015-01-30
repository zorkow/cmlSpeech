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
package io.github.egonw;

import java.util.Set;

/**
 *
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
        System.out.println(positions);
        for (String element : positions) {
            System.out.println(element);
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

