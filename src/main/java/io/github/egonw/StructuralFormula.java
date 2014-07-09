package io.github.egonw;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

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
	private static String structuralFormula;

	/**
	 * Computes and prints the structural formula of the given molecule to a
	 * string
	 * 
	 * @param molecule
	 *            The IAtomContainer to be translated
	 */
	public static void compute(IAtomContainer molecule) {

		// Iterator to go over the atoms in the molecule
		Iterable<IAtom> it = molecule.atoms();

		// For each atom in the iterator
		for (IAtom a : it) {

			// Gets the symbol of the current atom
			structuralFormula += a.getSymbol();

			// Gets the hydrogen count of the current atom
			int h = a.getImplicitHydrogenCount();

			// If h > 0 then adds H to the formula and works out the subscript
			// of
			// the number of hydrogens
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

	/**
	 * Returns the subscript of a given number
	 * 
	 * @param number
	 *            The input number to be converted
	 * @return
	 */
	public static String getSubScript(int number) {

		switch (number) {
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

		return null;

	}

}
