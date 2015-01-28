
//
package io.github.egonw;

/**
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.vecmath.Point2d;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Basic visualiser for structural graphs.
 */
public class StructuralGraphVisualizer {
    private static final Color DEFAULT_BG_COLOR = Color.decode("#B6D1C2");
    // Some colours for binary graphs.
    private static final Color WHITE = Color.decode("#FFFFFF");
    private static final Color BLACK = Color.decode("#000000");
    private static final Color GRAY = Color.decode("#C2CBCC");

    private static final Dimension DEFAULT_SIZE = new Dimension(500, 500);

    // TODO (sorge) Test if this is better done with double and infinity.
    private final int scale = 150;
    private final int offset = 10;
    private final int padding = 120;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    
    private boolean colour = true;

    class NamedPoint {
        private int x;
        private int y;
        private String name;
        
        NamedPoint(String name, int x, int y) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }
    
    // 
    private JGraphModelAdapter<?, ?> m_jgAdapter;
    
    /**
     * @see java.applet.Applet#init().
     */
    public void init(SimpleGraph<?, ?> sg, List<RichStructure<?>> structures, String name) {
        this.colour = !Cli.hasOption("vis_bw");
        ListenableGraph<?, ?> g = new ListenableUndirectedGraph<>(sg);
        m_jgAdapter = new JGraphModelAdapter<>(g);

        JGraph jgraph = new JGraph(m_jgAdapter);

        if (this.colour) {
            jgraph.setBackground(DEFAULT_BG_COLOR);
        } else {
            jgraph.setBackground(WHITE);
            jgraph.setForeground(BLACK);
        }
        
        JScrollPane scroller = new JScrollPane(jgraph);
        JFrame frame = new JFrame(name);

        List<NamedPoint> points = new ArrayList<NamedPoint>();
        for (RichStructure<?> structure : structures) {
            if (structure instanceof RichAtomSet) {
                points.add(computeCentroid((RichAtomSet)structure));
            } else {
                points.add(computeAtom((RichAtom)structure));
            }
        }
        positionPoints(points);

        frame.setBounds(this.minX, this.minY,
                        this.maxX - this.minX + this.padding,
                        this.maxY - this.minY + this.padding);
        frame.add(scroller);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jgraph.getGraphLayoutCache().reload();
        jgraph.repaint();
    }


    private void positionPoints(List<NamedPoint> points) {
        points.stream()
            .forEach(p -> positionVertexAt(p.getName(),
                                           p.getX() - this.minX + this.offset,
                                           p.getY() - this.minY + this.offset));
    }


    private NamedPoint computeCentroid(RichAtomSet set) {
        double x = 0;
        double y = 0;
        int n = 0;
        for (IAtom atom : set.getStructure().atoms()) {
            Point2d x2d = atom.getPoint2d();
            x += (x2d.x * scale);
            y += (x2d.y * scale);
            n++;
        }
        NamedPoint point = new NamedPoint(set.getId(), (int)x / n, -1 * (int)y / n);
        this.minX = Math.min(this.minX, point.getX());
        this.minY = Math.min(this.minY, point.getY());
        this.maxX = Math.max(this.maxX, point.getX());
        this.maxY = Math.max(this.maxY, point.getY());
        return point;
    }
    

    private NamedPoint computeAtom(RichAtom richAtom) {
        IAtom atom = richAtom.getStructure();
        Point2d x2d = atom.getPoint2d();
        NamedPoint point = new NamedPoint(atom.getID(),
                                          (int)(x2d.x * scale),
                                          (int)(-1 * x2d.y * scale));
        this.minX = Math.min(this.minX, point.getX());
        this.minY = Math.min(this.minY, point.getY());
        this.maxX = Math.max(this.maxX, point.getX());
        this.maxY = Math.max(this.maxY, point.getY());
        return point;
    }
    

    private void positionVertexAt(String vertex, int x, int y) {
        DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D b = GraphConstants.getBounds(attr);

        if (!this.colour) {
            attr.applyValue("foregroundColor", BLACK);
            attr.applyValue("backgroundColor", GRAY);
        }
        
        GraphConstants.setBounds(attr, new Rectangle(x, y,
                                                     (int)b.getWidth(),
                                                     (int)b.getHeight()));
        Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<>();
        cellAttr.put(cell, attr);
        m_jgAdapter.edit(cellAttr, null, null, null);
    }
}
