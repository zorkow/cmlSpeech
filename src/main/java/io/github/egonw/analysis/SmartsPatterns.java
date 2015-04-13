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
 * @file   SmartsPatterns.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Feb 24 00:55:58 2015
 *
 * @brief  Loads and tests Smarts patterns.
 *
 *
 */

//

package io.github.egonw.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton class to hold smart patterns. These patterns are currently loaded
 * from files given in hard coded pathnames.
 */
public class SmartsPatterns {

  private static volatile SmartsPatterns instance = null;
  private static String[] smartsFiles = {
    "src/main/resources/smarts/daylight-pattern.txt",
  "src/main/resources/smarts/smarts-pattern.txt" };
  private static Map<String, String> smartsPatterns = new HashMap<String, String>();
  private static boolean loaded = false;

  protected SmartsPatterns() {
    if (!loaded) {
      SmartsPatterns.loadSmartsFiles();
      loaded = true;
    }
  }

  private static SmartsPatterns getInstance() {
    if (instance == null) {
      instance = new SmartsPatterns();
    }
    return instance;
  }

  public static Set<Map.Entry<String, String>> getPatterns() {
    getInstance();
    return smartsPatterns.entrySet();
  }

  private static void loadSmartsFiles() {
    for (final String file : SmartsPatterns.smartsFiles) {
      loadSmartsFile(file);
    }
  }

  private static void loadSmartsFile(final String file) {
    try {
      final BufferedReader br = new BufferedReader(new FileReader(
          new File(file)));
      String line;
      while ((line = br.readLine()) != null) {
        final int colonIndex = line.indexOf(":");
        // Checks that the line has a colon in it and if it is one of
        // the patterns to be skipped (notated by a '#' before the name
        // in the file
        if (colonIndex != -1 && line.charAt(0) != '#') {
          smartsPatterns.put(line.substring(0, colonIndex),
              line.substring(colonIndex + 2));
        }
      }
      br.close();
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

}
