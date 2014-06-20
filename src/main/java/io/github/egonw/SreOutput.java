/**
 * @file   SreOutput.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Thu Jun 19 16:34:40 2014
 * 
 * @brief  Class to handle SRE annotations.
 * 
 * 
 */

//
package io.github.egonw;

import java.util.stream.Collectors;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import nu.xom.Element;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;
import nu.xom.Document;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.interfaces.IBond;
import java.util.Iterator;
import com.google.common.base.Joiner;
import java.util.SortedSet;
import java.util.TreeSet;
import org.openscience.cdk.interfaces.IAtom;

/**
 *
 */

public class SreOutput {

    SreAnnotations annotations = new SreAnnotations();
    StructuralAnalysis analysis;
    
    SreOutput(StructuralAnalysis analysis) {
        this.analysis = analysis;
        this.annotations();
    }

    public SreAnnotations getAnnotations() {
        return this.annotations;
    }

    public void annotations() {
        
        for (RichAtom structure : this.analysis.getAtoms()) {
            this.annotations.registerAnnotation(structure.getId(), SreNamespace.Tag.ATOM);
            this.toSreStructure(structure);
        }
        for (RichBond structure : this.analysis.getBonds()) {
            annotations.registerAnnotation(structure.getId(), SreNamespace.Tag.BOND);
            this.toSreStructure(structure);
        }
        for (RichAtomSet structure : this.analysis.getAtomSets()) {
            annotations.registerAnnotation(structure.getId(), SreNamespace.Tag.ATOMSET);
            this.toSreStructure(structure);
        }
        annotations.finalize();
    }


    private void toSreSet(String annotate, SreNamespace.Tag tag, Set<String> set) {
        for (String element : set) {
            this.annotations.appendAnnotation(annotate, tag, this.toSreElement(element));
        }
    }

    private void toSreConnections(RichStructure structure) {
        String id = structure.getId();
        Set<Connection> connections = structure.getConnections();
        for (Connection connection : connections) {
            SreNamespace.Tag tag;
            switch (connection.getType()) {
            case SHAREDBOND:
                tag = SreNamespace.Tag.SHAREDBOND;
                break;
            case SHAREDATOM:
                tag = SreNamespace.Tag.SHAREDATOM;
                break;
            case CONNECTINGBOND:
            default:
                tag = SreNamespace.Tag.CONNECTION;
                break;
            }
            this.annotations.appendAnnotation(id, SreNamespace.Tag.CONNECTIONS,
                                              new SreElement(tag,
                                                             this.toSreElement(connection.getConnector()), 
                                                             this.toSreElement(connection.getConnected())));
        }
    }

