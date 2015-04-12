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

package io.github.egonw.graph;

import io.github.egonw.base.Cli;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichStructure;

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

  // TODO (sorge) Test if this is better done with double and infinity.
  //
  // TODO (sorge) Refactor into visualiser class that sets all frames at the
  // right point.
  private final int scale = 150;
  private final int offset = 10;
  private final int padding = 120;
  private int minX = Integer.MAX_VALUE;
  private int minY = Integer.MAX_VALUE;
  private int maxX = Integer.MIN_VALUE;
  private int maxY = Integer.MIN_VALUE;

  private boolean colour = true;

  class NamedPoint {
    private int pointX;
    private int pointY;
    private String name;

    NamedPoint(String name, int pointX, int pointY) {
      this.pointX = pointX;
      this.pointY = pointY;
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public int getX() {
      return this.pointX;
    }

    public int getY() {
      return this.pointY;
    }
  }

  //
  private JGraphModelAdapter<?, ?> mjgAdapter;

  /**
   * @see java.applet.Applet#init().
   */
  public void init(SimpleGraph<?, ?> sg, List<RichStructure<?>> structures,
      String name) {
    this.colour = !Cli.hasOption("vis_bw");
    ListenableGraph<?, ?> graph = new ListenableUndirectedGraph<>(sg);
    mjgAdapter = new JGraphModelAdapter<>(graph);

    JGraph jgraph = new JGraph(mjgAdapter);

    if (this.colour) {
      jgraph.setBackground(DEFAULT_BG_COLOR);
    } else {
      jgraph.setBackground(WHITE);
      jgraph.setForeground(BLACK);
    }

    List<NamedPoint> points = new ArrayList<NamedPoint>();
    for (RichStructure<?> structure : structures) {
      if (structure instanceof RichAtomSet) {
        points.add(computeCentroid((RichAtomSet) structure));
      } else {
        points.add(computeAtom((RichAtom) structure));
      }
    }
    positionPoints(points);

    JScrollPane scroller = new JScrollPane(jgraph);
    JFrame frame = new JFrame(name);

    frame.setBounds(this.minX, this.minY, this.maxX - this.minX + this.padding,
        this.maxY - this.minY + this.padding);
    frame.add(scroller);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    jgraph.getGraphLayoutCache().reload();
    jgraph.repaint();
  }

  private void positionPoints(List<NamedPoint> points) {
    points.stream().forEach(
        p -> positionVertexAt(p.getName(), p.getX() - this.minX + this.offset,
            p.getY() - this.minY + this.offset));
  }

  private NamedPoint computeCentroid(RichAtomSet set) {
    double pointX = 0;
    double pointY = 0;
    int steps = 0;
    for (IAtom atom : set.getStructure().atoms()) {
      Point2d x2d = atom.getPoint2d();
      pointX += (x2d.x * scale);
      pointY += (x2d.y * scale);
      steps++;
    }
    NamedPoint point = new NamedPoint(set.getId(), (int) pointX / steps,
        -1 * (int) pointY / steps);
    this.minX = Math.min(this.minX, point.getX());
    this.minY = Math.min(this.minY, point.getY());
    this.maxX = Math.max(this.maxX, point.getX());
    this.maxY = Math.max(this.maxY, point.getY());
    return point;
  }

  private NamedPoint computeAtom(RichAtom richAtom) {
    IAtom atom = richAtom.getStructure();
    Point2d x2d = atom.getPoint2d();
    NamedPoint point = new NamedPoint(atom.getID(), (int) (x2d.x * scale),
        (int) (-1 * x2d.y * scale));
    this.minX = Math.min(this.minX, point.getX());
    this.minY = Math.min(this.minY, point.getY());
    this.maxX = Math.max(this.maxX, point.getX());
    this.maxY = Math.max(this.maxY, point.getY());
    return point;
  }

  private void positionVertexAt(String vertex, int pointX, int pointY) {
    DefaultGraphCell cell = mjgAdapter.getVertexCell(vertex);
    AttributeMap attr = cell.getAttributes();
    Rectangle2D bounds = GraphConstants.getBounds(attr);

    if (!this.colour) {
      attr.applyValue("foregroundColor", BLACK);
      attr.applyValue("backgroundColor", GRAY);
    }

    GraphConstants.setBounds(attr, new Rectangle(pointX, pointY,
        (int) bounds.getWidth(),
        (int) bounds.getHeight()));
    Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<>();
    cellAttr.put(cell, attr);
    mjgAdapter.edit(cellAttr, null, null, null);
  }
}
