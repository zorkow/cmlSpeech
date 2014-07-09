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
	 * Computes and prints the structural formula of the given molecule to a
	 * string
	 * 
	 * @param molecule
	 *            The IAtomContainer to be translated
	 */
	public static void compute(IAtomContainer molecule) {

		System.out.println("");

		Iterable<IAtom> it = molecule.atoms();

		String structuralFormula = "";

		for (IAtom a : it) {

			structuralFormula += a.getSymbol();

			int h = a.getImplicitHydrogenCount();

			if (h > 0) {
				structuralFormula += "H";
				structuralFormula += h;

			}

		}
		
		System.out.println("The structural formula is:");
		
		System.out.println(structuralFormula);

		System.out.println("");

	}

}
