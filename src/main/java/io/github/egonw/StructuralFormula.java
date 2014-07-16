package io.github.egonw;

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

	private static String structuralFormula = "";
	public static BiMap<Integer, String> atomPositions = HashBiMap.create();
	public static RichAtomSet rac;
	public static StructuralAnalysis sa;

	public static void computeAnalysis(StructuralAnalysis saImported) {
		System.out.println("");
		sa = saImported;

		List<RichAtomSet> atomSets = sa.getAtomSets();

		if (atomSets.size() == 0) {
			String currentAtom = atomPositions.get(0);
			atomPositions = rac.atomPositions;
			structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();

			addHydrogens(currentAtom);

			System.out.println(structuralFormula);
		}

		RichAtomSet richAtomSet = sa.getAtomSets().get(0);

		computeRAC(richAtomSet);

	}

	public static void computeRAC(RichAtomSet rac) {
		structuralFormula = "";

		String currentAtom = null;
		IAtom currentIAtom = null;
		RichAtom currentRichAtom = null;

		Set connectingAtoms = rac.getConnectingAtoms();

		atomPositions = rac.atomPositions;

		for (int i = 1; i < atomPositions.size() + 1; i++) {
			currentAtom = atomPositions.get(i);
			currentIAtom = sa.getRichAtom(currentAtom).getStructure();
			currentRichAtom = sa.getRichAtom(currentAtom);

			if (!connectingAtoms.contains(currentAtom)) {
				structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();

				addHydrogens(currentAtom);

			} else {
				structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();

				addHydrogens(currentAtom);

				structuralFormula += "(";

				Set<Connection> connections = currentRichAtom.getConnections();

				for (Connection connection : connections) {
					currentAtom = connection.getConnected();

					if (!connectingAtoms.contains(currentAtom) && !atomPositions.containsValue(currentAtom)) {
						structuralFormula += sa.getRichAtom(currentAtom).getStructure().getSymbol();

						addHydrogens(currentAtom);
					}
				}
				structuralFormula += ")";
			}
		}

		System.out.println(structuralFormula);
	}

	public static void addHydrogens(String id) {
		int hydrogens = sa.getRichAtom(id).getStructure().getImplicitHydrogenCount();

		if (hydrogens > 0) {
			structuralFormula += "H";
			structuralFormula += getSubScript(hydrogens);
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
		return "Error: Wrong number in getSubScript";
	}

}
