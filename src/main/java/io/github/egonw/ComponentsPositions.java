package io.github.egonw;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ComponentsPositions {

    public BiMap<Integer, String> atomPositions = HashBiMap.create();
    public BiMap<Integer, RichAtomSet> atomSets = HashBiMap.create();
    public int atomCount = 0;
    public int setCount = 0;

    public BiMap<Integer, RichAtomSet> getAtomSets() {
        return this.atomSets;
    }
    
    public void addAtomSet(RichAtomSet richAtomSet){
        atomSets.put(setCount, richAtomSet);
        setCount++;
    }

    public void put(Integer position, String atomID) {
        this.atomPositions.put(position, atomID);
    }

    public boolean containsValue(String value) {
        return this.atomPositions.containsValue(value);
    }

    public Set<Integer> keySet() {
        return this.atomPositions.keySet();
    }
    
    public Set<Integer> getAtomPositions(){
        return this.atomPositions.keySet();
    }

    public String get(Integer key) {
        return this.atomPositions.get(key);
    }
    
    public String getAtom(Integer key){
        return this.atomPositions.get(key);
    }

    public BiMap<String, Integer> inverse() {
        return this.atomPositions.inverse();
    }

    public void inverseAtomPositions() {
        this.atomPositions.inverse();
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
        this.atomPositions.put(atomCount, atomID);
        atomCount = atomCount++;
    }

    public void printPositions(Integer offset) {
        // This is incorrect for substructures!
        System.out.println("Local\tGlobal");
        for (Integer key : this.atomPositions.keySet()) {
            System.out.printf("%d\t%d:\t%s\n", key, key + offset, this.atomPositions.get(key));
        }
    }

    public class AtomIterator implements Iterator<String> {

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

    public String getPositionAtom(Integer position) {
        return this.get(position);
    }

    public Integer getAtomPosition(String atom) {
        return this.atomPositions.inverse().get(atom);
    }

}
