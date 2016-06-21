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
 * @file   ComponentsPositions.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:17:23 2015
 *
 * @brief  Data structure for positions in rich structures.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;


/**
 * Iterable bijective map for positions and structure names.
 */
public class ComponentsPositions implements Iterable<String> {

  private final BiMap<Integer, String> atomPositions = HashBiMap.create();
  private int atomCount = 0;


  /**
   * Checks if an element is contained in the keys.
   *
   * @param value
   *          The name of the element.
   *
   * @return True if the name is amongst the keys.
   */
  public boolean contains(final String value) {
    return this.atomPositions.containsValue(value);
  }


  /**
   * Checks if a position is contained in the values.
   *
   * @param value
   *          The position number.
   *
   * @return True if the number is amongst the values.
   */
  public boolean contains(final Integer value) {
    return this.atomPositions.containsKey(value);
  }


  /**
   * Retrieves an atom by its position.
   *
   * @param key
   *          The position key.
   *
   * @return The atom at this position.
   */
  public String getAtom(final Integer key) {
    return this.atomPositions.get(key);
  }


  /**
   * Look up the position of an atom.
   *
   * @param atom
   *          The atom to check.
   *
   * @return The position of that atom.
   */
  public Integer getPosition(final String atom) {
    return this.atomPositions.inverse().get(atom);
  }


  /**
   * @return The number of atoms mapped to positions.
   */
  public int size() {
    return this.atomPositions.size();
  }


  /**
   * @return True if the position mapping is empty.
   */
  public boolean isEmpty() {
    return this.atomPositions.isEmpty();
  }


  /**
   * Inserts all the elements from a given component position mapping into this
   * one by appending the new elements.
   *
   * @param componentPositions
   *          The position structure to merge into this one.
   */
  public void putAll(final ComponentsPositions componentPositions) {
    if (this.atomPositions.isEmpty()) {
      this.atomPositions.putAll(componentPositions.atomPositions);
      this.atomCount = componentPositions.size();
      return;
    }
    for (final String atom : componentPositions) {
      if (!this.contains(atom)) {
        this.addNext(atom);
      }
    }
  }


  /**
   * Adds a component at the next available position.
   *
   * @param key
   *          The name of the component.
   */
  public void addNext(final String key) {
    this.atomCount++;
    this.atomPositions.put(this.atomCount, key);
  }


  @Override
  public String toString() {
    String result = "";
    for (final String key : this) {
      result += String.format("%d:\t%s\n", this.getPosition(key), key);
    }
    return result;
  }


  /**
   * Iterator over the components by increasing positions.
   */
  private class AtomIterator implements Iterator<String> {

    private int current;

    /**
     * Iterator constructor.
     */
    AtomIterator() {
      this.current = 0;
    }


    @Override
    public boolean hasNext() {
      return this.current < ComponentsPositions.this.atomPositions.size();
    }


    @Override
    public String next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }
      return ComponentsPositions.this.atomPositions.get(++this.current);
    }

  }


  @Override
  public Iterator<String> iterator() {
    return new AtomIterator();
  }


  @Override
  public void forEach(final Consumer<? super String> action) {
    this.atomPositions.values().forEach(action);
  }

}
