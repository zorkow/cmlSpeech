/**
 * @file   SreSpeech.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:33:18 2015
 * 
 * @brief  Sre speech output.
 * 
 * 
 */

//
package io.github.egonw.sre;

import com.google.common.base.Joiner;

import io.github.egonw.analysis.StructuralAnalysis;
import io.github.egonw.connection.Connection;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichBond;
import io.github.egonw.structure.RichChemObject;
import io.github.egonw.structure.RichStructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Constructs the Sre speech annotations.
 */

public class SreSpeech extends SreXML {
    
    public Document doc;

    SreSpeech(StructuralAnalysis analysis) {
        super(analysis);
        this.compute();
    }

    public SreSpeech(StructuralAnalysis analysis, Document document) {
        super(analysis);
        this.doc = document;
        this.compute();
    }

    @Override
    public void compute() {
        // TODO (sorge) This should not be set here, but during positions computations!
        RichAtomSet molecule = this.analysis.getRichAtomSet(this.analysis.getMolecule().getID());
        molecule.componentPositions = this.analysis.majorPath;
        // Describe the molecule.
        this.atomSet(molecule);

        // Describe the first level.
        for (Integer i = 1; i <= this.analysis.majorPath.size(); i++) {
            String structure = this.analysis.majorPath.get(i);
            if (this.analysis.isAtom(structure)) {
                this.atom(this.analysis.getRichAtom(structure), molecule);
            } else {
                RichAtomSet atomSet = this.analysis.getRichAtomSet(structure);
                this.atomSet(atomSet, molecule);
                // TODO (sorge) Deal with FUSED rings here.
                // Describe the bottom level.
                for (String atom : atomSet.componentPositions) {
                    this.atom(this.analysis.getRichAtom(atom), atomSet);
                }
            }
        }

        // Finally add the bonds.
        this.analysis.getBonds().stream().forEach(this::bond);
    }


    /** 
     * Turns a speech string into an attribute.
     * 
     * @param speech The speech string.
     * 
     * @return The newly created attribute.
     */
    private SreAttribute speechAttribute(String speech) {
        return new SreAttribute(SreNamespace.Attribute.SPEECH, speech);
    }


