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
 * @file   RichFunctionalGroup.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich functional groups.
 * 
 * 
 */

//

package io.github.egonw.structure;

import io.github.egonw.analysis.Heuristics;
import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.connection.Connection;
import io.github.egonw.connection.ConnectionType;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * Atom sets that are rich functional groups.
 */

public class RichFunctionalGroup extends RichAtomSet {

  public RichFunctionalGroup(IAtomContainer container, String id) {
    super(container, id, RichSetType.FUNCGROUP);
  }

  protected final void walk() {
    RichAtom start = this.getStartAtom();
    List<RichStructure<?>> atoms = this.getSubSystems().stream()
        .map(RichStructureHelper::getRichStructure)
        .collect(Collectors.toList());
    Collections.sort(atoms, new Heuristics(""));
    WalkDepthFirst dfs;
    if (start == null) {
      dfs = new WalkDepthFirst(atoms);
    } else {
      dfs = new WalkDepthFirst(start, atoms);
    }
    this.componentPositions = dfs.getPositions();
  }

  private RichAtom getStartAtom() {
    SortedSet<Connection> connections = this.getConnections();
    if (connections.isEmpty()) {
      return null;
    }
    Heuristics comparator = new Heuristics("");
    Connection maxConnection = connections.first();
    RichStructure<?> maxConnected = RichStructureHelper
        .getRichStructure(maxConnection.getConnected());
    for (Connection connection : connections) {
      RichStructure<?> connected = RichStructureHelper
          .getRichStructure(connection.getConnected());
      if (comparator.compare(maxConnected, connected) > 0) {
        maxConnected = connected;
        maxConnection = connection;
      }
      ;
    }
    if (maxConnection.getType() == ConnectionType.CONNECTINGBOND) {
      SortedSet<String> atoms = RichStructureHelper.getRichBond(
          maxConnection.getConnector()).getComponents();
      for (String atom : atoms) {
        if (this.getSubSystems().contains(atom)) {
          return RichStructureHelper.getRichAtom(atom);
        }
      }
    }
    return RichStructureHelper.getRichAtom(maxConnection.getConnector());
  }
}
