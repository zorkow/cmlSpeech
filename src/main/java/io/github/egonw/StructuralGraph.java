/**
 * @file   StructuralGraph.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed May 28 22:56:58 2014
 * 
 * @brief A lightweight graph to represent the top level structure of molecules.
 * !!! This uses Jgrapht v0.9 !!!
 * 
 */

//
package io.github.egonw;


import org.jgrapht.graph.SimpleGraph;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
// import org.jgrapht.ListenableGraph;
// import org.jgrapht.graph.ListenableUndirectedGraph;

/**
 *
 */

public class StructuralGraph extends SimpleGraph {
    
    StructuralGraph() {
        super(StructuralEdge.class);
    }
    
    public StructuralEdge addEdge(String source, String target, SreElement label) {
        StructuralEdge edge = new StructuralEdge(label);
        this.addEdge(source, target, edge);
        return edge;
    }

    public void visualize () {
        //fList<RichAtomSet> majorSystems, Set<IAtom> singletonAtoms) {
        //        ListenableGraph g = new ListenableUndirectedGraph(this);
        StructuralGraphVisualizer vis = new StructuralGraphVisualizer();
        vis.init(this);
    }


}


