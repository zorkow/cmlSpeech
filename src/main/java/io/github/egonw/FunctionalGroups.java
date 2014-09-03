package io.github.egonw;

import io.github.egonw.RichAtomSet.Type;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.smiles.smarts.SmartsPattern;

public class FunctionalGroups {
	/**
	 * Goes through the file of smarts patterns and checks each pattern against
	 * the structural analysis
	 * 
	 * @param _analysis
	 */
	public static void compute(StructuralAnalysis _analysis) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"src/main/resources/smarts/smarts-pattern.txt")));
			String line;
			while ((line = br.readLine()) != null) {
				int colonIndex = line.indexOf(":");
				// Checks that the line has a colon in it and if it is one of
				// the patterns to be skipped (notated by a '#' before the name
				// in the file
				if (colonIndex != -1 && line.charAt(0) != '#') {
					String name = line.substring(0, colonIndex);
					String pattern = line.substring(colonIndex + 2);
					checkMollecule(pattern, name, _analysis);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Cloans the container before matching the pattern against it as the smarts
	 * matching is destructive
	 * 
	 * @param _pattern
	 *            the pattern being matched against the mollecule
	 * @param _name
	 *            The name of the functional group
	 * @param _analysis
	 *            The mollecule being matched
	 */
	private static void checkMollecule(String _pattern, String _name,
			StructuralAnalysis _analysis) {
		try {
			IAtomContainer tempContainer = _analysis.getMolecule().clone();
			checkMollecule(_pattern, _name, tempContainer);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks a pattern against a mollecule and puts them in atom sets
	 * 
	 * @param _pattern
	 *            the pattern to check against the mollecule
	 * @param _name
	 *            The name of the functional group
	 * @param _mol
	 *            The mollecule being checked against
	 */
	private static void checkMollecule(String _pattern, String _name,
			IAtomContainer _mol) {
		// deals with illegal smarts strings
		try {
			SMARTSQueryTool query = new SMARTSQueryTool(_pattern,
					DefaultChemObjectBuilder.getInstance());
			boolean matchesFound = false;
			try {
				matchesFound = query.matches(_mol);
			} catch (CDKException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// If there are matches, uses the getMatchingAtoms method to process
			// the matches
			if (matchesFound) {
				List<List<Integer>> mappings = query.getMatchingAtoms();
				System.out.println(_name);
				System.out.println(_pattern);
				List<RichAtomSet> groupList = getMappedAtoms(mappings, _mol);
			}
		} catch (IllegalArgumentException e) {
			// Shows which (if any) functional groups have illegal smarts
			// patterns in the file
			System.out.println("Error: " + _name);
		}

	}

	/**
	 * Method that takes a list of matched atom positions and returns a list of
	 * the relevant atom sets
	 * 
	 * This is the part that deals with any functionality and it has been
	 * abstracted out to make editing it easier
	 * 
	 * @param _mappings
	 *            A list of the list of matched atom positions for each seperate
	 *            match
	 * @param _mol
	 *            The atom the pattern was matched against
	 * @return a list of atom sets for each atom matched
	 */
	private static List<RichAtomSet> getMappedAtoms(
			List<List<Integer>> _mappings, IAtomContainer _mol) {
		List<RichAtomSet> groups = new ArrayList<RichAtomSet>();
		// Goes through each match for the pattern
		for (List<Integer> mappingList : _mappings) {
			IAtomContainer funcGroup = new AtomContainer();
			// Adds the matched mollecule to the atomcontainer
			for (Integer i : mappingList) {
				funcGroup.addAtom(_mol.getAtom(i));
			}
			RichAtomSet richFuncGroup = new RichAtomSet(funcGroup,
					Type.FUNCGROUP, "");// TODO FIND OUT HOW TO ADD AN ID
			groups.add(richFuncGroup);
			System.out.println(richFuncGroup);
		}
		return groups;
	}
}
