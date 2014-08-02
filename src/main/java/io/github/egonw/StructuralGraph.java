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

import java.util.List;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.openscience.cdk.interfaces.IAtom;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 *
 */

public class StructuralGraph extends SimpleGraph {
    
    StructuralGraph() {
        super(StructuralEdge.class);
    }


    StructuralGraph(List<RichAtomSet> atomSets, List<RichAtom> singletonAtoms) {
        super(StructuralEdge.class);
        List<RichStructure> combined = new ArrayList<RichStructure>(atomSets);
        combined.addAll(singletonAtoms);
        this.init(combined);
    }


    StructuralGraph(List<RichStructure> structures) {
        super(StructuralEdge.class);
        this.init(structures);
    }

    
    private void init(List<RichStructure> structures) {
        List<String> names = structures.stream()
            .map(RichStructure::getId).collect(Collectors.toList());
        names.stream().forEach(this::addVertex);

        for (RichStructure structure : structures) {
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

    public void visualize (List<RichAtomSet> majorSystems, List<RichAtom> singletonAtoms) {
        StructuralGraphVisualizer vis = new StructuralGraphVisualizer();
        vis.init(this, majorSystems, singletonAtoms);
    }


}


