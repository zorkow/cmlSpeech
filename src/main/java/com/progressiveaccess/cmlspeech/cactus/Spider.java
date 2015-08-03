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
 * @file   Spider.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jul 21 20:12:00 2015
 *
 * @brief  Utility class to handle calls to Chem Spider searches.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;


import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Utility functions to call the Chem Spider Web search.
 */
public final class Spider {

  private static final HtmlCleaner CLEANER = new HtmlCleaner();

  /** Dummy constructor. */
  private Spider() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Send a call to the Cactus web service.
   *
   * @param input
   *          String with input structure.
   * @param names
   *          The naming structure.
   *
   * @return List of names if any.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  private static SpiderNames getNames(final String input,
                                      final SpiderNames names)
      throws CactusException {
    URL url = null;
    try {
      url = new URL("http://www.chemspider.com/Search.aspx?q=" + input);
      TagNode node = Spider.CLEANER.clean(url.openStream());
      Spider.getName(node, names);
      Spider.getSynonyms(node, names);
    } catch (final FileNotFoundException e) {
      throw new CactusException("No result for " + url);
    } catch (final MalformedURLException e) {
      throw new CactusException("Can't make URL from input " + input);
    } catch (final IOException e) {
      throw new CactusException("IO exception when translating " + url
          + "\n" + e);
    }
    return names;
  }


  /**
   * Get all names for a molecule from ChemSpider.
   *
   * @param molecule
   *          The molecule to name.
   * @param names
   *          The naming structure.
   *
   * @return The name structure.
   */
  public static SpiderNames getNames(final IAtomContainer molecule,
                                     final SpiderNames names) {
    final String inchi = Cactus.translate(molecule);
    return Spider.getNames(inchi, names);
  }


  /**
   * Inserts the moleculename from the result page into the naming structure.
   *
   * @param node
   *          The page resulting from the search.
   * @param names
   *          The naming structure.
   */
  private static void getName(final TagNode node, final SpiderNames names) {
    TagNode nameNode = node.findElementByAttValue("class", "h4", true, true);
    if (nameNode == null) {
      return;
    }
    SpiderName name = new SpiderName(Spider.getContent(nameNode),
                                     "name", "English");
    System.out.println("Adding: " + name.toString());
    names.add(name);
  }


  /**
   * Inserts all synonyms into the naming structure.
   *
   * @param node
   *          The page resulting from the search.
   * @param names
   *          The naming structure.
   */
  private static void getSynonyms(final TagNode node, final SpiderNames names) {
    List<? extends TagNode> obj =
        node.getElementListByAttValue("class", "syn", true, true);
    for (TagNode ob : obj) {
      Spider.getSynonym(ob, names);
    }
  }


  /**
   * Inserts a single synonym into the naming structure.
   *
   * @param node
   *          A synonym node.
   * @param names
   *          The naming structure.
   */
  private static void getSynonym(final TagNode node, final SpiderNames names) {
    String name = Spider.getContent(node.getChildTagList().iterator().next());
    String language = Spider.getLanguage(node);
    String[] sources = Spider.getSources(node);
    String type = Spider.getType(sources, language);
    if (sources.length == 0) {
      names.add(new SpiderName(name, type, language));
    } else {
      names.add(new SpiderName(name, type, language, sources));
    }
  }


  /**
   * Rertrieves the language for a synonym name if it exists.
   *
   * @param node
   *          A synonym node.
   *
   * @return The language argument if it exists.
   */
  private static String getLanguage(final TagNode node) {
    TagNode language = node.findElementByAttValue(
        "class", "synonym_language", true, false);
    return language == null ? "" : Spider.getContent(language);
  }


  /**
   * Rertrieves the sources for a synonym name if there are any.
   *
   * @param node
   *          A synonym node.
   *
   * @return The array of sources.
   */
  private static String[] getSources(final TagNode node) {
    List<? extends TagNode> obj =
        node.getElementListByAttValue("class", "synonym_ref", true, false);
    String[] sources = new String[obj.size()];
    for (int i = 0; i < obj.size(); i++) {
      sources[i] = Spider.getContent(obj.get(i));
    }
    return sources;
  }


  /**
   * Rertrieves the type for a synonym name if it exists.
   *
   * @param sources
   *          An array of sources.
   * @param language
   *          The language of the synonym if known.
   *
   * @return The type is either iupac, wiki, name or empty.
   */
  private static String getType(final String[] sources, final String language) {
    boolean wiki = false;
    for (String source : sources) {
      if (source.matches(".*IUPAC.*")) {
        return "iupac";
      }
      if (source.matches(".*Wiki.*")) {
        wiki = true;
      }
    }
    return (language != "") ? "name" : wiki ? "wiki" : "";
  }


  /**
   * Retrieves and cleans the text content of a node.
   *
   * @param node
   *          The HTML node.
   *
   * @return The text content with outer whitespaces trimmed and intial and
   *      final square brackets removed, if there are any.
   */
  private static String getContent(final TagNode node) {
    String content = node.getText().toString().trim();
    while (content.matches("^\\[.+")) {
      content = content.substring(1);
    }
    Integer length = content.length();
    while (content.matches(".+\\]$")) {
      content = content.substring(0, --length);
    }
    return content;
  }

}
