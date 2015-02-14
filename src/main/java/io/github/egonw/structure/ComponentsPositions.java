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
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:17:23 2015
 * 
 * @brief  Data structure for positions in rich structures.
 * 
 * 
 */

//
package io.github.egonw.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Iterable bijective map for positions and structure names.
 */

public class ComponentsPositions implements Iterable<String> {

    private BiMap<Integer, String> atomPositions = HashBiMap.create();
    private int atomCount = 0;


    public boolean contains(String value) {
        return this.atomPositions.containsValue(value);
    }

    public boolean contains(Integer value) {
        return this.atomPositions.containsKey(value);
    }

    public String get(Integer key) {
        return this.atomPositions.get(key);
    }

    public String getAtom(Integer key) {
        return this.atomPositions.get(key);
    }

    public Integer getPosition(String atom) {
        return this.atomPositions.inverse().get(atom);
    }

    public int size() {
        return this.atomPositions.size();
    }

    public boolean isEmpty() {
        return this.atomPositions.isEmpty();
    }

    public void putAll(ComponentsPositions componentPositions) {
        if (this.atomPositions.isEmpty()) {
            this.atomPositions.putAll(componentPositions.atomPositions);
            this.atomCount = componentPositions.size();
            return;
        }
        for (String atom : componentPositions) {
            if (!this.contains(atom)) { 
                this.addNext(atom);
            }
        }
    }

    public void addNext(String atomID) {
        this.atomCount++;
        this.atomPositions.put(this.atomCount, atomID);
    }

    public String toString() {
        String result = "";
        for (Integer key : this.atomPositions.keySet()) {
            result += String.format("%d:\t%s\n", key, this.atomPositions.get(key));
        }
        return result;
    }

    private class AtomIterator implements Iterator<String> {

        private int current;

        AtomIterator() {
            this.current = 0;
        }

        @Override
        public boolean hasNext() {
            return this.current < atomPositions.size();
        }

        @Override
        public String next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return atomPositions.get(++this.current);
        }

    }

    public Iterator<String> iterator() {
        return new AtomIterator();
    }

    public void forEach(Consumer<? super String> action) {
        this.atomPositions.values().forEach(action);
    };
        

}
