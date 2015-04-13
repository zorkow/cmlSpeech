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
 * @file   StructuralGraph.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed May 28 22:56:58 2014
 *
 * @brief A lightweight graph to represent the top level structure of molecules.
 *        !!! This uses Jgrapht v0.9 !!!
 *
 */

//

package com.progressiveaccess.cmlspeech.graph;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.connection.Connection;
import com.progressiveaccess.cmlspeech.structure.RichStructure;

import org.jgrapht.graph.SimpleGraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The structural graph.
 */

public class StructuralGraph extends SimpleGraph<String, StructuralEdge> {

  private static final long serialVersionUID = 1L;

  private final List<RichStructure<?>> structures;
  private final Set<String> names;


  /**
   * Constructs a new structural graph for rich chemical objects.
   *
   * @param structures
   *            A set of names for structures.
   */
  public StructuralGraph(final Set<String> structures) {
    super(StructuralEdge.class);
    this.names = structures;
    this.structures = this.names.stream()
        .map(RichStructureHelper::getRichStructure)
        .collect(Collectors.toList());
    this.init();
  }


  /**
   * Initialised the graph.
   */
  private void init() {
    this.names.stream().forEach(this::addVertex);
    for (final RichStructure<?> structure : this.structures) {
      final Set<Connection> connections = structure.getConnections();
      if (!connections.isEmpty()) {
        this.addSingleEdges(structure.getId(), connections, this.names);
      }
    }
  }


  /**
   * Adds a number of edges for a source structure within a given
   * context. Connections are only added if the goal of the connection also
   * lives in that context, e.g., is part of the same atom set.
   *
   * @param source Name of the source structure.
   * @param connections Set of connections for the source structures.
   * @param systems The system providing the context.
   */
  private void addSingleEdges(final String source,
      final Set<Connection> connections,
      final Set<String> systems) {
    for (final Connection connection : connections) {
      if (systems.contains(connection.getConnected())) {
        this.addEdge(source, connection.getConnected(),
            connection.getConnector());
      }
    }
  }


  /**
   * Adds a single structural edge to the graph.
   *
   * @param source Name of edge source.
   * @param target Name of edge target.
   * @param label Name of edge label.
   *
   * @return The newly created structural edge.
   */
  public StructuralEdge addEdge(final String source, final String target,
      final String label) {
    final StructuralEdge edge = new StructuralEdge(label);
    this.addEdge(source, target, edge);
    return edge;
  }


  /**
   * Visualise the graph in a frame.
   *
   * @param name Title of frame.
   */
  public void visualize(final String name) {
    final StructuralGraphVisualizer vis = new StructuralGraphVisualizer();
    vis.init(this, this.structures, name);
  }

}
