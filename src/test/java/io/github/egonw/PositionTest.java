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

import java.nio.file.Paths;
import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.base.CMLEnricher;
import io.github.egonw.structure.RichAtomSet;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

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
        assertArrayEquals(actual.toArray(), order);
    }

    @Test
    public void chainTests() {
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
}



