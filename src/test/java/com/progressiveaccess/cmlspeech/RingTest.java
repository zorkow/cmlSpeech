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
 * @file RingTest.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date Fri Mar 20 22:47:09 2015
 *
 * @brief Running tests particular for ring structures.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.progressiveaccess.cmlspeech.analysis.RichStructureHelper;
import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.base.CmlEnricher;
import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import com.progressiveaccess.cmlspeech.structure.RichRing;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Tests for ring structures.
 */
public class RingTest {

  private static String testSources = "src/main/resources/test_files";

  /**
   * Initialises a dummy Cli.
   *
   * @throws Exception
   *          Possible exceptions as files are loaded etc.
   */
  @Before
  private void initCli() throws Exception {
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
    enricher.loadMolecule(Paths.get(RingTest.testSources, input).toString());
    enricher.analyseMolecule();
  }


  /**
   * Compares two sets.
   *
   * @param actual
   *          The computed set.
   * @param expected
   *          The expected set.
   */
  public void compareSets(final List<String> actual, final String[] expected) {
    final SortedSet<String> actualSet = new TreeSet<>(new CmlNameComparator());
    final SortedSet<String> expectedSet =
        new TreeSet<>(new CmlNameComparator());
    actualSet.addAll(actual);
    expectedSet.addAll(Arrays.asList(expected));
    System.out.println(actualSet);
    System.out.println(expectedSet);
    assertTrue(actualSet.size() == actual.size());
    assertArrayEquals(Lists.newArrayList(actualSet).toArray(),
        Lists.newArrayList(expectedSet).toArray());
  }


  /**
   * Compares the rim computed for a ring structure against the expected one.
   *
   *
   * @param input
   *          The input molecule that is loaded from file.
   * @param set
   *          Name of ring set in the rich molecule whose ring to compare.
   * @param expected
   *          The expected order of rim elements.
   */
  public void compareRim(final String input, final String set,
      final String[] expected) {
    this.loadMolecule(input);
    final RichRing atomSet = (RichRing) RichStructureHelper.getRichAtomSet(set);
    this.compareSets(atomSet.getRim().stream().map(a -> a.getID())
        .collect(Collectors.toList()), expected);
  }


  /**
   * Compares the sub-systems computed for a fused ring against the expected
   * one.
   *
   * @param input
   *          The input molecule that is loaded from file.
   * @param set
   *          Name of fused ring in the rich molecule whose ring to compare.
   * @param expected
   *          The expected order of sub systems.
   */
  public void compareSubSystems(final String input, final String set,
      final Integer expected) {
    this.loadMolecule(input);
    final RichRing atomSet = (RichRing) RichStructureHelper.getRichAtomSet(set);
    assertTrue(atomSet.getSubSystems().size() == expected);
  }


  @Test
  public void rimTest() {
    System.out.println("Testing rims of fused rings...");
    this.compareRim("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13"});
    this.compareRim("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13", "a14"});
    this.compareRim("rings_fused_simple/pyridine.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                      "a16", "a17", "a18", "a19", "a20"});
    this.compareRim("rings_fused_simple_ext/fused_ext1.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13"});
    this.compareRim("rings_fused_simple_ext/fused_ext2.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13"});
    this.compareRim("rings_fused_inner/ovalene.mol", "as1",
        new String[] {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                      "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                      "a16", "a17", "a18", "a19", "a20", "a21", "a22"});
  }

  @Test
  public void subRingTest() {
    System.out.println("Testing subring numbers...");
    this.compareSubSystems("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol",
        "as1", 3);
    this.compareSubSystems("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol",
        "as1", 3);
    this.compareSubSystems("rings_fused_simple/pyridine.mol", "as1", 5);
    this.compareSubSystems("rings_fused_simple_ext/fused_ext1.mol", "as1", 3);
    this.compareSubSystems("rings_fused_simple_ext/fused_ext2.mol", "as1", 3);
    this.compareSubSystems("rings_fused_inner/ovalene.mol", "as1", 10);
  }
}
