package io.github.egonw;

import io.github.egonw.RichAtomSet.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
	public static BiMap<Integer, String> atomPositionsNew = HashBiMap.create();
	public static ArrayList<IAtom> frontier = new ArrayList<IAtom>();
	public static IAtomContainer molecule;
	public static RichAtomSet rac;

	public static StructuralAnalysis sa;

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

	public static void computeRAC(RichAtomSet rac) {
		structuralFormula ="";

		String currentAtom = null;
		IAtom currentIAtom = null;
		RichAtom currentRichAtom = null;

		Set connectingAtoms = rac.getConnectingAtoms();

		System.out.println("");
		System.out.println(rac.getConnections());
		System.out.println("");
		System.out.println(rac.getConnectingAtoms());
		System.out.println("");
		System.out.println(rac.getExternalBonds());
		System.out.println("");
		System.out.println(rac.atomPositions);

		atomPositionsNew = rac.atomPositions;
		System.out.println(rac.atomPositions == atomPositionsNew);
		System.out.println(atomPositionsNew.toString());
		System.out.println(atomPositionsNew.get(1));

		for (int i = 1; i < atomPositionsNew.size()+1; i++) {
			currentAtom = atomPositionsNew.get(i);
			currentIAtom = sa.getRichAtom(currentAtom).getStructure();
			currentRichAtom = sa.getRichAtom(currentAtom);

			if (!connectingAtoms.contains(currentAtom)) {
				structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();

				int hydrogens = sa.getRichAtom(currentAtom).getStructure().getImplicitHydrogenCount();

				if (hydrogens > 0) {
					structuralFormula += "H";
					structuralFormula += getSubScript(hydrogens);
				}
			} else {
				structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();
				int hydrogens = sa.getRichAtom(currentAtom).getStructure().getImplicitHydrogenCount();

				if (hydrogens > 0) {
					structuralFormula += "H";
					structuralFormula += getSubScript(hydrogens);
				}
				structuralFormula += "(";
				
				System.out.println("(");
				
				Set<Connection> connections = currentRichAtom.getConnections();
				System.out.println("Connections are: " + connections);
			
//				if(is an atom set){
//					computeRAC(atomset)
//				}
				
				System.out.println(currentRichAtom);
				System.out.println(currentRichAtom.getConnections());
				System.out.println(currentRichAtom.getExternalBonds());
				
				
				
				//for each individual atom
				
				for (Connection connection : connections) {
				
					if(!connectingAtoms.contains(connection.getConnected()) && !atomPositionsNew.containsValue(connection.getConnected())){
						structuralFormula += sa.getRichAtom(connection.getConnected()).getStructure().getSymbol();
						System.out.println("adding " + sa.getRichAtom(connection.getConnected()).getStructure().getSymbol() + " " + currentAtom);
						System.out.println(connectingAtoms);
						
						hydrogens = sa.getRichAtom(connection.getConnected()).getStructure().getImplicitHydrogenCount();

						if (hydrogens > 0) {
							structuralFormula += "H";
							structuralFormula += getSubScript(hydrogens);
						}
					}
					
//					if(atomPositions.containsValue(connection.getConnected())){
//						structuralFormula += sa.getRichAtom(connection.getConnected()).getStructure().getSymbol();
//					}
				}
				
				
				
				System.out.println(")");
				
				structuralFormula += ")";
				
			}
		}
		System.out.println("");
		System.out.println(structuralFormula);
	}

	public static void computeAnalysis(StructuralAnalysis sa2) {
		sa = sa2;
		System.out.println("");
		System.out.println(sa.getAtomSets());

		System.out.println("");

		System.out.println(sa.isAtomSet("as1"));

		List<RichAtomSet> atomSets = sa.getAtomSets();
		
		if(atomSets.size() == 0){
			return;
		}

		RichAtomSet richAtomSet = sa.getAtomSets().get(0);

		computeRAC(richAtomSet);

	}

	public static void findAtomPositions() {

		// IAtom startAtom = findStartAtomHeaviest();
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
				return;
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
