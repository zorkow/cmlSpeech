
//
package io.github.egonw;

import junit.framework.TestSuite;

import junit.framework.Test;

import junit.framework.TestCase;
import io.github.egonw.base.Cli;
import io.github.egonw.base.App;
import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.custommonkey.xmlunit.XMLTestCase;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Full functional test for the enricher.
 */

public class EnrichTest extends XMLTestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EnrichTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EnrichTest.class );
    }


    private static String readFile(String filename) {
        List<String> lines = new LinkedList<String>();
        try {
            lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.join("", lines);
    }


    private void compareEnrichedMolecule(String name) {
        System.out.println("Testing " + name + "...");
        String[] dummy = {"-a", "-r", "-nonih", "src/main/resources/" + name + ".mol"};
        try {
            new App();
            App.main(dummy);
        }
        catch (Exception e) {
            System.out.println("Application Error: " + e.getMessage());
            fail();
        }
        String original = readFile("src/test/resources/enriched/" + name + "-enr.cml");
        String revised  = readFile(name + "-enr.cml");
        try {
            assertXMLEqual(name, original, revised);
        }
        catch (Exception e) {
            System.out.println("XML Error " + e.getMessage());
            fail();
        }
    }

    // /**
    //  * Test enrichment of molecule.
    //  */
    // public void testChain()
    // {
    //     assertTrue( this.compareEnrichedMolecule("book1-004-05") );
    // }

    // /**
    //  * Test enrichment of molecule.
    //  */
    // public void testFunctional()
    // {
    //     assertTrue( this.compareEnrichedMolecule("book1-006-03") );
    // }


    // /**
    //  * Test enrichment of molecule.
    //  */
    // public void testRing()
    // {
    //     assertTrue( this.compareEnrichedMolecule("book1-012-00") );
    // }


    // /**
    //  * Test enrichment of molecule.
    //  */
    // public void testRingFunctional()
    // {
    //     this.compareEnrichedMolecule("aspirin");
    // }


    /**
     * Test enrichment of molecule.
     */
    public void testComplex()
    {
        this.compareEnrichedMolecule("US06358966-20020319-C00001");
    }


}
