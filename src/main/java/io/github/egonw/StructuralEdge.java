
//
package io.github.egonw;

import org.jgrapht.graph.DefaultEdge;

/**
 *
 */

public class StructuralEdge<V> extends DefaultEdge {
    
    private String label;

    public StructuralEdge(String label) {
        super();
        this.label = label;
    }

    // private String label;

    // public StructuralEdge(String label) {
    //     super();
    //     this.label = label;
    // }

    public String toString() {
        return "(" + this.getSource().toString() + " : " + 
            this.label + " : " + this.getTarget().toString() + ")";
    }
}
