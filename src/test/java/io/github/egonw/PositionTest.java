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

/**
 * A basic environment to run position tests.
 */

public class PositionTest {

    private static String testSources = "src/main/resources";
    
    public PositionTest(String input, String[] order) {
        Paths.get(PositionTest.testSources, input);
    }
}
