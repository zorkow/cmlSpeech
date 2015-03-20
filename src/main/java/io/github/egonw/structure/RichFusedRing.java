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
 * @file   RichFusedRing.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich fused ring.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Set;
import org.openscience.cdk.interfaces.IAtom;
import java.util.HashSet;
import java.util.List;
import io.github.egonw.connection.ConnectionType;
import org.openscience.cdk.interfaces.IBond;
import java.util.stream.Collectors;
import io.github.egonw.analysis.RichStructureHelper;
import java.util.ArrayList;

/**
 * Atom sets that are rich fused rings.
 */

public class RichFusedRing extends RichRing {

    public RichFusedRing(IAtomContainer container, String id) {
        super(container, id, RichSetType.FUSED);
    }

    protected final void walk() {
        this.rim = this.getRim();
    }

    private Set<IAtom> getRim() {
        Set<IAtom> result = new HashSet<>();
        IAtomContainer container = this.getStructure();
        Set<String> sharedBonds = new HashSet<>();
        for (String ring : this.getSubSystems()) {
            RichAtomSet subRing = RichStructureHelper.getRichAtomSet(ring);
            sharedBonds.addAll(subRing.getConnections().stream()
                               .filter(c -> c.getType().equals(ConnectionType.SHAREDBOND))
                               .map(c -> c.getConnector())
                               .collect(Collectors.toSet()));
        }
        for (IBond bond: container.bonds()) {
            if (!sharedBonds.contains(bond.getID())) {
                for (IAtom atom : bond.atoms()) {
                    result.add(atom);
                }
            }
        }
        return result;
    }

    

}
