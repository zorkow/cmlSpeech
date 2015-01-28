
//
package io.github.egonw;

import org.jgrapht.graph.DefaultEdge;

/**
 *
 */

public class StructuralEdge extends DefaultEdge {
    
    private static final long serialVersionUID = 1L;

    private String label;

    public StructuralEdge(String label) {
        super();
        this.label = label;
    }

    public String toString() {
        // return "(" + this.getSource().toString() + " : " + 
        //     this.label + " : " + this.getTarget().toString() + ")";
        return this.label;
    }
}
