package io.github.egonw;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.smiles.smarts.SmartsPattern;


public class FunctionalGroups {
	
	public static void compute(StructuralAnalysis _analysis){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/resources/smarts/smarts-pattern.txt")));
			String line;
			while ((line = br.readLine()) != null){
				int colonIndex = line.indexOf(":");
				if(colonIndex != -1 && line.charAt(0) !=  '#'){
					String name = line.substring(0, colonIndex);
					String pattern = line.substring(colonIndex + 2);
					checkMollecule(pattern, name, _analysis);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	private static void checkMollecule(String _pattern, String _name, StructuralAnalysis _analysis){
			try {
				IAtomContainer tempContainer = _analysis.getMolecule().clone();
				checkMollecule(_pattern, _name, tempContainer);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}			
	}
	
	private static void checkMollecule(String _pattern, String _name, IAtomContainer _mol){
		try{
		SMARTSQueryTool query = new SMARTSQueryTool(_pattern,DefaultChemObjectBuilder.getInstance());
		boolean matchesFound = false;
		try {
			matchesFound= query.matches(_mol);
		} catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(matchesFound){
			List<List<Integer>> mappings = query.getMatchingAtoms();
			List<IAtomContainer> = getMappedAtoms(mappings);
		}}catch (IllegalArgumentException e){
			System.out.println("Error: " + _name);
		}

	}

	private static List<IAtomContainer> getMappedAtoms(
			List<List<Integer>> mappings) {
		
		return null;
	}
}
