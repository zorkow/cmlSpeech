package io.github.egonw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * Class which takes a RichAtomSet or an IAtomContainer and returns a string
 * with the structural formula.
 * 
 * @author Joshie
 *
 */

public class StructuralFormula {

	/**
	 * The string to be returned with the structural formula
	 */
	private static String structuralFormula = "";
	
	public static BiMap<Integer, String> atomPositions = HashBiMap.create();
	public static ArrayList<IAtom> atomConnections = new ArrayList<IAtom>();
	public static Set<IAtom> setConnections = new HashSet<IAtom>();

	/**Computes and prints the structural formula of the given molecule to a
	 * string
	 * @param molecule
	 *            The IAtomContainer to be translated
	 */
	public static void compute(IAtomContainer molecule) {

		// Iterator to go over the atoms in the molecule
		Iterable<IAtom> it = molecule.atoms();
		
		System.out.println("");
		
		System.out.println("The number of atoms is: " + molecule.getAtomCount());

//			For each atom in the iterator
//			for (IAtom a : it) {
//
//			// Gets the symbol of the current atom
//			structuralFormula += a.getSymbol();
//
//			// Gets the hydrogen count of the current atom
//			int h = a.getImplicitHydrogenCount();
//
//			// If h > 0 then adds H to the formula and works out the subscript
//			// of the number of hydrogens
//			if (h > 0) {
//				structuralFormula += "H";
//
//				String subScript = getSubScript(h);
//
//				structuralFormula += subScript;
//			}
//			
//		}
		
		if(molecule.getAtomCount() == 1){
			computeAtomPositionsIsolated(molecule);
		}
		else{
			computeAtomPositionsAliphatic(molecule);
		}
		
		System.out.println(atomPositions);
		
		for (IAtom atom : atomConnections) {
			
			structuralFormula += atom.getSymbol();
			
			int h = atom.getImplicitHydrogenCount();

			if (h > 0) {
				
				structuralFormula += "H";
				String subScript = getSubScript(h);
				structuralFormula += subScript;
			}
		}

		// Prints the structural formula
		System.out.println("");

		System.out.println("The structural formula is:");

		System.out.println(structuralFormula);

		System.out.println("");
		

	}
	
	public static void workOutLocations(IAtomContainer molecule){
		
		Iterable<IAtom> it = molecule.atoms();
		
		for (IAtom a : it) {
			
			System.out.println("");
			
			System.out.println("This atom is: " + a.getID() + " " + a.getSymbol());
			
			for (IAtom iAtom : molecule.getConnectedAtomsList(a)) {
				System.out.println(iAtom);
			}
		}

		System.out.println("");
		
	}
	
	public static void workOutBonds(IAtomContainer molecule){
		
		Iterable<IBond> it = molecule.bonds();
		
		System.out.println("The number of bonds is: " + molecule.getBondCount());
		
		for (IBond iBond : it) {
			
			System.out.println("");
			System.out.println(iBond);
			System.out.println("The number of atoms in this bond is: " + iBond.getAtomCount());
		}
		
	}
	
	
	private static void walkRing(IAtom atom, Integer count, List<IAtom> visited, IAtomContainer molecule) {
        if (visited.contains(atom)) {
            return;
        }
        atomPositions.put(count, atom.getID());
        atomConnections.add(atom);
        visited.add(atom);
        for (IAtom connected : molecule.getConnectedAtomsList(atom)) {
            if (!visited.contains(connected)) {
                walkRing(connected, ++count, visited, molecule);
                return;
            }
        }
    }
	
	private static void computeAtomPositionsAliphatic(IAtomContainer molecule) {
        IAtom startAtom = null;
        for (IAtom atom : molecule.atoms()) {
            if (molecule.getConnectedAtomsList(atom).size() == 1) {
                startAtom = atom;
                if (atomConnections.contains(startAtom)) {
                	System.out.println("RETURNING");
                    return;
                }
            }
        }
        if (startAtom == null) {
            throw new SreException("Aliphatic chain without start atom!");
        }
        System.out.println("Start atom is");
        System.out.println(startAtom);
        walkRing(startAtom, 1, new ArrayList<IAtom>(), molecule);
    }
	
	private static void computeAtomPositionsIsolated(IAtomContainer molecule) {
        IAtom startAtom;
        if (atomConnections.size() == 0 && setConnections.size() == 0) {
            List<IAtom> atoms = Lists.newArrayList(molecule.atoms());
            startAtom = atoms.get(0);
        } else if (atomConnections.size() == 0) {
            startAtom = setConnections.iterator().next();
        } else {
            startAtom = atomConnections.iterator().next();
        }
        walkRing(startAtom, 1, new ArrayList<IAtom>(), molecule);
    }

	/** Returns the subscript of a given number
	 * @param number
	 *            The input number to be converted
	 * @return
	 */
	public static String getSubScript(int number) {

		switch (number) {
		case 0:
			return "\u2080";
		case 1:
			return "\u2081";
		case 2:
			return "\u2082";
		case 3:
			return "\u2083";
		case 4:
			return "\u2084";
		case 5:
			return "\u2085";
		case 6:
			return "\u2086";
		case 7:
			return "\u2087";
		case 8:
			return "\u2088";
		case 9:
			return "\u2089";
		}

		return "test";

	}

}
