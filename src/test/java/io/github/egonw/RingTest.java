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
 * @file   RingTest.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Fri Mar 20 22:47:09 2015
 * 
 * @brief  Running tests particular for ring structures.
 * 
 * 
 */

//
package io.github.egonw;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import io.github.egonw.base.CmlEnricher;
import java.nio.file.Paths;
import io.github.egonw.structure.RichRing;
import io.github.egonw.analysis.RichStructureHelper;
import java.util.stream.Collectors;

import io.github.egonw.base.CmlEnricher;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import io.github.egonw.base.CmlNameComparator;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.Before;
import io.github.egonw.base.Cli;
import java.util.Arrays;

/**
 *
 */
public class RingTest {

    private static String testSources = "src/main/resources/test_files";

    @Before
    public void initCli() throws Exception {
        String[] dummy = {"-nonih"};
        Cli.init(dummy);
    }

    public void compareSets(List<String> actual, String[] expected) {
        SortedSet<String> actualSet = new TreeSet<>(new CmlNameComparator());
        SortedSet<String> expectedSet = new TreeSet<>(new CmlNameComparator());
        actualSet.addAll(actual);
        expectedSet.addAll(Arrays.asList(expected));
        System.out.println(actualSet);
        System.out.println(expectedSet);
        assertTrue(actualSet.size() == actual.size());
        assertArrayEquals(Lists.newArrayList(actualSet).toArray(),
                          Lists.newArrayList(expectedSet).toArray());
    }


    private void loadMolecule(String input) {
        CmlEnricher enricher = new CmlEnricher();
        enricher.loadMolecule
            (Paths.get(RingTest.testSources, input).toString());
        enricher.analyseMolecule();
    }
    
    
    public void compareRim(String input, String set, String[] actual) {
        this.loadMolecule(input);
        RichRing atomSet = (RichRing)RichStructureHelper.getRichAtomSet(set);
        compareSets(atomSet.getRim().stream().map(a -> a.getID()).collect(Collectors.toList()),
                    actual);
    }


    public void compareSubSystems(String input, String set, Integer actual) {
        this.loadMolecule(input);
        RichRing atomSet = (RichRing)RichStructureHelper.getRichAtomSet(set);
        assertTrue(atomSet.getSubSystems().size() == actual);
    }


    @Test
    public void rimTest() {
        System.out.println("Testing rims of fused rings...");
        this.compareRim("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13"});
        this.compareRim("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13", "a14"});
        this.compareRim("rings_fused_simple/pyridine.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                                     "a16", "a17", "a18", "a19", "a20"});
        this.compareRim("rings_fused_simple_ext/fused_ext1.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13"});
        this.compareRim("rings_fused_simple_ext/fused_ext2.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13"});
        this.compareRim("rings_fused_inner/ovalene.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                                     "a16", "a17", "a18", "a19", "a20", "a21", "a22"});
    }

    @Test
    public void subRingTest() {
        System.out.println("Testing subring numbers...");
        this.compareSubSystems("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1", 3);
        this.compareSubSystems("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1", 3);
        this.compareSubSystems("rings_fused_simple/pyridine.mol", "as1", 5);
        this.compareSubSystems("rings_fused_simple_ext/fused_ext1.mol", "as1", 3);
        this.compareSubSystems("rings_fused_simple_ext/fused_ext2.mol", "as1", 3);
        this.compareSubSystems("rings_fused_inner/ovalene.mol", "as1", 10);
    }
}
