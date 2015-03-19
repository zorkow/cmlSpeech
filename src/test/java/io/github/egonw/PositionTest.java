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
 * @file   PositionTest.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Thu Feb 26 17:31:17 2015
 * 
 * @brief  An environment to run position tests.
 * 
 * 
 */

//
package io.github.egonw;

import static org.junit.Assert.assertArrayEquals;

import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.base.CMLEnricher;
import io.github.egonw.structure.RichAtomSet;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * A basic environment to run position tests.
 */

public class PositionTest {

    private static String testSources = "src/main/resources/test_files";

    private void comparePositions(String input, String set, String[] order) {
        CMLEnricher enricher = new CMLEnricher();
        enricher.loadMolecule
            (Paths.get(PositionTest.testSources, input).toString());
        enricher.analyseMolecule();
        RichAtomSet atomSet = RichStructureHelper.getRichAtomSet(set);
        List<String> actual = new ArrayList<String>();
        for (String atom: atomSet) {
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
                              new String[]{"a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2"});
        this.comparePositions("chains/5-bromo-8-decene.mol", "as1",
                              new String[]{"a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10", "a11"});
        this.comparePositions("chains/6-bromo-2-decene.mol", "as1",
                              new String[]{"a11", "a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2"});
        this.comparePositions("chains/6-bromo-2-nonene.mol", "as1",
                              new String[]{"a10", "a9", "a8", "a7", "a6", "a5", "a4", "a3", "a2"});
        this.comparePositions("chains/6-nonene.mol", "as1",
                              new String[]{"a9","a8","a7","a6","a5","a4","a3","a2","a1"});
        this.comparePositions("chains/1_chloro_2_pentene.mol", "as1",
                              new String[]{"a5", "a4", "a3", "a2", "a1"});
    }


    @Test
    public void ringIntTests() {
        System.out.println("Testing Ring with Internal Substitutions...");
        this.comparePositions("rings_int/ring_int1.mol", "as1",
                              new String[]{"a1", "a2", "a3", "a4", "a5", "a6"});
        this.comparePositions("rings_int/ring_int2.mol", "as1",
                              new String[]{"a5", "a4", "a3", "a2", "a1", "a6"});
        this.comparePositions("rings_int/ring_int3.mol", "as1",
                              new String[]{"a5", "a4", "a3", "a2", "a1", "a6"});
        this.comparePositions("rings_int/ring_int4.mol", "as1",
                              new String[]{"a3", "a2", "a1", "a6", "a5", "a4"});
        this.comparePositions("rings_int/ring_int5.mol", "as1",
                              new String[]{"a5", "a4", "a3", "a2", "a1", "a6"});
        this.comparePositions("rings_int/ring_int6.mol", "as1",
                              new String[]{"a5", "a4", "a3", "a2", "a1", "a6"});
        this.comparePositions("rings_int/ring_int7.mol", "as1",
                              new String[]{"a5", "a6", "a1", "a2", "a3", "a4"});
    }


    @Test
    public void ringExtTests() {
        System.out.println("Testing Ring with External Substitutions...");
        this.comparePositions("rings_ext/ring_ext1.mol", "as1",
                              new String[]{"a5", "a2", "a4", "a6", "a7", "a3"});
        this.comparePositions("rings_ext/ring_ext2.mol", "as1",
                              new String[]{"a5", "a2", "a4", "a6", "a7", "a3"});
        this.comparePositions("rings_ext/ring_ext3.mol", "as1",
                              new String[]{"a6", "a4", "a2", "a5", "a3", "a7"});
        this.comparePositions("rings_ext/ring_ext4.mol", "as1",
                              new String[]{"a7", "a3", "a5", "a2", "a4", "a6"});
        this.comparePositions("rings_ext/ring_ext5.mol", "as1",
                              new String[]{"a7", "a6", "a4", "a2", "a5", "a3"});
        this.comparePositions("rings_ext/ring_ext6.mol", "as1",
                              new String[]{"a7", "a3", "a5", "a2", "a4", "a6"});
        this.comparePositions("rings_ext/ring_ext7.mol", "as1",
                              new String[]{"a6", "a7", "a3", "a5", "a2", "a4"});
    }
}



