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
 * @file PositionTest.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date Thu Feb 26 17:31:17 2015
 *
 * @brief An environment to run position tests.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech;

import static org.junit.Assert.assertArrayEquals;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.base.CmlEnricher;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichSuperSet;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A basic environment to run position tests.
 */

public class PositionTest {

  private static String testSources = "src/main/resources/test_files";

  /**
   * Initialises a dummy Cli.
   *
   * @throws Exception
   *          Possible exceptions as files are loaded etc.
   */
  @Before
  public void initCli() throws Exception {
    final String[] dummy = {"-nonih"};
    Cli.init(dummy);
  }


  /**
   * Loads a molecule from a file.
   *
   * @param input
   *          Name of the input file.
   */
  private void loadMolecule(final String input) {
    final CmlEnricher enricher = new CmlEnricher();
    enricher.loadMolecule(Paths
                          .get(PositionTest.testSources, input).toString());
    enricher.analyseMolecule();
  }


  /**
   * Compares a given list of position against a compute list of positions.
   *
   * @param input
   *          The molecule to work with. Is loaded form file.
   * @param set
   *          Name of set in the enriched molecule to consider.
   * @param order
   *          The expected order of elements of set.
   */
  public void comparePositions(final String input, final String set,
      final String[] order) {
    this.loadMolecule(input);
    final RichAtomSet atomSet = RichStructureHelper.getRichAtomSet(set);
    final List<String> actual = new ArrayList<String>();
    for (final String atom : atomSet) {
      actual.add(atom);
    }
    System.out.println(actual);
    System.out.println(Arrays.toString(order));
    assertArrayEquals(actual.toArray(), order);
  }


  /**
   * Compares a give path against the computed one.
   *
   * @param input
   *          The molecule to work with. Is loaded form file.
   * @param set
   *          Name of set in the enriched molecule to consider.
   * @param order
   *          The expected order of elements in the path.
   */
  public void comparePaths(final String input, final String set,
      final String[] order) {
    this.loadMolecule(input);
    final RichSuperSet atomSet = (RichSuperSet) RichStructureHelper
        .getRichAtomSet(set);
    final List<String> actual = new ArrayList<String>();
    for (final String atom : atomSet.getPath()) {
      actual.add(atom);
    }
    System.out.println(actual);
    System.out.println(Arrays.toString(order));
    assertArrayEquals(actual.toArray(), order);
  }


  @Test
  public void chainTests() {
    System.out.println("Testing Aliphatic Chains...");
    this.comparePositions("chains/5-bromo-6-nonene.mol", "as1",
        new String[] {"a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2"});
    this.comparePositions("chains/5-bromo-8-decene.mol", "as1",
        new String[] {"a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10",
                      "a11"});
    this.comparePositions("chains/6-bromo-2-decene.mol", "as1",
        new String[] {"a11", "a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3",
                      "a2"});
    this.comparePositions("chains/6-bromo-2-nonene.mol", "as1",
        new String[] {"a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2"});
    this.comparePositions("chains/6-nonene.mol", "as1",
        new String[] {"a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2", "a1"});
    this.comparePositions("chains/1_chloro_2_pentene.mol", "as1",
        new String[] {"a5", "a4", "a3", "a2", "a1"});
  }


