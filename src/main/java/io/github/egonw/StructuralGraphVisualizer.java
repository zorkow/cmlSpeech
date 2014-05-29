
//
package io.github.egonw;

/**
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.awt.geom.Rectangle2D;
import javax.swing.JScrollPane;

import java.util.List;
import java.util.Set;
import org.openscience.cdk.interfaces.IAtom;
import java.util.ArrayList;
import javax.vecmath.Point2d;

/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 *
 * @author Barak Naveh
 *
 * @since Aug 3, 2003
 */
public class StructuralGraphVisualizer {
    private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
    private static final Dimension DEFAULT_SIZE = new Dimension( 530, 320 );

    private static final int scale = 150;
    private static final int offset = 10;
    private static int minX = 0;
    private static int minY = 0;

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
    private JGraphModelAdapter m_jgAdapter;

    /**
     * @see java.applet.Applet#init().
     */
    public void init(SimpleGraph sg, List<RichAtomSet> majorSystems, Set<IAtom> singletonAtoms) {
        ListenableGraph g = new ListenableUndirectedGraph( sg );

        m_jgAdapter = new JGraphModelAdapter( g );

        JGraph jgraph = new JGraph( m_jgAdapter );

        JScrollPane scroller = new JScrollPane(jgraph);
        JFrame frame = new JFrame("The Body");
        frame.setSize(600,600);
        frame.add(scroller);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        List<NamedPoint> points = new ArrayList();
        points.addAll(computeCentroids(majorSystems));
        points.addAll(computeAtoms(singletonAtoms));
        positionPoints(points);

        
        jgraph.getGraphLayoutCache().reload();
        jgraph.repaint();

    }

    private void positionPoints(List<NamedPoint> points) {
        points.stream()
            .forEach(p -> positionVertexAt(p.getName(),
                                           p.getX() - this.minX + this.offset,
                                           p.getY() - this.minY + this.offset));
    }


    private List<NamedPoint> computeCentroids(List<RichAtomSet> systems) {
        List<NamedPoint> points = new ArrayList();
        for (RichAtomSet system : systems) {
            double x = 0;
            double y = 0;
            int n = 0;
            for (IAtom atom : system.container.atoms()) {
                Point2d x2d = atom.getPoint2d();
                System.out.printf("%s %f %f\n", x2d, x2d.x, x2d.y);
                x += (x2d.x * scale);
                y += (x2d.y * scale);
                n++;
            }
            NamedPoint point = new NamedPoint(system.getId(), (int)x/n, (int)y/n);
            this.minX = Math.min(this.minX, point.getX());
            this.minY = Math.min(this.minY, point.getY());
            points.add(point);
        }
        return points;
    }
    

    private List<NamedPoint> computeAtoms(Set<IAtom> atoms) {
        List<NamedPoint> points = new ArrayList();
        System.out.println("Atoms");
        for (IAtom atom : atoms) {
            Point2d x2d = atom.getPoint2d();
            System.out.printf("%s %f %f\n", x2d, x2d.x, x2d.y);
            NamedPoint point = new NamedPoint(atom.getID(), (int)(x2d.x * scale), (int)(x2d.y * scale));
            this.minX = Math.min(this.minX, point.getX());
            this.minY = Math.min(this.minY, point.getY());
            points.add(point);
        }
        return points;
    }
    
    // private void adjustDisplaySettings( JGraph jg ) {
    //     jg.setPreferredSize( DEFAULT_SIZE );

    //     Color  c        = DEFAULT_BG_COLOR;
    //     String colorStr = null;

    //     try {
    //         colorStr = getParameter( "bgcolor" );
    //     }
    //      catch( Exception e ) {}

    //     if( colorStr != null ) {
    //         c = Color.decode( colorStr );
    //     }

    //     jg.setBackground( c );

    //     // positionVertexAt( "v2", 60, 200 );
    //     // positionVertexAt( "v3", 310, 230 );
    //     // positionVertexAt( "v4", 380, 70 );
    // }


    private void positionVertexAt( Object vertex, int x, int y ) {
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        Map              attr = cell.getAttributes(  );
        Rectangle2D      b    = GraphConstants.getBounds( attr );

        GraphConstants.setBounds( attr, new Rectangle( x, y, (int)b.getWidth(), (int)b.getHeight() ) );

        Map cellAttr = new HashMap(  );
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null );
    }
}
