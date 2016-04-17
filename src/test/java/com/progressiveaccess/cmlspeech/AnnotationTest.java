// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/**
 * @file   AnnotationTest.java
 * @author Volker Sorge<a href="mailto:V.Sorge@progressiveaccess.com">
 *          Volker Sorge</a>
 * @date   Tue Jul  7 21:13:28 2015
 *
 * @brief  Base class for annotation test.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech;

import com.progressiveaccess.cmlspeech.base.App;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class for annotation tests.
 */

public abstract class AnnotationTest extends XMLTestCase {


  /**
   * @return Parameter list for the dummy cli.
   */
  abstract String[] getParameters();


  /**
   * @return The directory with the expected result files.
   */
  abstract String expectedDirectory();


  /**
   * Create the test case.
   *
   * @param testName
   *          Name of the test case.
   */
  public AnnotationTest(final String testName) {
    super(testName);
  }


  /**
   * Reads a file for comparison.
   *
   * @param filename
   *          The name of the file to load.
   *
   * @return The content of the file as a one line string.
   */
  private static String readFile(final String filename) {
    List<String> lines = new LinkedList<String>();
    try {
      lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return String.join("", lines);
  }


  /**
   * Compares enriched molecules.
   *
   * @param name
   *          The name of the molecule, which corresponds to the filename.
   */
  public void compareEnrichedMolecule(final String name) {
    System.out.println("Testing " + name + "...");
    String[] parameters = this.getParameters();
    Integer length = parameters.length;
    String[] dummy = new String[length + 1];
    System.arraycopy(parameters, 0, dummy, 0, length);
    dummy[length] = "src/main/resources/test_files/molecule/" + name + ".mol";
    try {
      App.main(dummy);
    } catch (final Exception e) {
      System.out.println("Application Error: " + e.getMessage());
      fail();
    }
    final String original = readFile("src/test/resources/"
        + this.expectedDirectory() + "/" + name + "-enr.cml");
    final String revised = readFile(name + "-enr.cml");
    System.out.println(revised);
    try {
      this.assertXMLEqual(name, original, revised);
    } catch (final Exception e) {
      System.out.println("XML Error " + e.getMessage());
      fail();
    }
  }


  /**
   * Test enrichment of aliphatic chains.
   */
  @Test
  public void testChain() {
    this.compareEnrichedMolecule("book1-004-05");
  }


  /**
   * Test enrichment of functional groups.
   */
  @Test
  public void testFunctional() {
    this.compareEnrichedMolecule("book1-006-03");
  }


  /**
   * Test enrichment of ring.
   */
  @Test
  public void testRing() {
    this.compareEnrichedMolecule("book1-012-00");
  }


  /**
   * Test enrichment of ring with functional groups.
   */
  @Test
  public void testRingFunctional() {
    this.compareEnrichedMolecule("aspirin");
  }


  /**
   * Test enrichment of complex molecule with multiple systems..
   */
  @Test
  public void testComplex() {
    this.compareEnrichedMolecule("US06358966-20020319-C00001");
  }


  /**
   * Test enrichment of large fused ring system.
   */
  @Test
  public void testFused() {
    this.compareEnrichedMolecule("ovalene");
  }

}
