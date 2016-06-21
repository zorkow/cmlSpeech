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
 * @file   SpiderName.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jul 21 20:12:00 2015
 *
 * @brief Class to hold different one name of a structure and associated
 *        administrative information.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import java.util.Arrays;

/**
 * Data structure to hold structure names computed from a web service.
 */
public class SpiderName {

  private String name = "";
  private String type = "";
  private String language = "";
  private String[] sources = {"CS"};

  /**
   * Constructor.
   *
   * @param name
   *          The name of the structure.
   * @param type
   *          The type of name, e.g., name, iupac.
   * @param language
   *          The language of the name if known.
   * @param sources
   *          The sources for the name.
   */
  public SpiderName(final String name, final String type,
                    final String language, final String[] sources) {
    this(name, type, language);
    this.sources = sources;
  }


  /**
   * Constructor.
   *
   * @param name
   *          The name of the structure.
   * @param type
   *          The type of name, e.g., name, iupac.
   */
  public SpiderName(final String name, final String type) {
    this(name, type, "");
  }


  /**
   * Constructor.
   *
   * @param name
   *          The name of the structure.
   * @param type
   *          The type of name, e.g., name, iupac.
   * @param sources
   *          The sources for the name.
   */
  public SpiderName(final String name, final String type,
                    final String[] sources) {
    this(name, type, "", sources);
  }


  /**
   * Constructor.
   *
   * @param name
   *          The name of the structure.
   * @param type
   *          The type of name, e.g., name, iupac.
   * @param language
   *          The language of the name if known.
   */
  public SpiderName(final String name, final String type,
                    final String language) {
    this.name = name;
    this.type = type;
    this.language = language;
  }


  /**
   * @return A name of the structure.
   */
  public final String getName() {
    return this.name;
  }


  /**
   * @return A type of the structure.
   */
  public final String getType() {
    return this.type;
  }


  /**
   * @return A language of the structure.
   */
  public final String getLanguage() {
    return this.language;
  }


  /**
   * @return A sources of the structure.
   */
  public final String[] getSources() {
    return this.sources;
  }


  @Override
  public final String toString() {
    return this.name + " (" + this.type + "," + this.language + ") "
      + Arrays.toString(this.sources);
  }

}
