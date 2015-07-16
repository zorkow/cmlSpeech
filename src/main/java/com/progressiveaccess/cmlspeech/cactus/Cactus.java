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
 * @file   Cactus.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun May  4 13:22:37 2014
 *
 * @brief  Utility class to communicate with Cactus.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.XPather;
import org.htmlcleaner.XPatherException;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlSerializer;
import org.htmlcleaner.PrettyHtmlSerializer;

/**
 * Utility functions to call the NIH Cactus chemical structure identifier
 * service.
 */
public final class Cactus {

  /** Dummy constructor. */
  private Cactus() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Send a call to the Cactus web service.
   *
   * @param input
   *          String with input structure.
   * @param output
   *          Output format.
   *
   * @return Result if any.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  private static List<String> getCactus(final String input, final String output)
      throws CactusException {
    URL url = null;
    BufferedReader br = null;
    final List<String> lines = new ArrayList<>();
    try {
      url = new URL("http://cactus.nci.nih.gov/chemical/structure/" + input
          + "/" + output);
      br = new BufferedReader(new InputStreamReader(url.openStream()));
      while (br.ready()) {
        lines.add(br.readLine());
      }
    } catch (final FileNotFoundException e) {
      throw new CactusException("No result for " + url);
    } catch (final MalformedURLException e) {
      throw new CactusException("Can't make URL from input " + input + " "
          + output);
    } catch (final IOException e) {
      throw new CactusException("IO exception when translating " + url
          + "\n" + e);
    }
    return lines;
  }

  /**
   * Translates a molecule to Inchi format.
   *
   * @param molecule
   *          The input molecule.
   * @return String containing molecule in Inchi format.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  private static String translate(final IAtomContainer molecule)
      throws CactusException {
    try {
      final InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
      final InChIGenerator gen = factory.getInChIGenerator(molecule);
      return (gen.getInchi());
    } catch (final CDKException e) {
      throw new CactusException("Problems loading CDK Factory.");
    }
  }

  /**
   * Compute IUPAC name for molecule.
   *
   * @param molecule
   *          Input molecule.
   * @return The IUPAC name if it exists.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  public static String getIupac(final IAtomContainer molecule)
      throws CactusException {
    final String inchi = Cactus.translate(molecule);
    return Cactus.getCactus(inchi, "IUPAC_Name").get(0);
  }

  /**
   * Compute chemical formula for molecule.
   *
   * @param molecule
   *          Input molecule.
   * @return The chemical formula.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  public static String getFormula(final IAtomContainer molecule)
      throws CactusException {
    final String inchi = Cactus.translate(molecule);
    return Cactus.getCactus(inchi, "formula").get(0);
  }

  /**
   * Compute common name for molecule.
   *
   * @param molecule
   *          Input molecule.
   * @return Common name if it exists.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  public static String getName(final IAtomContainer molecule)
      throws CactusException {
    final String inchi = Cactus.translate(molecule);
    final List<String> names = Cactus.getCactus(inchi, "Names");
    final List<String> alpha = names.stream()
        .filter(line -> line.matches("^[a-zA-Z- ]+$"))
        .collect(Collectors.toList());
    if (alpha.isEmpty()) {
      return names.get(0);
    }
    return alpha.get(0);
  }


  
  /**
   * Send a call to the Cactus web service.
   *
   * @param input
   *          String with input structure.
   * @param output
   *          Output format.
   *
   * @return Result if any.
   *
   * @throws CactusException
   *          If error in Cactus call occurs.
   */
  private static List<String> getSpider(final String input)
      throws CactusException {
    URL url = null;
    // BufferedReader br = null;
    Document br = null;
    Builder builder = new Builder();

    // create an instance of HtmlCleaner
    HtmlCleaner cleaner = new HtmlCleaner();
    // take default cleaner properties
    CleanerProperties props = cleaner.getProperties();
    List<? extends TagNode> obj;
    
    final List<String> lines = new ArrayList<>();
    try {
      url = new URL("http://www.chemspider.com/Search.aspx?q=" + input);
      
      TagNode node = cleaner.clean(url.openStream());
      HtmlSerializer xs = new PrettyHtmlSerializer(props, "  ");

      XPather xp = new XPather("//h1[@class='h4']");
      obj = node.getElementListByAttValue("class", "syn", true, true);
      //br = builder.build(xml);

      for (TagNode ob : obj) {
        String xml = xs.getAsString(ob);
        br = builder.build(xml, "");
        System.out.println(br.getRootElement().toXML());
      }

      String name = node.findElementByAttValue("class", "h4", true, true).getText().toString().trim();
      System.out.println(name);
      
      
      // br = new BufferedReader(new InputStreamReader(url.openStream()));
      // while (br.ready()) {
      //   lines.add(br.readLine());
      // }
    } catch (final FileNotFoundException e) {
      throw new CactusException("No result for " + url);
    } catch (final MalformedURLException e) {
      throw new CactusException("Can't make URL from input " + input);
    } catch (final IOException e) {
      throw new CactusException("IO exception when translating " + url
          + "\n" + e);
    }
    catch (final ParsingException e) {
      throw new CactusException("Parsing exception when translating " + url
          + "\n" + e);
    }
    // catch (final XPatherException e) {
    //   throw new CactusException("Parsing exception when translating " + url
    //       + "\n" + e);
    // }
    // System.out.println(lines);
    return lines;
  }

  public static void callSpider(final IAtomContainer molecule) {
    final String inchi = Cactus.translate(molecule);
    Cactus.getSpider(inchi);
  }
  
}
