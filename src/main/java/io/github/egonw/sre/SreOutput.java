// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


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

import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.connection.Connection;
import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;
import io.github.egonw.structure.RichBond;
import io.github.egonw.structure.RichStructure;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Constructs structural annotations for Sre.
 */

public class SreOutput extends SreXML {

    public SreOutput() {
        super();
        this.compute();
    }

    @Override
    public void compute() {
        for (RichAtom structure : RichStructureHelper.getAtoms()) {
            this.annotations.registerAnnotation(structure.getId(), SreNamespace.Tag.ATOM);
            this.toSreStructure(structure);
        }
        for (RichBond structure : RichStructureHelper.getBonds()) {
            annotations.registerAnnotation(structure.getId(), SreNamespace.Tag.BOND);
            this.toSreStructure(structure);
        }
        for (RichAtomSet structure : RichStructureHelper.getAtomSets()) {
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
            case BRIDGEATOM:
                tag = SreNamespace.Tag.BRIDGEATOM;
                break;
            case SHAREDATOM:
                tag = SreNamespace.Tag.SHAREDATOM;
                break;
            case SPIROATOM:
                tag = SreNamespace.Tag.SPIROATOM;
                break;
            case CONNECTINGBOND:
            default:
                tag = SreNamespace.Tag.CONNECTINGBOND;
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
                      .filter(RichStructureHelper::isBond)
                      .collect(Collectors.toSet()));
        this.toSreSet(id, SreNamespace.Tag.SUBSYSTEM, structure.getSubSystems());
        this.toSreSet(id, SreNamespace.Tag.SUPERSYSTEM, structure.getSuperSystems());
        this.toSreSet(id, SreNamespace.Tag.CONNECTINGATOMS, structure.getConnectingAtoms());
    }

}

