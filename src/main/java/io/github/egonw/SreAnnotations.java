
//
package io.github.egonw;

import nu.xom.Element;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Comparator;


/**
 *
 */

public class SreAnnotations extends SreElement {

    public static Comparator<String> CMLNameComparator = 
        new Comparator<String>() {
        
        public int compare(String name1, String name2) {
            String reg1 = "[0-9]*";
            String alpha1 = name1.replaceAll(reg1, "");
            String alpha2 = name2.replaceAll(reg1, "");
            // System.out.println(alpha1);
            // System.out.println(alpha2);
            if (alpha1.equals(alpha2)) {
                String reg2 = "[a-z]*";
                Integer numer1 = Integer.parseInt(name1.replaceAll(reg2, ""));
                Integer numer2 = Integer.parseInt(name2.replaceAll(reg2, ""));
                // System.out.println(numer1);
                // System.out.println(numer2);
                if (numer1 == numer2) {
                    // System.out.println("Case 0");
                    return 0;
                }
                if (numer1 < numer2) {
                    // System.out.println("Case 1");
                    return -1;
                }
                    // System.out.println("Case 2");
                return 1;
                }
            if (alpha1.equals("as") && alpha2.equals("b")) {
                // System.out.println("Case 3");
                return 1;
            }
            if (alpha1.equals("b") && alpha2.equals("as")) {
                // System.out.println("Case 3");
                return -1;
            }
                    // System.out.println("Case 4");
            return alpha1.compareTo(alpha2);
        }
    };

    private static SortedMap<String, Element> annotationNodes = new TreeMap(CMLNameComparator);

    SreAnnotations() {
        super(SreNamespace.Tag.ANNOTATIONS);
    }


    public void appendAnnotation(IAtom annotate, SreNamespace.Tag tag, Element entry) {
        this.appendAnnotation(this.getNodeToAnnotate(annotate.getID(), SreNamespace.Tag.ATOM), tag, entry);
    }

    public void appendAnnotation(IBond annotate, SreNamespace.Tag tag, Element entry) {
        this.appendAnnotation(this.getNodeToAnnotate(annotate.getID(), SreNamespace.Tag.BOND), 
                         tag, entry);
    }

    public void appendAnnotation(CMLAtomSet annotate, SreNamespace.Tag tag, Element entry) {
        this.appendAnnotation(this.getNodeToAnnotate(annotate.getId(), SreNamespace.Tag.ATOMSET), 
                         tag, entry);
    }

    public void appendAnnotation(Element annotate, SreNamespace.Tag tag, Element entry) {
        XPathContext xc = new XPathContext();
        xc.addNamespace(SreNamespace.getInstance().prefix, SreNamespace.getInstance().uri);
        Nodes nodes = annotate.query("//" + tag.tag, xc);
        Element node = null;
        System.out.println("here2: " + tag.tag);
        if (nodes.size() == 0) {
            node = new SreElement(tag);
            annotate.appendChild(node);
        } else {
            node = (Element)nodes.get(0);
        }
        node.appendChild(entry);
    }
    
    public void appendAnnotation(Element annotate, String id, SreNamespace.Tag tag, Element entry) {
        SreNamespace.Tag elementTag = null;
        System.out.println("hereA: " + tag.tag);
        switch (annotate.getLocalName()) {
        case "atom":
            elementTag = SreNamespace.Tag.ATOM;
            break;
        case "bond":
            elementTag = SreNamespace.Tag.BOND;
            break;
        case "atomSet":
            elementTag = SreNamespace.Tag.ATOMSET;
            break;
        default:
            //TODO(sorge) what to do here?
        }
        Element node = this.getNodeToAnnotate(id, elementTag);
        this.appendAnnotation(node, tag, entry);
    }

    // private Element getNodeToAnnotate(String id) {
    //     Nodes nodes = this.query("//" + SreNamespace.Tag.ANNOTATION + "/*[1][text()=" + id + "]");
    //     if (nodes.size() != 0) {
    //         return (Element)nodes.get(0).getParent();
    //     }
    // }

    private Nodes xpathQuery(String query) {
        XPathContext xc = new XPathContext();
        xc.addNamespace(SreNamespace.getInstance().prefix, SreNamespace.getInstance().uri);
        return this.query(query, xc);
    }


    // private Element getNodeToAnnotate(String id, SreNamespace.Tag tag) {
    //     Nodes nodes = xpathQuery("//" + SreNamespace.Tag.ANNOTATION + "/*[1][text()=" + id + "]");
    //     if (nodes.size() != 0) {
    //         System.out.println("Nodes found: " + nodes);
    //         return (Element)nodes.get(0).getParent();
    //     }
    //     Element annotation = new SreElement(SreNamespace.Tag.ANNOTATION);
    //     Element node = new SreElement(tag, id);
    //     this.appendChild(annotation);
    //     annotation.appendChild(node);
    //     return node;
    // }


    private Element getNodeToAnnotate(String id, SreNamespace.Tag tag) {
        Element element = this.annotationNodes.get(id);
        if (element != null) {
            return element;
        }
        Element annotation = new SreElement(SreNamespace.Tag.ANNOTATION);
        Element node = new SreElement(tag, id);
        annotation.appendChild(node);
        this.annotationNodes.put(id, annotation);
        return annotation;
    }


    public void finalize() {
        System.out.println("Finalizing...");
        for (String key : this.annotationNodes.keySet()) {
            System.out.println(key + " : " + this.annotationNodes.get(key));
            this.appendChild(this.annotationNodes.get(key));
        }
    }
}
