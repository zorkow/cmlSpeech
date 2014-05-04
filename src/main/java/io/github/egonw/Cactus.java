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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.exception.CDKException;


//    URL url = new URL("http://cactus.nci.nih.gov/chemical/structure/CCCCCCCCCCCCCCCCCCC/Names");
//    URL url = new URL("http://cactus.nci.nih.gov/chemical/structure/InChI=1S/C10H15N/c1-9(11-2)8-10-6-4-3-5-7-10/h3-7,9,11H,8H2,1-2H3/t9-/m0/s1/Names");
//    URL url = new URL("http://cactus.nci.nih.gov/chemical/structure/CCCCCCCCCCCCCCCCCCC/IUPAC_Name");
//    URL url = new URL("http://cactus.nci.nih.gov/chemical/structure/InChI=1S/C10H15N/c1-9(11-2)8-10-6-4-3-5-7-10/h3-7,9,11H,8H2,1-2H3/t9-/m0/s1/IUPAC_Name");


public class Cactus {
    
    private final static ArrayList<String> getCactus(String input, String output) throws CactusException {
        URL url = null;
        BufferedReader br = null;
        ArrayList<String> lines = new ArrayList<>();
        System.out.println("Starting name lookup.");

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
        for(String line : lines) {
            System.out.println(line);
        }
        return lines;
    }

    private final static String translate(IAtomContainer molecule) throws CactusException {
        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            InChIGenerator gen = factory.getInChIGenerator(molecule);
            System.out.println(gen.getInchi());
            return(gen.getInchiKey());
        } catch (CDKException e) {
            throw new CactusException("Problems loading CDK Factory.");
        }
    }

    public final static void getIUPAC(IAtomContainer molecule) throws CactusException {
        String inchi = translate(molecule);
        getCactus(inchi, "IUPAC_Name");
        getCactus(inchi, "Names");
    }

}
