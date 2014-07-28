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
        describeTopLevel(doc.getRootElement());
        describeMajorLevel();
        this.description.finalize();
    }


    private void describeMajorLevel() {
        this.analysis.getMinorSystems().stream().forEach(this::describeAtomSet);
    }

    private void describeAtomSet(RichAtomSet system) {
        switch (system.type) {
        case ALIPHATIC:
            describeAliphaticChain(system);
                // describeAliphaticChain(system, (Element)systemElement.get(0));
            break;
        case ISOLATED:
            describeIsolatedRing(system);
            break;
        }
    }


    private void describeIsolatedRing(RichAtomSet system) {
        String descr = "Ring with " + system.getStructure().getAtomCount() + " elements.";
        descr += " " + this.describeReplacements(system);
        descr += " " + this.describeMultiBonds(system);
        descr += " " + this.describeSubstitutions(system);
        this.description.addDescription(2, descr,
                                        this.describeComponents(system));
        this.describeRingStepwise(system);
    }
    

    private String describeReplacements(RichAtomSet system) {
        String descr = "";
        Iterator<String> iterator = system.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            RichAtom atom = this.analysis.getRichAtom(value);
            if (!atom.isCarbon()) {
                descr += " with " + this.describeAtom(atom) 
                    + " at position "
                    + system.getAtomPosition(value).toString();
            }
        }
        return descr;
    }

    private void describeRingStepwise(RichAtomSet system) {
        this.describeAliphaticChainStepwise(system);
    }

    
    private void describeAliphaticChain(RichAtomSet system) {
        String descr = "Aliphatic chain of length " + system.getStructure().getAtomCount();
        descr += " " + this.describeMultiBonds(system);
        descr += " " + this.describeSubstitutions(system);
        this.description.addDescription(2, descr,
                                        this.describeComponents(system));
        this.describeAliphaticChainStepwise(system);
    }
 

   private String describeSubstitutions(RichAtomSet system) {
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



    private String describeMultiBonds(RichAtomSet system) {
        Map<Integer, String> bounded = new TreeMap<Integer, String>();
        for (IBond bond : system.getStructure().bonds()) {
            String order = this.describeBond(bond, true);
            if (order.equals("")) { continue; }
            // TODO (sorge) Make this one safer!
            Iterator<String> atoms = this.analysis.getRichBond(bond).getComponents().iterator();
            Integer atomA = system.getAtomPosition(atoms.next());
            Integer atomB = system.getAtomPosition(atoms.next());
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


    private void describeAliphaticChainStepwise(RichAtomSet system) {
        for (int i = 1; i <= system.componentPositions.size(); i++) {            
            this.description.addDescription
                (3,
                 this.describeAtomConnections(system, system.getPositionAtom(i)),
                 // This is temporary!
                 this.describeAtomComponents(system.getPositionAtom(i)));
        }
    }


    // This is temporary!
    private SreElement describeAtomComponents(String atom) {
        List<String> components = new ArrayList<String>();
        components.add(atom);
        RichAtom richAtom = this.analysis.getRichAtom(atom);
        for (Connection connection : richAtom.getConnections()) {
            components.add(connection.getConnector());
            components.add(connection.getConnected());
        }
        return this.describeComponents(components);
    }

    private String describeAtomConnections(RichAtomSet system, String atom) {
        return this.describeAtomConnections(system, this.analysis.getRichAtom(atom));
    }
        

    private String describeAtomConnections(RichAtomSet system, RichAtom atom) {
        List<String> result = new ArrayList<String>();
        for (Connection connection : atom.getConnections()) {
            System.out.println("this is a connection:" + connection.toString());
            result.add(describeConnectingBond(system, connection));
        }
        Joiner joiner = Joiner.on(" ");
        return describeAtomPosition(atom) + " "
            + this.describeHydrogenBonds(atom.getStructure()) + " "
            + joiner.join(result);
    }

    private String describeConnectingBond(RichAtomSet system, Connection connection) {
        // TODO (sorge) Make this one safer!
        if (connection.getType() != Connection.Type.CONNECTINGBOND) {
            throw(new SreException("Wrong connection type in structure."));
        }
        String bond = this.describeBond(this.analysis.
                                        getRichBond(connection.getConnector()).getStructure(), false);
        String atom = this.describeAtomPosition(connection.getConnected());
        return bond + " bonded to " + atom;
    }


    private String describeBond(IBond bond, Boolean ignoreSingle) {
        IBond.Order order = bond.getOrder();
        if (ignoreSingle && order == IBond.Order.SINGLE) {
            return "";
        } else {
            return describeBondOrder(order);
        }
    }


    private String describeBondOrder(IBond.Order bond) {
        return bond.toString().toLowerCase();
    }


    // TODO (sorge) Combine the following two methods.
    private String describeAtomPosition(RichAtom atom) {
        Integer position = this.analysis.getAtomPosition(atom.getId());
        return describeAtom(atom) + " " + position.toString();
    }


    private String describeAtomPosition(String atom) {
        Integer position = this.analysis.getAtomPosition(atom);
        return describeAtom(this.analysis.getRichAtom(atom)) 
            + " " + position.toString();
    }


    private String describeAtom(RichAtom atom) {
        return describeAtom(atom.getStructure());
    }

    private String describeAtom(IAtom atom) {
        return this.atomTable.lookup(atom);
    }


    private String describeHydrogenBonds(IAtom atom) {
        String hydrogens = describeHydrogens(atom);
        return hydrogens.equals("") ? "" :  
            "bonded to " + hydrogens;
    }

    private String describeHydrogens(IAtom atom) {
        Integer count = atom.getImplicitHydrogenCount();
        switch (count) {
        case 0:
            return "";
        case 1:
            return count.toString() + " hydrogen";
        default:
            return count.toString() + " hydrogens";
        }
    }


    // private List<String> describeAtomSetElements(String id) {
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

   private SreElement describeComponents(RichStructure system) {
       return this.describeComponents
           (Lists.newArrayList(system.getComponents()));
    }

    private SreElement describeComponents(List<String> components) {
        SreElement element = new SreElement(SreNamespace.Tag.COMPONENT);
        for (String component : components) {
            element.appendChild(this.toSreElement(component));
        }
        return element;
    }

    private void describeTopLevel(Element doc) {
        String content = describeName(doc);
        List<String> elements = SreUtil.xpathValueList(doc, 
                                                       "//cml:atom/@id | //cml:bond/@id");
        this.description.addDescription(1, content, this.describeComponents(elements));
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
