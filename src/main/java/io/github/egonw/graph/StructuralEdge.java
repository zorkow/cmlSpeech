
//
package io.github.egonw.graph;

import io.github.egonw.base.Cli;

import org.jgrapht.graph.DefaultEdge;

/**
 *
 */

public class StructuralEdge extends DefaultEdge {
    
    private static final long serialVersionUID = 1L;

    private String label;
    private boolean shortBonds = false;

    public StructuralEdge(String label) {
        super();
        this.label = label;
        this.shortBonds = Cli.hasOption("vis_short");
    }

    public String toString() {
        if (this.shortBonds) {
            return this.label;
        }
        return "(" + this.getSource().toString() + " : " + 
            this.label + " : " + this.getTarget().toString() + ")";
    }
}
