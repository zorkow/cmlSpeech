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

package io.github.egonw.base;

public class App {

  public static void main(String[] args) throws Exception {
    Cli.init(args);
    Logger.start();
    for (String file : Cli.getFiles()) {
      CMLEnricher cmle = new CMLEnricher();
      cmle.enrichFile(file);
    }
    Logger.end();
  }
}
