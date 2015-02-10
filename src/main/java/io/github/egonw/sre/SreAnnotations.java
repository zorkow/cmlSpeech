
//
package io.github.egonw.sre;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.xmlcml.cml.element.CMLAtomSet;

import nu.xom.Nodes;
import io.github.egonw.base.CMLNameComparator;

import java.util.TreeMap;
import java.util.SortedMap;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Set;


/**
 *
 */

public class SreAnnotations extends SreElement {

    private SortedMap<String, Element> annotationNodes;

    SreAnnotations() {
        super(SreNamespace.Tag.ANNOTATIONS);
        this.annotationNodes = new TreeMap<>(new CMLNameComparator());
    }

    SreAnnotations(IAtomContainer molecule) {
        super(SreNamespace.Tag.ANNOTATIONS);
        for (IAtom atom : molecule.atoms()) {
            this.getNodeToAnnotate(atom.getID(), SreNamespace.Tag.ATOM);
        }
        for (IBond bond : molecule.bonds()) {
            this.getNodeToAnnotate(bond.getID(), SreNamespace.Tag.BOND);
        }
    }

    public void registerAnnotation(String id, SreNamespace.Tag tag) {
        this.getNodeToAnnotate(id, tag);
    };
        

    public void registerAnnotation(String id, SreNamespace.Tag tag, SreAttribute attr) {
        Element element = this.getNodeToAnnotate(id, tag);
        element.addAttribute(attr);
    };
        

    public void appendAnnotation(String annotate, SreNamespace.Tag tag, Element entry) {
        this.appendAnnotation(this.getNodeToAnnotate(annotate, SreNamespace.Tag.UNKNOWN), 
                              tag, entry);
    }

    public void appendAnnotation(Element annotate, SreNamespace.Tag tag, Element entry) {
        Nodes nodes = SreUtil.xpathQuery(annotate, "//" + tag.tag);
        Element node = null;
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


    public void addAttribute(String id, SreAttribute attr) {
        Element element = this.getNodeToAnnotate(id);
        if (element == null) {
            throw new SreException("Annotation element " + id + 
                                   " does not exist. Attribute cannot be added!");
        }
        element.addAttribute(attr);
    }

    private Element getNodeToAnnotate(String id) {
        return this.annotationNodes.get(id);
    }

    private Element getNodeToAnnotate(String id, SreNamespace.Tag tag) {
        Element element = this.getNodeToAnnotate(id);
        if (element != null) {
            return element;
        }
        Element annotation = new SreElement(SreNamespace.Tag.ANNOTATION);
        Element node = new SreElement(tag, id);
        annotation.appendChild(node);
        this.annotationNodes.put(id, annotation);
        return annotation;
    }


    public SreElement retrieveAnnotation(String id, SreNamespace.Tag tag) {
        Element element = this.annotationNodes.get(id);
        if (element == null) {
            return null;
        }
        return (SreElement)SreUtil.xpathQuery(element, "//" + tag.tag).get(0);
    }


    public void finalize() {
        for (String key : this.annotationNodes.keySet()) {
            this.appendChild(this.annotationNodes.get(key));
        }
    }

    public String toString() {
        String result = "";
        for (String key : this.annotationNodes.keySet()) {
            result += key + ": " + this.annotationNodes.get(key).toXML() + "\n";
        }
        return result;
    }

}
