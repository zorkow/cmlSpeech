
//
package io.github.egonw;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import nu.xom.Attribute;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import java.util.Set;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Element;

/**
 *
 */

public class SreSpeech extends SreXML {
    
    private AtomTable atomTable = AtomTable.getInstance();
    public Document doc;

    SreSpeech(StructuralAnalysis analysis) {
        super(analysis);
        this.compute();
    }

    SreSpeech(StructuralAnalysis analysis, Document document) {
        super(analysis);
        this.doc = document;
        this.compute();
    }

    @Override
    public void compute() {
        this.analysis.getAtoms().stream().forEach(this::atom);
        this.analysis.getBonds().stream().forEach(this::bond);
        this.analysis.getAtomSets().stream().forEach(this::atomSet);
    }


    private void atom(RichAtom atom) {
        RichAtomSet system = this.analysis.getRichAtomSet((atom.getSuperSystems().iterator()).next());
        String id = atom.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM, this.speechAtom(atom));
        this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CHILDREN, atom.getSubSystems());
        this.describeAtomConnections(system, atom, id);
    }


    private void bond(RichBond bond) {
        String id = bond.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.BOND, this.speechBond(bond));
        this.annotations.addAttribute(id, new SreAttribute(SreNamespace.Attribute.ORDER,
                                                           this.describeBond(bond, false)));
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, bond.getComponents());
    }


    private void atomSet(RichAtomSet atomSet) {
        String id = atomSet.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM, this.speechAtomSet(atomSet));
        this.toSreSet(id, SreNamespace.Tag.PARENTS, atomSet.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.getSubSystems());
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
        this.describeConnections(atomSet);
    }


    private SreAttribute speechAttribute(String speech) {
        return new SreAttribute(SreNamespace.Attribute.SPEECH, speech);
    }


    private SreAttribute speechAtom(RichAtom atom) {
        return speechAttribute(describeAtomPosition(atom) + " "
                              + this.describeHydrogenBonds(atom.getStructure()));
    }

    private SreAttribute speechBond(RichBond bond) {
        return speechAttribute(this.describeBond(bond, false) + " bond");
    }


    private SreAttribute speechAtomSet(RichAtomSet atomSet) {
        String result = "";
        switch (atomSet.type) {
        case MOLECULE:
            result = describeMolecule(atomSet);
            break;
        case ALIPHATIC:
            result = describeAliphaticChain(atomSet);
            break;
        case ISOLATED:
            result =  describeIsolatedRing(atomSet);
        }
        return speechAttribute(result);
    }


    private String describeMolecule(RichStructure structure) {
        String id = structure.getId();
        Node element = SreUtil.xpathQueryElement(this.doc.getRootElement(), "//cml:atomSet[@id='" + id + "']");
        System.out.println(element);
        
        return SreUtil.xpathValue((Element)element, "@sre:name | @sre:iupac | @sre:formula");
    }


    private String describeAliphaticChain(RichAtomSet system) {
        String descr = "Aliphatic chain of length " + system.getStructure().getAtomCount();
        descr += " " + this.describeMultiBonds(system);
        descr += " " + this.describeSubstitutions(system);
        return descr;
    }


    private String describeIsolatedRing(RichAtomSet system) {
        String descr = "Ring with " + system.getStructure().getAtomCount() + " elements.";
        descr += " " + this.describeReplacements(system);
        descr += " " + this.describeMultiBonds(system);
        descr += " " + this.describeSubstitutions(system);
        this.describeRingStepwise(system);
        return descr;
    }
    

    private void describeConnections(RichChemObject structure) {
        String id = structure.getId();
        for (Connection connection : structure.getConnections()) {
            
        }
    }

    private void describeAtomConnections(RichAtomSet system, RichAtom atom, String id) {
        List<String> result = new ArrayList<String>();
        for (Connection connection : atom.getConnections()) {
            String connected = connection.getConnected();
            SreElement element = null;
            if (this.analysis.isAtom(connected)) {
                element = new SreElement(this.analysis.getRichAtom(connected).getStructure());
                } else {
                element = new SreElement(this.analysis.getRichAtomSet(connected).getStructure());
                }
            SreAttribute speech = new SreAttribute(SreNamespace.Attribute.SPEECH, describeConnectingBond(system, connection));
            SreAttribute bond = new SreAttribute(SreNamespace.Attribute.BOND, connection.getConnector());
            SreAttribute ext = new SreAttribute(SreNamespace.Attribute.TYPE, "internal");
            element.addAttribute(speech);
            element.addAttribute(bond);
            element.addAttribute(ext);
            this.annotations.appendAnnotation(id, SreNamespace.Tag.NEIGHBOURS, element);
        }
                                          
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


    private String describeBond(RichBond bond, Boolean ignoreSingle) {
        return describeBond(bond.getStructure(), ignoreSingle);
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
        if (position == null) { return describeAtom(atom) + " unknown position."; }
        return describeAtom(atom) + " " + position.toString();
    }


    private String describeAtomPosition(String atom) {
        Integer position = this.analysis.getAtomPosition(atom);
        if (position == null) { return "Not an atom."; }
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

    // private void describeTopLevel(Element doc) {
    //     String content = describeName(doc);
    //     List<String> elements = SreUtil.xpathValueList(doc, 
    //                                                    "//cml:atom/@id | //cml:bond/@id");
    //     this.description.addDescription(1, content, this.describeComponents(elements));
    // }


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
        for (int i = 1; i <= system.atomPositions.size(); i++) {            
            // this.description.addDescription
            //     (3,
            //      this.describeAtomConnections(system, system.getPositionAtom(i)),
            //      // This is temporary!
            //      this.describeAtomComponents(system.getPositionAtom(i)));
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

}
