/**
 * @file   SreOutput.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Thu Jun 19 16:34:40 2014
 * 
 * @brief  Singleton class to handle SRE annotations.
 * 
 * 
 */

//
package io.github.egonw;

import java.util.stream.Collectors;

import java.util.Set;

/**
 *
 */

public class SreOutput {

    SreAnnotations annotations = new SreAnnotations();
    StructuralAnalysis analysis;
    
    SreOutput(StructuralAnalysis analysis) {
        this.analysis = analysis;
        this.annotations();
        //this.descriptions();
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



}
