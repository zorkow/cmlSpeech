package io.github.egonw;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Class which takes a RichAtomSet or an IAtomContainer and returns a string with the structural formula.
 * 
 * @author Joshie
 */

public class StructuralFormula {

	private String structuralFormula = "";
	private BiMap<Integer, String> atomPositions = HashBiMap.create();
	private StructuralAnalysis structuralAnalysis;
	private ArrayList<String> richAtomSetAtoms = new ArrayList<String>();
	private boolean useSubScripts;
	private ArrayList<String> appendedAtoms = new ArrayList<String>();

	/**
	 * Computes a structural formula using a Structural Analysis
	 * 
	 * @param saImported
	 *            The StructuralAnalysis to be used
	 * @param cli
	 */
	public void computeAnalysis() {
		List<RichAtomSet> atomSets = this.structuralAnalysis.getAtomSets();
		System.out.println(atomSets);
		// If there is only one atom
		if (atomSets.size() == 0) {
			for (RichAtom atom : this.structuralAnalysis.getAtoms()) {
				appendAtom(atom.getId());
			}
		}
		// Stores all atoms contained in a richAtomSet
		for (RichAtomSet richAtomSet : atomSets) {
			for (IAtom atom : richAtomSet.getStructure().atoms()) {
				this.richAtomSetAtoms.add(atom.getID());
			}
		}
		System.out.println(richAtomSetAtoms);
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
		this.atomPositions = richAtomSet.atomPositions;

		// For each atom in the atomPositions
		for (int i = 1; i < this.atomPositions.size() + 1; i++) {
			// Get data of the current atom
			String currentAtom = this.atomPositions.get(i);
			RichAtom currentRichAtom = this.structuralAnalysis.getRichAtom(currentAtom);
			// Check if the current atom is connected to a subStructure
			// If not then simply "print" the atom
			if (!connectingAtoms.contains(currentAtom)) {
				appendAtom(currentAtom);
			} else {
				// If the atom does have a connecting atom then we print
				// the atom and we also print its connecting atoms
				appendAtom(currentAtom);
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
	private void addSubSctructure(String currentAtom, RichAtom currentRichAtom, Set connectingAtoms) {
		// This is where the subStructure is printed
		this.structuralFormula += "(";
		// We get every connecting atom to the current atom
		Set<Connection> connections = currentRichAtom.getConnections();
		for (Connection connection : connections) {
			// Assign the connected atom in question
			String currentSubAtom = connection.getConnected();
			// We check if this currentSubAtom is a member of the current RichAtomSet
			if (!connectingAtoms.contains(currentSubAtom) && !this.atomPositions.containsValue(currentSubAtom)) {
				appendAtom(currentSubAtom);
				addNeighbours(currentSubAtom, connectingAtoms);
			}
		}
		this.structuralFormula += ")";
	}

	/**
	 * Method to print atoms which are in a subStructure and not part of a atom set or connected to an atom set
	 * 
	 * @param atomID
	 *            The atom in the subStructure
	 * @param connectingAtoms
	 *            Set of connectingAtoms in the richAtomSet
	 */
	private void addNeighbours(String atomID, Set connectingAtoms) {

		RichAtom currentRichSubAtom = this.structuralAnalysis.getRichAtom(atomID);

		for (Connection connection : currentRichSubAtom.getConnections()) {
			// This is a atom or atom set connected to the atom in question
			String neighbour = connection.getConnected();

			// If this connection is not a connectingAtom or an atomSet then will append
			if (!connectingAtoms.contains(neighbour) && !(structuralAnalysis.getRichAtom(neighbour) == null)) {
				appendAtom(neighbour);
			}
		}
	}

	/**
	 * Adds the atom and its Hydrogens to the structuralFormula
	 * 
	 * @param atomID
	 */
	private void appendAtom(String atomID) {
		if (this.appendedAtoms.contains(atomID)) {
			return;
		} else {
			this.appendedAtoms.add(atomID);
		}
		IAtom atom = structuralAnalysis.getRichAtom(atomID).getStructure();
		this.structuralFormula += atom.getSymbol();
		int hydrogens = atom.getImplicitHydrogenCount();
		if (hydrogens > 0) {
			this.structuralFormula += "H";
			// Checking whether to use sub scripts or not
			if (this.useSubScripts) {
				this.structuralFormula += getSubScript(hydrogens);
			} else {
				this.structuralFormula += hydrogens;
			}
		}
	}

	/**
	 * Returns the computed string of Structural Formula
	 * 
	 * @param structuralAnalysis
	 * @param b
	 * @return
	 */
	public String getStructuralFormula(StructuralAnalysis structuralAnalysis, boolean subScripts) {
		this.useSubScripts = subScripts;
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
		if (number > 9) {
			throw new IllegalArgumentException("Sub Scripts cannot be larger than 9");
		}
		return Character.toString((char) (0x2080 + number));
	}
}
