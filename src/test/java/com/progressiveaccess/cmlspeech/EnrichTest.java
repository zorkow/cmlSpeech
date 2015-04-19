// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/**
 * @file   EnrichTest.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Thu Feb 26 17:30:05 2015
 * 
 * @brief  Full blown tests for enrichment of some standard molecules.
 * 
 * 
 */

//
package com.progressiveaccess.cmlspeech;

import com.progressiveaccess.cmlspeech.base.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.custommonkey.xmlunit.XMLTestCase;


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
        String[] dummy = {"-ao", "-a", "-r", "-nonih",
                          "src/main/resources/test_files/molecule/" +
                          name + ".mol"};
        try {
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

    /**
     * Test enrichment of aliphatic chains.
     */
    @Test
    public void testChain()
    {
        this.compareEnrichedMolecule("book1-004-05");
    }

    /**
     * Test enrichment of functional groups.
     */
    @Test
    public void testFunctional()
    {
        this.compareEnrichedMolecule("book1-006-03");
    }


    /**
     * Test enrichment of molecule.
     */
    public void testRing()
    {
        this.compareEnrichedMolecule("book1-012-00");
    }


    /**
     * Test enrichment of molecule.
     */
    public void testRingFunctional()
    {
        this.compareEnrichedMolecule("aspirin");
    }


    /**
     * Test enrichment of molecule.
     */
    public void testComplex()
    {
      this.compareEnrichedMolecule("US06358966-20020319-C00001");
    }

}
