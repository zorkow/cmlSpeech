/**
 * @file   Cactus.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 13:22:37 2014
 * 
 * @brief  Utility class to communicate with Cactus.
 * 
 * 
 */

package io.github.egonw.cactus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;


public class Cactus {
    
    /** Enum type for different translations via Cactus */
    public enum Type {
        IUPAC ("iupac", Cactus::getIUPAC),
        NAME ("name", Cactus::getName),
        FORMULA ("formula", Cactus::getFormula);
        
        public final String tag;
        public final Function<IAtomContainer, String> caller;
        /**
         * Enum type for different translations via Cactus with two parameters.
         * @param tag String for tag.
         * @param caller Closure with call to Cactus for that tag.
         */
        private Type (String tag, Function<IAtomContainer, String> caller) {
            this.caller = caller;
            this.tag = tag;
        }
    }

    /**
     * Send a call to the Cactus web service.
     * @param input String with input structure.
     * @param output Output format.
     * @return Result if any.
     */
    private final static List<String> getCactus(String input, String output) throws CactusException {
        URL url = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            url = new URL("http://cactus.nci.nih.gov/chemical/structure/" + input + "/" + output);    
            br = new BufferedReader (new InputStreamReader(url.openStream()));
            while(br.ready()){
                lines.add(br.readLine());
            }
        } catch (FileNotFoundException e) {
            throw new CactusException("No result for " + url);
        } catch (MalformedURLException e) {
            throw new CactusException("Can't make URL from input " + input + " " + output);
        } catch (IOException e) {
            throw new CactusException("IO exception when translating "  + url);
        }
        return lines;
    }

    /**
     * Translates a molecule to Inchi format.
     * @param molecule The input molecule.
     * @return String containing molecule in Inchi format.
     */
    private final static String translate(IAtomContainer molecule) throws CactusException {
        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            InChIGenerator gen = factory.getInChIGenerator(molecule);
            return(gen.getInchi());
        } catch (CDKException e) {
            throw new CactusException("Problems loading CDK Factory.");
        }
    }

    /**
     * Compute IUPAC name for molecule.
     * @param molecule Input molecule.
     * @return The IUPAC name if it exists.
     */
    public final static String getIUPAC(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        return getCactus(inchi, "IUPAC_Name").get(0);
    }

    /**
     * Compute chemical formula for molecule.
     * @param molecule Input molecule.
     * @return The chemical formula.
     */
    public final static String getFormula(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        return getCactus(inchi, "formula").get(0);
    }

    /**
     * Compute common name for molecule.
     * @param molecule Input molecule.
     * @return Common name if it exists.
     */
    public final static String getName(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        List<String> names = getCactus(inchi, "Names");
        List<String> alpha = names.stream().filter
            (line -> line.matches("^[a-zA-Z- ]+$")).collect(Collectors.toList());
        if (alpha.isEmpty()) {
            return names.get(0);
        }
        return alpha.get(0);
    }
}