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

	private String structuralFormula = "";
	private BiMap<Integer, String> atomPositions = HashBiMap.create();
	private StructuralAnalysis structuralAnalysis;
	private ArrayList<String> racAtoms = new ArrayList<String>();
	private Cli cli;
	private ArrayList<String> printedAtoms = new ArrayList<String>();

	/**
	 * Computes a structural formula using a Structural Analysis
	 * 
	 * @param saImported
	 *            The StructuralAnalysis to be used
	 * @param cli
	 */
	public void computeAnalysis() {
		List<RichAtomSet> atomSets = structuralAnalysis.getAtomSets();
		// If there is only one atom
		if (atomSets.size() == 0) {
			String currentAtom = structuralAnalysis.getAtoms().get(0).getId();
			printAtom(currentAtom);
		}
		// Stores all atoms contained in a richAtomSet
		for (RichAtomSet richAtomSet : atomSets) {
			for(IAtom atom : richAtomSet.getStructure().atoms()){
				racAtoms.add(atom.getID());
			}
		}
		// Computes the structural formula for each RichAtomSet
		for (RichAtomSet richAtomSet : atomSets) {
			computeRAS(richAtomSet);
		}
		System.out.println(structuralFormula);
	}

	/**
	 * Computes the structural formula for a RichAtomSet
	 * 
	 * @param richAtomSet
	 *            The RichAtomSet to be computed
	 */
	public void computeRAS(RichAtomSet richAtomSet) {
		// Set of atoms in the richAtomSet which connect to a
		// subStructures or superStructures
		Set connectingAtoms = richAtomSet.getConnectingAtoms();
		
		// The atom positions of the current RichAtomSet
		atomPositions = richAtomSet.atomPositions;
		
		// For each atom in the atomPositions
		for (int i = 1; i < atomPositions.size() + 1; i++) {
			// Get data of the current atom
			String currentAtom = atomPositions.get(i);
			RichAtom currentRichAtom = structuralAnalysis.getRichAtom(currentAtom);
			IAtom currentIAtom = currentRichAtom.getStructure();
		
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
	private void addSubSctructure(String currentAtom, RichAtom currentRichAtom,
			Set connectingAtoms) {
		// This is where the subStructure is printed
		structuralFormula += "(";
		// We get every connecting atom to the current atom
		Set<Connection> connections = currentRichAtom.getConnections();
		for (Connection connection : connections) {
			// Assign the connected atom in question
			String currentSubAtom = connection.getConnected();
			// We check if this currentSubAtom is a member of the current
			// RichAtomSet
			if (!connectingAtoms.contains(currentSubAtom)
					&& !atomPositions.containsValue(currentSubAtom)) {
				printAtom(currentSubAtom);
				// This is for dealing with neighbours of the subStructure
				ArrayList<String> connectedToSubAtom = new ArrayList<String>();
				connectedToSubAtom.add(currentAtom);
				// addNeighbours(currentSubAtom, connectedToSubAtom);
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
	private void addNeighbours(String currentSubAtom,
			ArrayList<String> connectedToSubAtom) {
		Set<Connection> connections = structuralAnalysis.getRichAtom(
				currentSubAtom).getConnections();
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
	private void printNeighbours(String currentSubAtom,
			ArrayList<String> connectedToSubAtom, Set<Connection> connections) {
		for (Connection connection : connections) {
			if (!connectedToSubAtom.contains(connection)
					&& !racAtoms.contains(connection.getConnected())) {
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
	private void printAtom(String atomID) {
		if (printedAtoms.contains(atomID)) {
			return;
		} else {
			printedAtoms.add(atomID);
		}
		structuralFormula += structuralAnalysis.getRichAtom(atomID)
				.getStructure().getSymbol();
		int hydrogens = structuralAnalysis.getRichAtom(atomID).getStructure()
				.getImplicitHydrogenCount();
		if (hydrogens > 0) {
			structuralFormula += "H";
			if (cli.cl.hasOption("sub")) {
				structuralFormula += getSubScript(hydrogens);
			} else {
				structuralFormula += hydrogens;
			}
		}
	}

	public String getStructuralFormula(StructuralAnalysis structuralAnalysis, Cli cli) {
		this.cli = cli;
		this.structuralAnalysis = structuralAnalysis;
		this.computeAnalysis();
		return this.structuralFormula;
	}

	/**
	 * Gets the subscript for the inserted number
	 * 
	 * @param number
	 *            The number to be translated
	 * @return Returns the subscript of the inserted number
	 */
	private String getSubScript(int number) {
		return Character.toString((char) (0x2080 + number));
	}
}
