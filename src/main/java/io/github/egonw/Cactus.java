/**
 * @file   Cactus.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sun May  4 13:22:37 2014
 * 
 * @brief  Utility class to communicate with Cactus.
 * 
 * 
 */

package io.github.egonw;

import io.github.egonw.CactusException;

import java.io.IOException;

import java.net.MalformedURLException;

import java.net.URL;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.exception.CDKException;
import java.util.stream.Collectors;


public class Cactus {
    
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
            throw new CactusException("No name for " + input + " " + output);
        } catch (MalformedURLException e) {
            throw new CactusException("Can't make URL from input " + input + " " + output);
        } catch (IOException e) {
            throw new CactusException("IO exception when translating "  + input + " " + output);
        }
        return lines;
    }

    private final static String translate(IAtomContainer molecule) throws CactusException {
        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            InChIGenerator gen = factory.getInChIGenerator(molecule);
            return(gen.getInchi());
        } catch (CDKException e) {
            throw new CactusException("Problems loading CDK Factory.");
        }
    }

    public final static String getIUPAC(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        System.out.println("Translating\n" + molecule + "\n" + "inchi: " + inchi);
        return getCactus(inchi, "IUPAC_Name").get(0);
    }

    public final static String getName(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        List<String> names = getCactus(inchi, "Names");
        List<String> alpha = names.stream().filter
            (line -> {return line.matches("^[a-zA-Z- ]+$");}).collect(Collectors.toList());
        if (alpha.isEmpty()) {
            return names.get(0);
        }
        return alpha.get(0);
    }
}