  @Test
  public void ringIntTests() {
    System.out.println("Testing Ring with Internal Substitutions...");
    this.comparePositions("rings_int/ring_int1.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a6"});
    this.comparePositions("rings_int/ring_int2.mol", "as1",
        new String[] {"a5", "a4", "a3", "a2", "a1", "a6"});
    this.comparePositions("rings_int/ring_int3.mol", "as1",
        new String[] {"a5", "a4", "a3", "a2", "a1", "a6"});
    this.comparePositions("rings_int/ring_int4.mol", "as1",
        new String[] {"a3", "a2", "a1", "a6", "a5", "a4"});
    this.comparePositions("rings_int/ring_int5.mol", "as1",
        new String[] {"a5", "a4", "a3", "a2", "a1", "a6"});
    this.comparePositions("rings_int/ring_int6.mol", "as1",
        new String[] {"a5", "a4", "a3", "a2", "a1", "a6"});
    this.comparePositions("rings_int/ring_int7.mol", "as1",
        new String[] {"a5", "a6", "a1", "a2", "a3", "a4"});
  }


  @Test
  public void ringExtTests() {
    System.out.println("Testing Ring with External Substitutions...");
    this.comparePositions("rings_ext/ring_ext1.mol", "as1",
        new String[] {"a5", "a2", "a4", "a6", "a7", "a3"});
    this.comparePositions("rings_ext/ring_ext2.mol", "as1",
        new String[] {"a5", "a2", "a4", "a6", "a7", "a3"});
    this.comparePositions("rings_ext/ring_ext3.mol", "as1",
        new String[] {"a6", "a4", "a2", "a5", "a3", "a7"});
    this.comparePositions("rings_ext/ring_ext4.mol", "as1",
        new String[] {"a7", "a3", "a5", "a2", "a4", "a6"});
    this.comparePositions("rings_ext/ring_ext5.mol", "as1",
        new String[] {"a7", "a6", "a4", "a2", "a5", "a3"});
    this.comparePositions("rings_ext/ring_ext6.mol", "as1",
        new String[] {"a7", "a3", "a5", "a2", "a4", "a6"});
    this.comparePositions("rings_ext/ring_ext7.mol", "as1",
        new String[] {"a6", "a7", "a3", "a5", "a2", "a4"});
  }


  @Test
  public void fusedRingSimpleTest() {
    System.out
    .println("Testing simple fused rings with internal substitutions...");
    this.comparePositions("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol",
                          "as1",
                          new String[] {"a8", "a7", "a12", "a13",
                                        "a1", "a2", "a3", "a4", "a5",
                                        "a11", "a10", "a9"});
    this.comparePositions("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol",
                          "as1",
                          new String[] {"a1", "a2", "a3", "a4", "a5", "a14",
                                        "a13", "a12", "a11", "a10", "a9", "a8",
                                        "a7", "a6"});
    // This needs to become more deterministic!
    // this.comparePositions("rings_fused_simple/pyridine.mol", "as1",
    // new String[]{"a1", "a5", "a6", "a7",
    // "a12", "a13", "a14", "a15", "a16",
    // "a11", "a10", "a9",
    // "a17", "a18", "a19", "a20", "a8",
    // "a4", "a3", "a2"
    // });
  }


  @Test
  public void fusedRingSimpleExtTest() {
    System.out
    .println("Testing simple fused rings with external substitutions...");
    this.comparePositions("rings_fused_simple_ext/fused_ext1.mol", "as1",
        new String[] {"a8", "a7", "a12", "a13",
                      "a1", "a2", "a3", "a4", "a5",
                      "a11", "a10", "a9"});
    this.comparePositions("rings_fused_simple_ext/fused_ext2.mol", "as1",
        new String[] {"a13", "a12", "a7", "a8", "a9", "a10", "a11",
                      "a5", "a4", "a3", "a2", "a1"});
  }


  @Test
  public void fusedRingComplexTest() {
    System.out
    .println("Testing complex fused rings with internal substitutions...");
    this.comparePositions("rings_fused_inner/ovalene.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                      "a16", "a17", "a18", "a19", "a20", "a21", "a22"});
  }


  @Test
  public void essentialRingTest() {
    System.out.println("Testing order of essential rings in fused rings...");
    this.comparePaths("rings_fused_inner/ovalene.mol", "as1",
        new String[] {"as2", "as3", "as4", "as5", "as6", "as7", "as8",
                      "as9", "as10", "as11"});
    this.comparePaths("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
        new String[] {"as4", "as2", "as3"});
    this.comparePaths("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
        new String[] {"as2", "as3", "as4"});
    // This needs to become more deterministic!
    // Check wrt. largest subring. Then check wrt. smallest atom name.
    // this.comparePositions("rings_fused_simple/pyridine.mol", "as1",
    // new String[]{"as2", "as3", "as4", "as5", "as6"});
  }


  @Test
  public void moleculeTest() {
    System.out.println("Testing order of blocks in a molecule...");
    this.comparePaths("molecule/book1-004-05.mol", "as3",
        new String[] {"as1", "as2"});
    this.comparePaths("molecule/book1-006-03.mol", "as4",
        new String[] {"as1", "as3", "as2"});
    this.comparePaths("molecule/book1-012-00.mol", "as5",
        new String[] {"as1", "as4", "as2", "as3"});
    this.comparePaths("molecule/aspirin.mol", "as4",
        new String[] {"as1", "as3", "as2"});
    this.comparePaths("molecule/US06358966-20020319-C00001.mol", "as11",
        new String[] {"as1", "as7", "as9", "as4", "as8", "as10"});
  }


  @Test
  public void functionalGroupTest() {
    System.out.println("Testing order in functional groups...");
    this.comparePositions("molecule/aspirin.mol", "as3",
        new String[] {"a8", "a13", "a6", "a10", "a1"});
  }

}
