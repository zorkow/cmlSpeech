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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import java.util.stream.StreamSupport;

public class FunctionalGroups {

    private static volatile FunctionalGroups instance = null;
    private final static String[] smartsFiles = {
        "src/main/resources/smarts/daylight-pattern.txt",
        "src/main/resources/smarts/smarts-pattern.txt"
    };
    private static Map<String, String> smartsPatterns = new HashMap<String, String>();
    private static Map<String, IAtomContainer> groups;
    private static Integer groupCounter = 1;

    private static IAtomContainer molecule;
    
    protected FunctionalGroups() {
        FunctionalGroups.loadSmartsFiles();
    }


    private static void loadSmartsFiles() {
        for (String file : smartsFiles) {
            loadSmartsFile(file);
        }
    }

    private static void loadSmartsFile(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));
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
    public void compute(IAtomContainer molecule) {
        FunctionalGroups.molecule = molecule;
        groups = new HashMap<String, IAtomContainer>();
        for (Map.Entry<String, String> smarts : smartsPatterns.entrySet()) {
            try {
                checkMolecule(smarts.getValue(), smarts.getKey(),
                              FunctionalGroups.molecule.clone());
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
                getMappedAtoms(mappings, name);
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
     * Retrieves matched atoms from the molecule container by position and adds
     * them to the functional group container.
     * 
     * @param mappings
     *            A list of the list of matched atom positions for each separate
     *            match
     * @param name
     *            The name of the functional group
     */
    private static void getMappedAtoms(List<List<Integer>> mappings, String name) {
        // Goes through each match for the pattern
        for (List<Integer> mappingList : mappings) {
            IAtomContainer funcGroup = new AtomContainer();
            // Adds the matched molecule to the atomcontainer
            for (Integer i : mappingList) {
                funcGroup.addAtom(FunctionalGroups.molecule.getAtom(i));
            }
            getMappedBonds(funcGroup);
            groups.put(name + "-" + groupCounter++, funcGroup);
        }
    }


    /**
     * Retrieves the necessary bonds for a functional group from the molecule
     * container and adds them to the functional group container.
     * 
     * @param fg
     *            Functonal group container.
     */
    private static void getMappedBonds(IAtomContainer fg) {
        for (IAtom atom : fg.atoms()) {
            for (IBond bond : FunctionalGroups.molecule.getConnectedBondsList(atom)) {
                if (!fg.contains(bond) &&
                    StreamSupport.stream(bond.atoms().spliterator(), false).
                    allMatch(a -> fg.contains(a))) {
                    fg.addBond(bond);
                }
            }
        }
    }

    public Map<String, IAtomContainer> getGroups() {
        return groups;
    }
}
