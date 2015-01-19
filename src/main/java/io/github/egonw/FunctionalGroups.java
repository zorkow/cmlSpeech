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
import java.util.Map;
import java.util.HashMap;

public class FunctionalGroups {

    private static volatile FunctionalGroups instance = null;
    private final static String smartsFile = "src/main/resources/smarts/smarts-pattern.txt";
    private static Map<String, String> smartsPatterns = new HashMap<String, String>();
    
    
    protected FunctionalGroups() {
        FunctionalGroups.loadSmartsFile();
    }


    private static void loadSmartsFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(smartsFile)));
            String line;
            while ((line = br.readLine()) != null) {
                int colonIndex = line.indexOf(":");
                // Checks that the line has a colon in it and if it is one of
                // the patterns to be skipped (notated by a '#' before the name
                // in the file
                if (colonIndex != -1 && line.charAt(0) != '#') {
                    smartsPatterns.put(line.substring(0, colonIndex),
                                       line.substring(colonIndex + 2));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static FunctionalGroups getInstance() {
        if (instance == null) {
            instance = new FunctionalGroups();
        }
        return instance;
    }
    

    /**
     * Goes through the file of smarts patterns and checks each pattern against
     * the atom container.
     * 
     * @param molecule
     */
    public static void compute(IAtomContainer molecule) {
        for (Map.Entry<String, String> smarts : smartsPatterns.entrySet()) {
            try {
                checkMolecule(smarts.getValue(), smarts.getKey(), molecule.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Checks a pattern against a molecule and puts them in atom sets
     * 
     * @param _pattern
     *            the pattern to check against the molecule
     * @param _name
     *            The name of the functional group
     * @param _mol
     *            The molecule being checked against
     */
    private static void checkMolecule(String _pattern, String _name,
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
            // Adds the matched molecule to the atomcontainer
            for (Integer i : mappingList) {
                funcGroup.addAtom(_mol.getAtom(i));
            }
            RichAtomSet richFuncGroup = new RichAtomSet(funcGroup,
                                                        Type.FUNCGROUP, "");// TODO FIND OUT HOW TO ADD AN ID
            groups.add(richFuncGroup);
        }
        return groups;
    }
}
