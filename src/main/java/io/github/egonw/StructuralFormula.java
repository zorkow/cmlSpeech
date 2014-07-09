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

		Iterable<IAtom> it = molecule.atoms();

		String structuralFormula = "";

		for (IAtom a : it) {

			structuralFormula += a.getSymbol();

			int h = a.getImplicitHydrogenCount();

			if (h > 0) {
				structuralFormula += "H";
				
				String subScript = getSubScript(h);
				
				structuralFormula += subScript;

			}

		}
		
		System.out.println("");
		
		System.out.println("The structural formula is:");
		
		System.out.println(structuralFormula);

		System.out.println("");

	}
	
	public static String getSubScript(int number){
		
		switch (number){
		case 1: return "\u2081";
		case 2: return "\u2082";
		case 3: return "\u2083";
		case 4: return "\u2084";
		case 5: return "\u2085";
		case 6: return "\u2086";
		case 7: return "\u2087";
		case 8: return "\u2088";
		case 9: return "\u2089";
		}
		
		return null;
		
		
	}
	
	

}














