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
import io.github.egonw.structure.RichSuperSet;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.junit.Before;
import io.github.egonw.base.Cli;

/**
 * A basic environment to run position tests.
 */

public class PositionTest {

    private static String testSources = "src/main/resources/test_files";

    @Before
    public void initCli() throws Exception {
        String[] dummy = {"-nonih"};
        Cli.init(dummy);
    }

    private void loadMolecule(String input) {
        CMLEnricher enricher = new CMLEnricher();
        enricher.loadMolecule
            (Paths.get(PositionTest.testSources, input).toString());
        enricher.analyseMolecule();
    }
    
    
    public void comparePositions(String input, String set, String[] order) {
        this.loadMolecule(input);
        RichAtomSet atomSet = RichStructureHelper.getRichAtomSet(set);
        List<String> actual = new ArrayList<String>();
        for (String atom: atomSet) {
            actual.add(atom);
        }
        System.out.println(actual);
        System.out.println(Arrays.toString(order));
        assertArrayEquals(actual.toArray(), order);
    }


    public void comparePaths(String input, String set, String[] order) {
        this.loadMolecule(input);
        RichSuperSet atomSet = (RichSuperSet)RichStructureHelper.getRichAtomSet(set);
        List<String> actual = new ArrayList<String>();
        for (String atom: atomSet.getPath()) {
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


    @Test
    public void fusedRingSimpleTest() {
        System.out.println("Testing simple fused rings with internal substitutions...");
        this.comparePositions("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
                        new String[]{"a8", "a7", "a12", "a13",
                                     "a1", "a2", "a3", "a4", "a5",
                                     "a11", "a10", "a9"});
        this.comparePositions("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
                              new String[]{"a1", "a2", "a3", "a4", "a5", "a14", "a13",
                                           "a12", "a11", "a10", "a9", "a8", "a7", "a6"});
        // This needs to become more deterministic!
        // this.comparePositions("rings_fused_simple/pyridine.mol", "as1",
        //                       new String[]{"a1", "a5", "a6", "a7",
        //                                    "a12", "a13", "a14", "a15", "a16",
        //                                    "a11", "a10", "a9",
        //                                    "a17", "a18", "a19", "a20", "a8",
        //                                    "a4", "a3", "a2"
        //                       });
    }

    @Test
    public void fusedRingSimpleExtTest() {
        System.out.println("Testing simple fused rings with external substitutions...");
        this.comparePositions("rings_fused_simple_ext/fused_ext1.mol", "as1",
                              new String[]{"a8", "a7", "a12", "a13",
                                           "a1", "a2", "a3", "a4", "a5", 
                                           "a11", "a10", "a9"});
        this.comparePositions("rings_fused_simple_ext/fused_ext2.mol", "as1",
                              new String[]{"a13", "a12", "a7", "a8", "a9", "a10", "a11",
                                           "a5", "a4", "a3", "a2", "a1"});
    }
    
    @Test
    public void fusedRingComplexTest() {
        System.out.println("Testing complex fused rings with internal substitutions...");
        this.comparePositions("rings_fused_inner/ovalene.mol", "as1",
                        new String[]{"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                                     "a9", "a10", "a11", "a12", "a13", "a14", "a15",
                                     "a16", "a17", "a18", "a19", "a20", "a21", "a22"});
    }

    @Test
    public void essentialRingTest() {
        System.out.println("Testing order of essential rings in fused rings...");
        this.comparePaths("rings_fused_inner/ovalene.mol", "as1",
                          new String[]{"as2", "as3", "as4", "as5", "as6", "as7", "as8",
                                       "as9", "as10", "as11"});
        this.comparePaths("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
                          new String[]{"as4", "as2", "as3"});
        this.comparePaths("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
                          new String[]{"as2", "as3", "as4"});
        // This needs to become more deterministic!
        // Check wrt. largest subring. Then check wrt. smallest atom name.
        // this.comparePositions("rings_fused_simple/pyridine.mol", "as1",
        //new String[]{"as2", "as3", "as4", "as5", "as6"});
    }

    // @Test
    // public void moleculeTest() {
    //     System.out.println("Testing order of blocks in a molecule...");
    //     this.comparePaths("rings_fused_inner/ovalene.mol", "as1",
    //                       new String[]{"as2", "as3", "as4", "as5", "as6", "as7", "as8",
    //                                    "as9", "as10", "as11"});
    //     this.comparePaths("rings_fused_simple/1H-indeno[7,1-bc]azepine.mol", "as1",
    //                       new String[]{"as4", "as2", "as3"});
    //     this.comparePaths("rings_fused_simple/Pyrido[2,3-b]naphthalene.mol", "as1",
    //                       new String[]{"as2", "as3", "as4"});
        
    // }
}