    private void toSreStructure(RichStructure structure) {
        String id = structure.getId();
        this.toSreSet(id, SreNamespace.Tag.CONTEXT, structure.getContexts());
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, structure.getComponents());
        this.toSreSet(id, SreNamespace.Tag.EXTERNALBONDS, structure.getExternalBonds());
        this.toSreConnections(structure);
    }


    private void toSreStructure(RichAtomSet structure) {
        String id = structure.getId();
        this.toSreStructure((RichStructure)structure);
        this.toSreSet(id, SreNamespace.Tag.INTERNALBONDS, 
                      structure.getComponents().stream()
                      .filter(this.analysis::isBond)
                      .collect(Collectors.toSet()));
        this.toSreSet(id, SreNamespace.Tag.SUBSYSTEM, structure.getSubSystems());
        this.toSreSet(id, SreNamespace.Tag.SUPERSYSTEM, structure.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CONNECTINGATOMS, structure.getConnectingAtoms());
    }


    private SreElement toSreElement(String name) {
        if (this.analysis.isAtom(name)) {
            return new SreElement(SreNamespace.Tag.ATOM, name);
        }
        if (this.analysis.isBond(name)) {
            return new SreElement(SreNamespace.Tag.BOND, name);
        }
        if (this.analysis.isAtomSet(name)) {
            return new SreElement(SreNamespace.Tag.ATOMSET, name);
        }
        return new SreElement(SreNamespace.Tag.UNKNOWN, name);
    }


    private SreDescription description = new SreDescription();
    private AtomTable atomTable = AtomTable.getInstance();

    // TODO (sorge): This needs serious refactoring! Generating descriptions could be 
    // done easier when keeping some of the information in more dedicated data structures!
    
    public SreDescription getDescriptions() {
        return this.description;
    }


    public void computeDescriptions(Document doc) {
        // Currently using fixed levels!
        descriptionTopLevel(doc.getRootElement());
        descriptionMajorLevel();
        //        this.description.addDescription(2, "Aliphatic Chain", descriptionAtomSetElements("as1"));
        // descriptionMajorLevel();
        this.description.finalize();
    }


    private void descriptionMajorLevel() {
        this.analysis.getMinorSystems().stream().forEach(this::descriptionAtomSet);
    }

    private void descriptionAtomSet(RichAtomSet system) {
        switch (system.type) {
        case ALIPHATIC:
            descriptionAliphaticChain(system);
                // descriptionAliphaticChain(system, (Element)systemElement.get(0));
            break;
        }
    }


    private void descriptionAliphaticChain(RichAtomSet system) {
        String chain = "Aliphatic chain of length " + system.getStructure().getAtomCount();
        chain += " " + this.descriptionMultiBonds(system);
        chain += " " + this.descriptionSubstitutions(system);
        this.description.addDescription(2, chain,
                                        this.descriptionComponents(system));
    }
 

   private String descriptionSubstitutions(RichAtomSet system) {
        SortedSet<Integer> subst = new TreeSet<Integer>();
        for (String atom : system.getConnectingAtoms()) {
            subst.add(system.getAtomPosition(atom));
        }
        switch (subst.size()) {
        case 0:
            return "";
        case 1: 
            return "Substitution at position " + subst.iterator().next();
        default:
            Joiner joiner = Joiner.on(" and ");
            return "Substitutions at position " + joiner.join(subst);
        }
    }



    private String descriptionMultiBonds(RichAtomSet system) {
        Map<Integer, String> bounded = new TreeMap<Integer, String>();
        for (IBond bond : system.getStructure().bonds()) {
            String order = "";
            switch(bond.getOrder()) {
            case SINGLE:
                continue;
            default:
                order = bond.getOrder().toString().toLowerCase();
            }
            Iterator<String> atoms = this.analysis.getRichBond(bond).getComponents().iterator();
            Integer atomA = system.getAtomPosition(atoms.next());
            Integer atomB = system.getAtomPosition(atoms.next());
            System.out.println("here" + atomA + atomB);
            if (atomA > atomB) {
                Integer aux = atomA;
                atomA = atomB;
                atomB = aux;
            }
            bounded.put(atomA, order + " bond between position " + atomA + " and " + atomB + ".");
        }
        Joiner joiner = Joiner.on(" ");
        return joiner.join(bounded.values());
    }


    // private List<String> descriptionAtomSetElements(String id) {
    //     String atoms = SreUtil.xpathValue(this.getAnnotations(),
    //                                       "//cml:atomSet[@id='" + id + "']");
    //     List<String> bonds = SreUtil.xpathValueList(this.getAnnotations(),
    //                                                 "//sre:annotation/sre:atomSet[text()='" + id + 
    //                                                 "']/following-sibling::sre:internalBonds/sre:bond"
    //                                                 );
    //     bonds.add(0, atoms);
    //     bonds.stream().forEach(System.out::println);
    //     return bonds;
    // };

   private SreElement descriptionComponents(RichStructure system) {
       return this.descriptionComponents
           (Lists.newArrayList(system.getComponents()));
    }

    private SreElement descriptionComponents(List<String> components) {
        SreElement element = new SreElement(SreNamespace.Tag.COMPONENT);
        for (String component : components) {
            element.appendChild(this.toSreElement(component));
        }
        return element;
    }

    private void descriptionTopLevel(Element doc) {
        String content = describeName(doc);
        List<String> elements = SreUtil.xpathValueList(doc, 
                                                       "//cml:atom/@id | //cml:bond/@id");
        this.description.addDescription(1, content, this.descriptionComponents(elements));
    }

    private String describeName(Element element) {
        return SreUtil.xpathValue(element, "//@sre:name | //@sre:iupac | //@sre:formula");
    }

    
    

    private List<String> splitAttribute(String attribute) {
        try {
            return Arrays.asList(attribute.split("\\s+"));
        } catch (PatternSyntaxException ex) {
            return new ArrayList<String>();
        }
    }


}
