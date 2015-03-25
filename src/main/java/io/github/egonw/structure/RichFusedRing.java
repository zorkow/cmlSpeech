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

public class RichFusedRing extends RichRing implements RichSuperSet {

    public RichFusedRing(IAtomContainer container, String id) {
        super(container, id, RichSetType.FUSED);
    }

    private Set<String> sharedBonds = new HashSet<>();
    
    protected final void walk() {
        this.computeRichSubSystems();
        this.computeSharedBonds();
        this.computeRim();
        super.walk();
        richSubSystems.stream().forEach(s -> s.walk());
        this.setPath();
    }


    private void computeSharedBonds() {
        for (RichAtomSet subRing : this.richSubSystems) {
            this.sharedBonds.addAll(subRing.getConnections().stream()
                                    .filter(c -> c.getType().equals(ConnectionType.SHAREDBOND))
                                    .map(c -> c.getConnector())
                                    .collect(Collectors.toSet()));
        }
    }
    
    
    private void computeRim() {
        IAtomContainer container = this.getStructure();
        this.rim = new HashSet<>();
        for (IBond bond: container.bonds()) {
            if (!sharedBonds.contains(bond.getID())) {
                for (IAtom atom : bond.atoms()) {
                    this.rim.add(atom);
                }
            }
        }
    }


    @Override
    protected List<IAtom> getConnectedAtomsList(IAtom atom) {
        List<IBond> rimBonds = this.getStructure().getConnectedBondsList(atom)
            .stream()
            .filter(b -> !this.sharedBonds.contains(b.getID()))
            .collect(Collectors.toList());
        List<IAtom> rimAtoms = new ArrayList<>();
        for (IBond bond : rimBonds) {
            for (IAtom batom : bond.atoms()) {
                if (atom != batom) {
                    rimAtoms.add(batom);
                }
            }
        }
        return rimAtoms;
    }


    private ComponentsPositions path = new ComponentsPositions();
    private Set<RichAtomSet> richSubSystems = null;


    private void computeRichSubSystems() {
        richSubSystems = this.getSubSystems().stream()
            .map(s -> RichStructureHelper.getRichAtomSet(s))
            .collect(Collectors.toSet());
    }
    
    @Override
    public ComponentsPositions getPath() {
        return path;
    }

    private RichAtomSet findAtom(List<RichAtomSet> sets, RichAtom atom) {
        for (RichAtomSet set : sets) {
            if (set.getStructure().contains(atom.getStructure())) {
                return set;
            }
        }
        return null;
    }

    @Override
    public void setPath() {
        List<RichAtomSet> newSystem = new ArrayList<>(richSubSystems);
        System.out.println(newSystem);
        RichAtomSet lastSystem = null;
        for (String atomName : this) {
            RichAtom atom = RichStructureHelper.getRichAtom(atomName);
            RichAtomSet container = this.findAtom(newSystem, atom);
            if (container != null) {
                lastSystem = container;
                newSystem.remove(container);
                this.path.addNext(container.getId());
            }
        }
        while (newSystem.size() > 0) {
            for (String atomName : lastSystem) {
                RichAtom atom = RichStructureHelper.getRichAtom(atomName);
                RichAtomSet container = this.findAtom(newSystem, atom);
                if (container != null) {
                    System.out.println(container.getId());
                    lastSystem = container;
                    newSystem.remove(container);
                    this.path.addNext(container.getId());
                    continue;
                }
            }
        }
        System.out.println(this.path);
    }
    
}
