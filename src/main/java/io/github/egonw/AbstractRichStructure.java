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
package io.github.egonw;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import com.google.common.base.Joiner;
import java.util.stream.Collectors;

/**
 * Implements basic functionality for Rich Structures.
 */

public abstract class AbstractRichStructure<S> implements RichStructure<S> {
    
    protected S structure;

    AbstractRichStructure(S structure) {
        this.structure = structure;
    }

    @Override 
    public S getStructure() {
        return this.structure;
    }

    private SortedSet<String> components = new TreeSet<String>(new CMLNameComparator());

    @Override
    public Set<String> getComponents() {
        return this.components;
    };


    private SortedSet<String> contexts = new TreeSet<String>(new CMLNameComparator());

    @Override
    public Set<String> getContexts() {
        return this.contexts;
    };


    private SortedSet<String> externalBonds = new TreeSet<String>(new CMLNameComparator());

    @Override
    public Set<String> getExternalBonds() {
        return externalBonds;
    };


    private SortedSet<Connection> connections = new TreeSet<Connection>(new Connection());

    @Override
    public Set<Connection> getConnections() {
        return connections;
    };


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
