package io.github.egonw;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ComponentsPositions {

	public BiMap<Integer, String> atomPositions = HashBiMap.create();

	public void put(Integer position, String atomID) {
		atomPositions.put(position, atomID);
	}

	public boolean containsValue(String value) {
		return atomPositions.containsValue(value);
	}

	public Set keySet() {
		return atomPositions.keySet();
	}
	
	public String get(Integer key){
		return atomPositions.get(key);
	}
	
	public BiMap<String, Integer> inverse(){
		return atomPositions.inverse();
	}
	
	public void inverseAtomPositions(){
		atomPositions.inverse();
	}
	
	public int size(){
		return atomPositions.size();
	}
	
	public boolean isEmpty(){
		return atomPositions.isEmpty();
	}
	
	public void putAll(BiMap map){
		atomPositions.putAll(map);
	}

}
