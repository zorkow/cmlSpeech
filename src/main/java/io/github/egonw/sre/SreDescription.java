/**
 * @file   SreDescription.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Fri May 30 00:14:03 2014
 * 
 * @brief  Class for SRE Descriptions.
 * 
 * 
 */

//
package io.github.egonw.sre;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;
import java.util.ArrayList;


/**
 *
 */

public class SreDescription extends SreElement {

    private static SortedMap<Integer, List<Element>> descriptionNodes = new TreeMap<>();

    SreDescription() {
        super(SreNamespace.Tag.DESCRIPTIONS);
    }


    public Element makeContent(String content) {
        return new SreElement(SreNamespace.Tag.CONTENT, content);
    }
    

    public void addDescription(Integer level, String content, Element element) {
        Element subdesc = new SreElement(SreNamespace.Tag.SUBDESC);
        subdesc.appendChild(makeContent(content));
        subdesc.appendChild(element);
        Attribute levelAtt = new SreAttribute(SreNamespace.Attribute.LEVEL.attribute, level.toString());
        subdesc.addAttribute(levelAtt);
        addDescription(level, subdesc);
    }

    public void addDescription(Integer level, String content, List<String> elements) {
        Element subdesc = new SreElement(SreNamespace.Tag.SUBDESC);
        Text text = new Text(content);
        subdesc.appendChild(text);
        Attribute levelAtt = new SreAttribute(SreNamespace.Attribute.LEVEL.attribute, level.toString());
        subdesc.addAttribute(levelAtt);
        elements.stream().forEach(e -> SreUtil.appendAttribute
                                  (subdesc,
                                   SreNamespace.Attribute.ELEMENTS.attribute,
                                   e));
        addDescription(level, subdesc);
    }


    // Just necessary because Java is so destructive!
    public void addDescription(Integer level, String content, List<String> atoms, List<String> bonds) {
        List<String> aux = new ArrayList<String>(atoms);
        aux.addAll(bonds);
        addDescription(level, content, aux);
    }


    public void addDescription(Integer level, Element element) {
        List<Element> list = SreDescription.descriptionNodes.get(level);
        if (list == null) {
            list = new ArrayList<Element>();
            SreDescription.descriptionNodes.put(level, list);
        }
        list.add(element);
    }

    public void finalize() {
        for (Integer key : SreDescription.descriptionNodes.keySet()) {
            Element desc = new SreElement(SreNamespace.Tag.DESC);
            Attribute levelAtt = new SreAttribute(SreNamespace.Attribute.LEVEL.attribute, key.toString());
            desc.addAttribute(levelAtt);
            for (Element subdesc : SreDescription.descriptionNodes.get(key)) {
                desc.appendChild(subdesc);
            }
            this.appendChild(desc);
        }
    }
}
