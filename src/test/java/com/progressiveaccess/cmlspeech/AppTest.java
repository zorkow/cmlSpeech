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
 * @file   AppTest.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Apr 19 23:06:45 2015
 *
 * @brief  Run trivial App test.
 *
 *
 */

package com.progressiveaccess.cmlspeech;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

  /**
   * Create the test case.
   *
   * @param testName
   *          Name of the test case.
   */
  public AppTest(final String testName) {
    super(testName);
  }


  /**
   * @return The suite of tests being tested.
   */
  public static Test suite() {
    return new TestSuite(AppTest.class);
  }


  /**
   * Rigourous Test. :-)
   */
  public void testApp() {
    assertTrue(true);
  }

}
