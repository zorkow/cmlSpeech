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
package io.github.egonw.graph;


import org.jgrapht.graph.SimpleGraph;

import io.github.egonw.connection.Connection;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichStructure;

import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.openscience.cdk.interfaces.IAtom;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * The structural graph.
 * @extends SimpleGraph
 */

public class StructuralGraph extends SimpleGraph<String, StructuralEdge> {
    
    private static final long serialVersionUID = 1L;

    private List<RichStructure<?>> structures;

    StructuralGraph() {
        super(StructuralEdge.class);
        this.structures = new ArrayList<RichStructure<?>>();
    }


    public StructuralGraph(List<RichAtomSet> atomSets, List<RichAtom> singletonAtoms) {
        super(StructuralEdge.class);
        this.structures = new ArrayList<RichStructure<?>>(atomSets);
        this.structures.addAll(singletonAtoms); 
       this.init();
    }


    public StructuralGraph(List<RichStructure<?>> structures) {
        super(StructuralEdge.class);
        this.structures = structures;
        this.init();
    }

    
    private void init() {
        List<String> names = this.structures.stream()
            .map(RichStructure::getId).collect(Collectors.toList());
        names.stream().forEach(this::addVertex);

        for (RichStructure<?> structure : this.structures) {
            Set<Connection> connections = structure.getConnections();
            if (!connections.isEmpty()) {
                this.addSingleEdges(structure.getId(), connections, names);
            }
        }
    }

   
    private void addSingleEdges(String source, Set<Connection> connections, List<String> systems) {
        for (Connection connection : connections) {
            if (systems.contains(connection.getConnected())) {
                this.addEdge(source, connection.getConnected(),
                                       connection.getConnector());
            }
        }
    }


    public StructuralEdge addEdge(String source, String target, String label) {
        StructuralEdge edge = new StructuralEdge(label);
        this.addEdge(source, target, edge);
        return edge;
    }

    public void visualize (String name) {
        StructuralGraphVisualizer vis = new StructuralGraphVisualizer();
        vis.init(this, this.structures, name);
    }


}


