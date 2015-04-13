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
 * @file   StructuralGraphVisualizer.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:36:33 2015
 *
 * @brief  A basic visualiser for molecule graphs.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.graph;

import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichStructure;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.interfaces.IAtom;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.vecmath.Point2d;

/**
 * Basic visualiser for structural graphs.
 */
public class StructuralGraphVisualizer {
  private static final Color DEFAULT_BG_COLOR = Color.decode("#B6D1C2");
  // Some colours for binary graphs.
  private static final Color WHITE = Color.decode("#FFFFFF");
  private static final Color BLACK = Color.decode("#000000");
  private static final Color GRAY = Color.decode("#C2CBCC");

  // TODO (sorge) Refactor into visualiser class that sets all frames at the
  // right point.
  private final double scale = 150;
  private final double offset = 10;
  private final double padding = 120;
  private double minX = Double.POSITIVE_INFINITY;
  private double minY = Double.POSITIVE_INFINITY;
  private double maxX = Double.NEGATIVE_INFINITY;
  private double maxY = Double.NEGATIVE_INFINITY;
  private boolean colour = true;

  /** A class of points with names. */
  class NamedPoint extends Point2d {

    private static final long serialVersionUID = 1L;

    private final String name;

    /**
     * Constructor for named points.
     *
     * @param name
     *          The name of point.
     * @param pointX
     *          X coordinate.
     * @param pointY
     *          Y coordinate.
     */
    NamedPoint(final String name, final double pointX, final double pointY) {
      super(pointX, pointY);
      this.name = name;
    }

    /**
     * The name of the point.
     *
     * @return The name.
     */
    public String getName() {
      return this.name;
    }
  }

  //
  private JGraphModelAdapter<?, ?> mjgAdapter;

  /**
   * Initialises the graph visualiser.
   *
   * @param sg
   *          The graph.
   * @param structures
   *          A list of rich chemical objects.
   * @param name
   *          The name of the graph to display as frame title.
   * @see java.applet.Applet#init().
   */
  public void init(final SimpleGraph<?, ?> sg,
      final List<RichStructure<?>> structures,
      final String name) {
    this.colour = !Cli.hasOption("vis_bw");
    final ListenableGraph<?, ?> graph = new ListenableUndirectedGraph<>(sg);
    this.mjgAdapter = new JGraphModelAdapter<>(graph);

    final JGraph jgraph = new JGraph(this.mjgAdapter);

    if (this.colour) {
      jgraph.setBackground(DEFAULT_BG_COLOR);
    } else {
      jgraph.setBackground(WHITE);
      jgraph.setForeground(BLACK);
    }

    final List<NamedPoint> points = new ArrayList<NamedPoint>();
    for (final RichStructure<?> structure : structures) {
      if (structure instanceof RichAtomSet) {
        points.add(this.computeCentroid((RichAtomSet) structure));
      } else {
        points.add(this.computeAtom((RichAtom) structure));
      }
    }
    this.positionPoints(points);

    final JScrollPane scroller = new JScrollPane(jgraph);
    final JFrame frame = new JFrame(name);

    frame.setBounds((int) this.minX, (int) this.minY,
        (int) (this.maxX - this.minX + this.padding),
        (int) (this.maxY - this.minY + this.padding));
    frame.add(scroller);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jgraph.getGraphLayoutCache().reload();
    jgraph.repaint();
  }

  /**
   * Computes the correct positions for points in the display frame.
   *
   * @param points
   *          A list of named points.
   */
  private void positionPoints(final List<NamedPoint> points) {
    points.stream().forEach(
        p -> this.positionVertexAt(p.getName(), p.getX() - this.minX
            + this.offset,
            p.getY() - this.minY + this.offset));
  }

  /**
   * Compute the centroid for a rich atom set.
   *
   * @param set
   *          The rich atom set.
   *
   * @return The named point corresponding to the centroid, named with the set
   *         id.
   */
  private NamedPoint computeCentroid(final RichAtomSet set) {
    double pointX = 0;
    double pointY = 0;
    int steps = 0;
    for (final IAtom atom : set.getStructure().atoms()) {
      final Point2d x2d = atom.getPoint2d();
      pointX += (x2d.getX() * this.scale);
      pointY += (x2d.getY() * this.scale);
      steps++;
    }
    final NamedPoint point = new NamedPoint(set.getId(), pointX / steps,
        -1 * pointY / steps);
    this.minX = Math.min(this.minX, point.getX());
    this.minY = Math.min(this.minY, point.getY());
    this.maxX = Math.max(this.maxX, point.getX());
    this.maxY = Math.max(this.maxY, point.getY());
    return point;
  }

  /**
   * Get a named point for a rich atom at the right position in the frame.
   *
   * @param richAtom
   *          The rich atom.
   *
   * @return The named point.
   */
  private NamedPoint computeAtom(final RichAtom richAtom) {
    final IAtom atom = richAtom.getStructure();
    final Point2d x2d = atom.getPoint2d();
    final NamedPoint point = new NamedPoint(atom.getID(),
        (x2d.getX() * this.scale),
        (-1 * x2d.getY() * this.scale));
    this.minX = Math.min(this.minX, point.getX());
    this.minY = Math.min(this.minY, point.getY());
    this.maxX = Math.max(this.maxX, point.getX());
    this.maxY = Math.max(this.maxY, point.getY());
    return point;
  }

  /**
   * Poisitions a vertex at a given point in the graph.
   *
   * @param vertex
   *          The vertex.
   * @param pointX
   *          X coordinate.
   * @param pointY
   *          Y coordinate.
   */
  private void positionVertexAt(final String vertex, final double pointX,
      final double pointY) {
    final DefaultGraphCell cell = this.mjgAdapter.getVertexCell(vertex);
    final AttributeMap attr = cell.getAttributes();
    final Rectangle2D bounds = GraphConstants.getBounds(attr);

    if (!this.colour) {
      attr.applyValue("foregroundColor", BLACK);
      attr.applyValue("backgroundColor", GRAY);
    }

    GraphConstants.setBounds(attr, new Rectangle((int) pointX, (int) pointY,
        (int) bounds.getWidth(), (int) bounds.getHeight()));
    final Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<>();
    cellAttr.put(cell, attr);
    this.mjgAdapter.edit(cellAttr, null, null, null);
  }
}
