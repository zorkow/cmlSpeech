/**
 * @file   StructuralEdge.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:34:38 2015
 * 
 * @brief  Edges for the structural graph.
 * 
 * 
 */

//
package io.github.egonw.graph;

import io.github.egonw.base.Cli;

import org.jgrapht.graph.DefaultEdge;

/**
 * Edges for the structural graph.
 * @extends DefaultEdge
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
