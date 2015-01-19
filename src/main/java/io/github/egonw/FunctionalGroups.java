package io.github.egonw;

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
    private static Map<String, IAtomContainer> groups;
    
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
        groups = new HashMap<String, IAtomContainer>();
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
     * @param pattern
     *            the pattern to check against the molecule
     * @param name
     *            The name of the functional group
     * @param mol
     *            The molecule being checked against
     */
    private static void checkMolecule(String pattern, String name,
                                      IAtomContainer mol) {
        // deals with illegal smarts strings
        try {
            SMARTSQueryTool query = new SMARTSQueryTool(pattern,
                                                        DefaultChemObjectBuilder.getInstance());
            boolean matchesFound = false;
            matchesFound = query.matches(mol);
            // If there are matches, uses the getMatchingAtoms method to process
            // the matches
            if (matchesFound) {
                List<List<Integer>> mappings = query.getMatchingAtoms();
                System.out.println(name);
                System.out.println(pattern);
                getMappedAtoms(mappings, name, mol);
            }
        } catch (IllegalArgumentException e) {
            // Shows which (if any) functional groups have illegal smarts
            // patterns in the file
            System.out.println("Error: " + name);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Method that takes a list of matched atom positions and returns a list of
     * the relevant atom sets
     * 
     * This is the part that deals with any functionality and it has been
     * abstracted out to make editing it easier
     * 
     * @param mappings
     *            A list of the list of matched atom positions for each separate
     *            match
     * @param name
     *            The name of the functional group
     * @param mol
     *            The atom the pattern was matched against
     * @return a list of atom containers for each atom matched
     */
    private static void getMappedAtoms(List<List<Integer>> mappings,
                                       String name, IAtomContainer mol) {
        // Goes through each match for the pattern
        for (List<Integer> mappingList : mappings) {
            IAtomContainer funcGroup = new AtomContainer();
            // Adds the matched molecule to the atomcontainer
            for (Integer i : mappingList) {
                funcGroup.addAtom(mol.getAtom(i));
            }
            groups.put(name, funcGroup);
        }
    }


    public static Map<String, IAtomContainer> getGroups() {
        return groups;
    }
}
