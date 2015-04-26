// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   StructureVisitor.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">Volker
 *         Sorge</a>
 * @date   Sat Apr 25 23:36:58 2015
 * 
 * @brief  Visitor to construct the exploration structure.
 * 
 * 
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.google.common.collect.TreeMultimap;
import java.util.Comparator;
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import java.util.Set;
import com.progressiveaccess.cmlspeech.connection.Connection;
import java.util.HashSet;
import java.util.stream.Collectors;
import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;
import java.util.List;

/**
 * Constructs the exploration structure.
 */

public class StructureVisitor implements XmlVisitor {

  //private final SreAnnotations annotations = new SreAnnotations();

  private TreeMultimap<String, SreElement> annotations = TreeMultimap.create(new CmlNameComparator(), new SreComparator());
  private SreElement element;
  private RichAtomSet context;
  

  private class SreComparator implements Comparator<SreElement> {

  @Override
  public int compare(final SreElement element1, final SreElement element2) {
    return 1;
  }}

  
  /** 
   * @return The annotation the visitor computes.
   */
  public SreElement getAnnotations() {
    SreElement element = new SreElement(SreNamespace.Tag.ANNOTATIONS);
    for (final String key : this.annotations.keySet()) {
      for (final SreElement value : this.annotations.get(key)) {
        element.appendChild(value);
      }
    }
    return element;
  }


  @Override
  public void visit(final RichAtom atom) {
    for (String parent : atom.getSuperSystems()) {
      this.element = new SreElement(SreNamespace.Tag.ANNOTATION);
      annotations.put(atom.getId(), this.element);
      this.context =  RichStructureHelper.getRichAtomSet(parent);
      // this.annotations.registerAnnotation(atom.getId(),
      //                                     this.element);
      this.atomStructure(atom);
    }
  }


  /**
   * Computes structure for an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   */
  private void atomStructure(final RichAtom atom) {
    String id = atom.getId();
    ComponentsPositions positions = this.context.getComponentsPositions();
    Integer position = positions.getPosition(id);
    Set<Connection> internalConnections = this.connectionsInContext(atom);
    this.element.appendChild(new SreElement(atom.tag(), id));
    this.element.appendChild(new SreElement(SreNamespace.Tag.PARENTS,
        this.context.getId()));
    this.element.appendChild(SreUtil.sreSet(SreNamespace.Tag.COMPONENT,
        internalConnections.stream().map(conn -> conn.getConnector())
                           .collect(Collectors.toList())));
    this.element.appendChild(new SreElement(SreNamespace.Tag.POSITION, 
                                            position.toString()));
    this.element.appendChild(new SreElement(SreNamespace.Tag.CHILDREN));
    SreElement connElement = new SreElement(SreNamespace.Tag.NEIGHBOURS);
    if (position > 1) {
      Integer decr = position - 1;
      SreElement neighbourElement = new SreElement(SreNamespace.Tag.NEIGHBOUR);
      this.element.appendChild(neighbourElement);
      String neighbour = positions.get(decr);
      neighbourElement.appendChild(new SreElement(SreNamespace.Tag.ATOM, neighbour));
      neighbourElement.appendChild(new SreElement(SreNamespace.Tag.POSITION, 
                                              decr.toString()));
      SreElement viaElement = new SreElement(SreNamespace.Tag.VIA);
      neighbourElement.appendChild(viaElement);
      internalConnections.stream().
        filter(c -> c.getConnected() == neighbour).
        forEach(c -> viaElement.appendChild(SreUtil.sreElement(c.getConnector())));
    }

    if (position < positions.size()) {
      Integer incr = position + 1;
      SreElement neighbourElement = new SreElement(SreNamespace.Tag.NEIGHBOUR);
      this.element.appendChild(neighbourElement);
      String neighbour = positions.get(incr);
      neighbourElement.appendChild(new SreElement(SreNamespace.Tag.ATOM, neighbour));
      neighbourElement.appendChild(new SreElement(SreNamespace.Tag.POSITION, 
                                              incr.toString()));
      SreElement viaElement = new SreElement(SreNamespace.Tag.VIA);
      neighbourElement.appendChild(viaElement);
      internalConnections.stream().
        filter(c -> c.getConnected() == neighbour).
        forEach(c -> viaElement.appendChild(SreUtil.sreElement(c.getConnector())));

    } 
  }
  
  //   ComponentsPositions positions = this.context.getComponentsPositions();
  //   Integer current = positions.getPosition(id);
  //   String next = this.nextElement(id);
  //   String previous = this.previousElement(id);
  //   this.connectionsStructure(internalConnections);
  // }


  private void connectionsStructure(Set<Connection> connections) {
    SreElement connElement = new SreElement(SreNamespace.Tag.CONNECTIONS);
    
    
  };
  

  /**
   * Computes connections of an atom in the context of a set.
   *
   * @param atom
   *          The rich atom.
   * 
   * @return The connections of the atom that belong to the set.
   */
  private Set<Connection> connectionsInContext(final RichAtom atom) {
    if (!this.context.getConnectingAtoms().contains(atom.getId())) {
      return atom.getConnections();
    }
    Set<Connection> internal = new HashSet<>();
    for (Connection connection : atom.getConnections()) {
      if (this.context.getInternalBonds().contains(connection.getConnector())) {
        internal.add(connection);
      }
    }
    return internal;
  }

  
  public void complete() {
  }


  private String nextElement (String id) {
    ComponentsPositions positions = this.context.getComponentsPositions();
    Integer current = positions.getPosition(id);
    return (current >= positions.size()) ? null : positions.get(current + 1);
  }
  
  private String previousElement (String id) {
    ComponentsPositions positions = this.context.getComponentsPositions();
    Integer current = positions.getPosition(id);
    return (current <= 1) ? null : positions.get(current - 1);
  }
  
}
