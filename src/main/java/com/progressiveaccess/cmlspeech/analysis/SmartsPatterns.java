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

package com.progressiveaccess.cmlspeech.analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton class to hold smart patterns. These patterns are currently loaded
 * from files given in hard coded pathnames.
 */
public final class SmartsPatterns {

  private static volatile SmartsPatterns instance = null;
  private static final String SMARTS_PATH = "src/main/resources/smarts";
  private static final String[] SMARTS_FILES = {
    "daylight-pattern.txt",
    "smarts-pattern.txt"
  };
  private static final Map<String, String> SMARTS_PATTERNS =
      new HashMap<String, String>();
  private static boolean loaded = false;


  /**
   * Constructor for smarts patterns.
   */
  private SmartsPatterns() {
    if (!loaded) {
      SmartsPatterns.loadSmartsFiles();
      loaded = true;
    }
  }


  /**
   * @return The only instance of the class.
   */
  private static SmartsPatterns getInstance() {
    if (instance == null) {
      instance = new SmartsPatterns();
    }
    return instance;
  }


  /**
   * @return The set of patterns.
   */
  public static Set<Map.Entry<String, String>> getPatterns() {
    getInstance();
    return SMARTS_PATTERNS.entrySet();
  }


  /**
   * Loads the smarts patterns from the given.
   */
  private static void loadSmartsFiles() {
    for (final String file : SmartsPatterns.SMARTS_FILES) {
      loadSmartsFile(file);
    }
  }


  /**
   * Loads a single smarts patterns file.
   *
   * @param file
   *          The name of the file to load.
   */
  private static void loadSmartsFile(final String file) {
    loadSmartsFile(FileSystems.getDefault().getPath(SMARTS_PATH, file));
  }


  /**
   * Loads a single smarts patterns file.
   *
   * @param file
   *          The name of the file to load.
   */
  private static void loadSmartsFile(final Path file) {
    try {
      final BufferedReader br = Files.newBufferedReader(file,
          StandardCharsets.UTF_8);
      String line;
      while ((line = br.readLine()) != null) {
        final int colonIndex = line.indexOf(":");
        // Checks that the line has a colon in it and if it is one of
        // the patterns to be skipped (notated by a '#' before the name
        // in the file
        if (colonIndex != -1 && line.charAt(0) != '#') {
          SMARTS_PATTERNS.put(line.substring(0, colonIndex),
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
