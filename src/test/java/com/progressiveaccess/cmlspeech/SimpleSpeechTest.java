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
 * @file StructureTest.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jul  8 16:11:07 2015
 *
 * @brief Functional tests for structure generation of some standard molecules.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech;

/**
 * Functional test for simple speech annotations.
 */

public class SimpleSpeechTest extends AnnotationTest {

  /**
   * Create the test case.
   *
   * @param testName
   *          Name of the test case.
   */
  public SimpleSpeechTest(final String testName) {
    super(testName);
  }


  @Override
  public String[] getParameters() {
    final String[] parameters = {"-ao", "-t", "-r0", "-nn", "-nh"};
    return parameters;
  }


  @Override
  public String expectedDirectory() {
    return "simpleSpeech";
  }

}