    // Atom to speech translation.
    private void atom(RichAtom atom, RichAtomSet system) {
        String id = atom.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOM, this.speechAtom(atom));
        this.toSreSet(id, SreNamespace.Tag.PARENTS, atom.getSuperSystems());
        this.describeConnections(system, atom, id);
    }


    private SreAttribute speechAtom(RichAtom atom) {
        return speechAttribute(describeAtomPosition(atom) + " "
                               + this.describeHydrogenBonds(atom.getStructure()));
    }


    private String describeAtom(RichAtom atom) {
        return AtomTable.lookup(atom.getStructure());
    }


    private String describeAtomPosition(RichAtom atom) {
        Integer position = this.analysis.getPosition(atom.getId());
        if (position == null) { return describeAtom(atom) + " unknown position."; }
        return describeAtom(atom) + " " + position.toString();
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


    // Bond to speech translation.
    private void bond(RichBond bond) {
        String id = bond.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.BOND, this.speechBond(bond));
        this.annotations.addAttribute(id, new SreAttribute(SreNamespace.Attribute.ORDER,
                                                           this.describeBond(bond, false)));
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, bond.getComponents());
    }


    private SreAttribute speechBond(RichBond bond) {
        return speechAttribute(this.describeBond(bond, false) + " bond");
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


    // AtomSet to speech translation.
    private void atomSet(RichAtomSet atomSet) {
        String id = atomSet.getId();
        this.annotations.registerAnnotation(id, SreNamespace.Tag.ATOMSET, this.speechAtomSet(atomSet));
        this.toSreSet(id, SreNamespace.Tag.PARENTS, atomSet.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CHILDREN, atomSet.componentPositions);
        this.toSreSet(id, SreNamespace.Tag.COMPONENT, atomSet.getComponents());
    }

    private void atomSet(RichAtomSet atomSet, RichAtomSet superSystem) {
        atomSet(atomSet);
        this.describeConnections(superSystem, atomSet, atomSet.getId());
    }


    private SreAttribute speechAtomSet(RichAtomSet atomSet) {
        String result = describeAtomSet(atomSet);
        switch (atomSet.type) {
        case MOLECULE:
            break;
        case ALIPHATIC:
        case ISOLATED:
            result += " " + this.describeMultiBonds(atomSet);
            result += " " + this.describeSubstitutions(atomSet);
            break;
        case FUSED:
        case FUNCGROUP:
            break;
        }
        return speechAttribute(result);
    }


    private String describeAtomSet(RichAtomSet atomSet) {
        switch (atomSet.type) {
        case MOLECULE:
            return describeMolecule(atomSet);
        case FUSED:
            return describeFusedRing(atomSet);
        case ALIPHATIC:
            return describeAliphaticChain(atomSet);
        case ISOLATED:
            return describeIsolatedRing(atomSet);
        case FUNCGROUP:
            return describeFunctionalGroup(atomSet);
        default:
            return "";
        }
    }


    private String describeMolecule(RichStructure<?> structure) {
        String id = structure.getId();
        Node element = SreUtil.xpathQueryElement(this.doc.getRootElement(), "//cml:atomSet[@id='" + id + "']");
        return SreUtil.xpathValue((Element)element, "@sre:name | @sre:iupac | @sre:formula");
    }


    private String describeAliphaticChain(RichAtomSet system) {
        return "Aliphatic chain of length " +
               system.getStructure().getAtomCount();
    }
    

    private String describeFusedRing(RichAtomSet system) {
        String descr = "Fused ring system with " + system.getSubSystems().size() + " subrings.";
        descr += " " + this.describeReplacements(system);
        return descr;
    }


    private String describeIsolatedRing(RichAtomSet system) {
        String descr = "Ring with " + system.getStructure().getAtomCount() + " elements.";
        descr += " " + this.describeReplacements(system);
        return descr;
    }


    private String describeFunctionalGroup(RichAtomSet system) {
        return "Functional group " + system.name + ".";
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
                    + system.getPosition(value).toString();
            }
        }
        return descr;
    }

    
   private String describeSubstitutions(RichAtomSet system) {
        SortedSet<Integer> subst = new TreeSet<Integer>();
        for (String atom : system.getConnectingAtoms()) {
            subst.add(system.getPosition(atom));
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
            Integer atomA = system.getPosition(atoms.next());
            Integer atomB = system.getPosition(atoms.next());
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


    private void describeConnections(RichAtomSet system, RichChemObject block, String id) {
        List<String> result = new ArrayList<String>();
        Integer count = 0;
        for (Connection connection : block.getConnections()) {
            String connected = connection.getConnected();
            if (!system.componentPositions.contains(connected)) {
                continue;
            }
            count++;

            // Build the XML elements structure.
            SreElement element = new SreElement(SreNamespace.Tag.NEIGHBOUR);
            SreElement positions = new SreElement(SreNamespace.Tag.POSITIONS);

            // Add type depended attributes.
            describeConnection(connection, element, positions);
            
            if (this.analysis.isAtom(connected)) {
                element.appendChild(new SreElement(this.analysis.getRichAtom(connected).getStructure()));
                } else {
                element.appendChild(new SreElement(this.analysis.getRichAtomSet(connected).getStructure()));
                }

            // Putting it all together.
            SreElement position = new SreElement(SreNamespace.Tag.POSITION, count.toString());
            positions.appendChild(position);
            SreElement via = new SreElement(SreNamespace.Tag.VIA);
            via.appendChild(positions);
            element.appendChild(via);
            this.annotations.appendAnnotation(id, SreNamespace.Tag.NEIGHBOURS, element);
        }
    }

    
    private void describeConnection(Connection connection,
                                    SreElement element, SreElement position) {
        String connector = connection.getConnector();
        String connected = connection.getConnected();

        String elementSpeech = "";
        SreAttribute connSpeech;
        SreNamespace.Attribute connAttr;
        switch(connection.getType()) {
        case CONNECTINGBOND:
            elementSpeech = describeConnectingBond(connector, connected);
            connAttr = SreNamespace.Attribute.BOND;
            connSpeech = this.speechBond(this.analysis.getRichBond(connector));
            break;
        case BRIDGEATOM:
            elementSpeech = describeBridgeAtom(connector, connected);
            connAttr = SreNamespace.Attribute.ATOM;
            connSpeech = this.speechAtom(this.analysis.getRichAtom(connector));
            break;
        case SHAREDATOM:
            elementSpeech = describeSharedAtom(connector, connected);
            connAttr = SreNamespace.Attribute.ATOM;
            connSpeech = this.speechAtom(this.analysis.getRichAtom(connector));
            break;
        case SPIROATOM:
            elementSpeech = describeSpiroAtom(connector, connected);
            connAttr = SreNamespace.Attribute.ATOM;
            connSpeech = this.speechAtom(this.analysis.getRichAtom(connector));
            break;
        case SHAREDBOND:
            // elementSpeech = ???
            connAttr = SreNamespace.Attribute.BOND;
            connSpeech = this.speechBond(this.analysis.getRichBond(connector));
            break;
        default:
            throw(new SreException("Unknown connection type in structure."));
        }
        element.addAttribute(this.speechAttribute(elementSpeech));
        position.addAttribute(new SreAttribute(connAttr, connector));
        position.addAttribute(connSpeech);
        position.addAttribute(new SreAttribute(SreNamespace.Attribute.TYPE,
                                               connection.getType().toString().toLowerCase()));
    }


    private String describeBridgeAtom(String connector, String connected) {
        String atom = this.describeAtom(this.analysis.getRichAtom(connector));
        RichAtomSet connectedSet = this.analysis.getRichAtomSet(connected);
        String structure = this.describeAtomSet(connectedSet);
        return "bridge atom " + atom + " to " + structure;
    }


    private String describeSpiroAtom(String connector, String connected) {
        String atom = this.describeAtom(this.analysis.getRichAtom(connector));
        RichAtomSet connectedSet = this.analysis.getRichAtomSet(connected);
        String structure = this.describeAtomSet(connectedSet);
            return "spiro atom " + atom + " to " + structure;
    }


    private String describeSharedAtom(String connector, String connected) {
        String atom = this.describeAtom(this.analysis.getRichAtom(connector));
        RichAtomSet connectedSet = this.analysis.getRichAtomSet(connected);
        String structure = this.describeAtomSet(connectedSet);
        return "shared " + atom + " atom with " + structure;
    }


    private String describeConnectingBond(String connector, String connected) {
        String bond = this.describeBond(this.analysis.getRichBond(connector), false);
        String structure = this.analysis.isAtom(connected) ?
            this.describeAtomPosition(this.analysis.getRichAtom(connected)) :
            this.describeAtomSet(this.analysis.getRichAtomSet(connected));
        return bond + " bonded to " + structure;
    }
}
