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
 * @file   App.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:05:04 2015
 *
 * @brief  Basic application file for project.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.base;

/**
 * The application class.
 */
public final class App {

  /** Dummy constructor. */
  private App() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * The main class.
   *
   * @param args
   *          The arguments from the command line.
   *
   * @throws Exception
   *          Any run-time error.
   */
  public static void main(final String[] args) throws Exception {
    Cli.init(args);
    Logger.start();
    for (final String file : Cli.getFiles()) {
      final CmlEnricher cmle = new CmlEnricher(file);
      cmle.enrichFile();
    }
    Logger.end();
  }
}
