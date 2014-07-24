package io.github.egonw;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Class which takes a RichAtomSet or an IAtomContainer and returns a string
 * with the structural formula.
 * 
 * @author Joshie
 */

public class StructuralFormula {

	public static String structuralFormula = "";
	public static BiMap<Integer, String> atomPositions = HashBiMap.create();
	private static RichAtomSet rac;
	private static StructuralAnalysis sa;
	private static ArrayList<String> racAtoms = new ArrayList<String>();
	private static Cli cli;
	public static ArrayList<String> printedAtoms = new ArrayList<String>();

	/**
	 * Computes a structural formula using a Structural Analysis
	 * 
	 * @param saImported
	 *            The StructuralAnalysis to be used
	 * @param cli 
	 */
	public static void computeAnalysis(StructuralAnalysis saImported, Cli cliImp) {
		cli = cliImp;
		System.out.println("");
		sa = saImported;
		List<RichAtomSet> atomSets = sa.getAtomSets();
		// If there is only one atom
		if (atomSets.size() == 0) {
			String currentAtom = sa.getAtoms().get(0).getId();
			printAtom(currentAtom);
		}
		// Stores all atoms contained in a richAtomSet
		for (RichAtomSet richAtomSet : atomSets) {
			for (int i = 0; i < richAtomSet.getStructure().getAtomCount(); i++) {
				racAtoms.add(richAtomSet.getStructure().getAtom(i).getID());
			}
		}
		// Computes the structural formula for each RichAtomSet
		for (RichAtomSet richAtomSet : atomSets) {
			computeRAC(richAtomSet);
		}
		System.out.println(structuralFormula);
	}

	/**
	 * Computes the structural formula for a RichAtomSet
	 * 
	 * @param rac
	 *            The RichAtomSet to be computed
	 */
	public static void computeRAC(RichAtomSet rac) {
		String currentAtom = null;
		IAtom currentIAtom = null;
		RichAtom currentRichAtom = null;
		// Set of atoms in the rac which connect to subStructures or
		// superStructures
		Set connectingAtoms = rac.getConnectingAtoms();
		// The atom positions of the current RichAtomSet
		atomPositions = rac.atomPositions;

		// For each atom in the atomPositions
		for (int i = 1; i < atomPositions.size() + 1; i++) {
			// Get data of the current atom
			currentAtom = atomPositions.get(i);
			currentIAtom = sa.getRichAtom(currentAtom).getStructure();
			currentRichAtom = sa.getRichAtom(currentAtom);
			// Check if the current atom is connected to a subStructure
			// If not then simply "print" the atom
			if (!connectingAtoms.contains(currentAtom)) {
				printAtom(currentAtom);
			} else {
				// If the atom does have a connecting atom then we print
				// the atom and we also print its connecting atoms
				printAtom(currentAtom);
				addSubSctructure(currentAtom, currentRichAtom, connectingAtoms);
			}
		}
	}

	/**
	 * Adds a substructure to the structuralFormula to be printed
	 * 
	 * @param currentAtom
	 * @param currentRichAtom
	 * @param connectingAtoms
	 */
	private static void addSubSctructure(String currentAtom, RichAtom currentRichAtom, Set connectingAtoms) {
		// This is where the subStructure is printed
		structuralFormula += "(";
		// We get every connecting atom to the current atom
		Set<Connection> connections = currentRichAtom.getConnections();
		for (Connection connection : connections) {
			// Assign the connected atom in question
			String currentSubAtom = connection.getConnected();
			// We check if this currentSubAtom is a member of the current
			// RichAtomSet
			if (!connectingAtoms.contains(currentSubAtom) && !atomPositions.containsValue(currentSubAtom)) {
				printAtom(currentSubAtom);
				// This is for dealing with neighbours of the subStructure
				ArrayList<String> connectedToSubAtom = new ArrayList<String>();
				connectedToSubAtom.add(currentAtom);
				//addNeighbours(currentSubAtom, connectedToSubAtom);
			}
		}
		structuralFormula += ")";
	}

	/**
	 * Method for dealing with isolated atoms attached to substructures
	 * 
	 * @param currentSubAtom
	 * @param connectedToSubAtom
	 */
	private static void addNeighbours(String currentSubAtom, ArrayList<String> connectedToSubAtom) {
		Set<Connection> connections = sa.getRichAtom(currentSubAtom).getConnections();
		System.out.println(connections);
		if (connections.size() > 1) {
			printNeighbours(currentSubAtom, connectedToSubAtom, connections);
		}
	}

	/**
	 * Once neighbouring atoms have been found this method prints them and their
	 * neighbours also
	 * 
	 * @param currentSubAtom
	 * @param connectedToSubAtom
	 * @param connections
	 */
	private static void printNeighbours(String currentSubAtom, ArrayList<String> connectedToSubAtom,
			Set<Connection> connections) {
		for (Connection connection : connections) {
			if (!connectedToSubAtom.contains(connection) && !racAtoms.contains(connection.getConnected())) {
				connectedToSubAtom.add(connection.getConnected());
				System.out.println(connection.getConnected());
				printAtom(connection.getConnected());
				addNeighbours(connection.getConnected(), connectedToSubAtom);
			}
		}
	}

	/**
	 * Adds the atom and its Hydrogens to the structuralFormula
	 * 
	 * @param atomID
	 */
	private static void printAtom(String atomID) {
		if(printedAtoms.contains(atomID)){
			return;
		} else {
			printedAtoms.add(atomID);
		}
		structuralFormula += sa.getRichAtom(atomID).getStructure().getSymbol();
		int hydrogens = sa.getRichAtom(atomID).getStructure().getImplicitHydrogenCount();
		if (hydrogens > 0) {
			structuralFormula += "H";
			if(cli.cl.hasOption("sub")){
				structuralFormula += getSubScript(hydrogens);
			} else {
				structuralFormula += hydrogens;
			}
		}
	}

	/**
	 * Gets the subscript for the inserted number
	 * 
	 * @param number
	 *            The number to be translated
	 * @return Returns the subscript of the inserted number
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
		return "Error: Wrong number in getSubScript";
	}
}
