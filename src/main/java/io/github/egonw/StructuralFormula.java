package io.github.egonw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Class which takes a RichAtomSet or an IAtomContainer and returns a string
 * with the structural formula.
 * 
 * @author Joshie
 */

public class StructuralFormula {

	private static String structuralFormula = "";

	public static BiMap<Integer, IAtom> atomPositions = HashBiMap.create();
	public static ArrayList<IAtom> frontier = new ArrayList<IAtom>();
	public static IAtomContainer molecule;

	public static void compute(IAtomContainer moleculeImported) {

		System.out.println("");

		molecule = moleculeImported;

		for (IAtom atom : molecule.atoms()) {
			frontier.add(atom);
		}

		findAtomPositions();

		computeStructuralFormula();

		System.out.println(structuralFormula);

	}

	public static void findAtomPositions() {

		//IAtom startAtom = findStartAtomHeaviest();
		IAtom startAtom = findStartAtomShortest();

		walkRing(startAtom, 0, new ArrayList<IAtom>());

	}

	public static IAtom findStartAtomHeaviest() {

		// Choose the heaviest atom
		IAtom heaviest = frontier.get(0);

		for (IAtom atom : molecule.atoms()) {
			if (atom.getExactMass() > heaviest.getExactMass()) {
				heaviest = atom;
			}
		}

		return heaviest;
	}

	public static IAtom findStartAtomShortest() {

		IAtom shortest = null;

		for (IAtom atom : molecule.atoms()) {
			if (frontier.size() == 1) {
				shortest = atom;
			}
			if (molecule.getConnectedAtomsList(atom).size() == 1) {
				shortest = atom;
			}
		}

		return shortest;
	}

	private static void walkRing(IAtom atom, Integer count, List<IAtom> visited) {
		
		if (visited.contains(atom)) {
			return;
		}
		
		atomPositions.put(count, atom);
		visited.add(atom);
		
		for (IAtom connected : molecule.getConnectedAtomsList(atom)) {
			if (!visited.contains(connected)) {
				walkRing(connected, ++count, visited);
			}
		}
	}

	public static void computeStructuralFormula() {

		for (int i = 0; i < atomPositions.size(); i++) {

			structuralFormula += atomPositions.get(i).getSymbol();

			int hydrogens = atomPositions.get(i).getImplicitHydrogenCount();

			if (hydrogens > 0) {
				structuralFormula += "H" + getSubScript(hydrogens);
			}
		}
	}

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
