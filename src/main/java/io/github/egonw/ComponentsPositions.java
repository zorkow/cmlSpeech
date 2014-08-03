package io.github.egonw;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;

public class ComponentsPositions implements Iterable<String> {

    private BiMap<Integer, String> atomPositions = HashBiMap.create();
    private int atomCount = 0;


    public void put(Integer position, String atomID) {
        this.atomPositions.put(position, atomID);
    }

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
        this.atomPositions.putAll(componentPositions.atomPositions);
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
