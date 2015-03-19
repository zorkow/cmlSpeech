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
 * @file   AbstractRichStructure.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Jun 10 22:25:05 2014
 * 
 * @brief  Abstract class for rich structures.
 * 
 * 
 */

//
package io.github.egonw.structure;

import io.github.egonw.base.CMLNameComparator;
import io.github.egonw.connection.Connection;
import io.github.egonw.connection.ConnectionComparator;

import com.google.common.base.Joiner;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implements basic functionality for Rich Structures.
 */

public abstract class AbstractRichStructure<S> implements RichStructure<S> {
    
    protected final S structure;

    AbstractRichStructure(S structure) {
        this.structure = structure;
    }

    @Override 
    public S getStructure() {
        return this.structure;
    }

    private SortedSet<String> components = new TreeSet<String>(new CMLNameComparator());

    @Override
    public SortedSet<String> getComponents() {
        return this.components;
    };


    private SortedSet<String> contexts = new TreeSet<String>(new CMLNameComparator());

    @Override
    public SortedSet<String> getContexts() {
        return this.contexts;
    };


    private SortedSet<String> externalBonds = new TreeSet<String>(new CMLNameComparator());

    @Override
    public SortedSet<String> getExternalBonds() {
        return this.externalBonds;
    };


    private SortedSet<Connection> connections = new TreeSet<Connection>(new ConnectionComparator());

    @Override
    public SortedSet<Connection> getConnections() {
        return this.connections;
    };


    private SortedSet<String> superSystems = new TreeSet<String>(new CMLNameComparator());

    @Override
    public SortedSet<String> getSuperSystems() {
        return this.superSystems;
    };


    private SortedSet<String> subSystems = new TreeSet<String>(new CMLNameComparator());

    @Override
    public SortedSet<String> getSubSystems() {
        return this.subSystems;
    }


    @Override
    public String toString() {
        Joiner joiner = Joiner.on(" ");
        return this.getId() + ":" +
            "\nComponents:" + joiner.join(this.getComponents()) +
            "\nContexts:" + joiner.join(this.getContexts()) +
            "\nExternal Bonds:" + joiner.join(this.getExternalBonds()) +
            "\nConnections:" + joiner.join(this.getConnections().stream()
                                           .map(Connection::toString)
                                           .collect(Collectors.toList()));
    }

}
