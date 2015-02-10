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
package io.github.egonw.sre;

import io.github.egonw.analysis.StructuralAnalysis;
import io.github.egonw.structure.Connection;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichBond;
import io.github.egonw.structure.RichStructure;

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

public class SreOutput extends SreXML {

    public SreOutput(StructuralAnalysis analysis) {
        super(analysis);
        this.compute();
    }

    @Override
    public void compute() {
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
    }


    private void toSreConnections(RichStructure<?> structure) {
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
            case SPIROATOM:
                tag = SreNamespace.Tag.SPIROATOM;
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

    private void toSreStructure(RichStructure<?> structure) {
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

}

