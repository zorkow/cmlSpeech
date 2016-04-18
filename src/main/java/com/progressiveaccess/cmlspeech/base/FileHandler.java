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
 * @file   FileHandler.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Feb 26 19:06:25 2015
 *
 * @brief  File handler utility functions.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.base;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nux.xom.pool.XOMUtil;

import org.apache.commons.io.FilenameUtils;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.xmlcml.cml.base.CMLBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for handling CML files and other chem file formats.
 */

public final class FileHandler {

  /** Dummy constructor. */
  private FileHandler() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Loads current file into the molecule IAtomContainer.
   *
   * @param fileName
   *          File to load.
   *
   * @return The molecule loaded.
   *
   * @throws IOException
   *           Problems with loading file.
   * @throws CDKException
   *           Problems with input file format.
   */
  public static IAtomContainer readFile(final String fileName)
      throws IOException,
  CDKException {
    final InputStream file = new BufferedInputStream(new FileInputStream(
        fileName));
    final ISimpleChemObjectReader reader = new ReaderFactory()
        .createReader(file);
    IChemFile cmlFile = null;
    cmlFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(
        IChemFile.class));
    reader.close();
    final IAtomContainer molecule = ChemFileManipulator.getAllAtomContainers(
        cmlFile)
        .get(0);
    Logger.logging(molecule);
    return molecule;
  }


  /**
   * Builds the CML XOM element.
   *
   * @param molecule
   *          The molecule to rewritten.
   *
   * @return The CML document.
   *
   * @throws IOException
   *           Problems with StringWriter
   * @throws CDKException
   *           Problems with CMLWriter
   * @throws ParsingException
   *           Problems with building CML XOM.
   */
  public static Document buildXom(final IAtomContainer molecule)
      throws IOException,
  CDKException, ParsingException {
    final StringWriter outStr = new StringWriter();
    final CMLWriter cmlwriter = new CMLWriter(outStr);
    cmlwriter.write(molecule);
    cmlwriter.close();
    final String cmlcode = outStr.toString();

    final Builder builder = new CMLBuilder();
    // this.doc.getRootElement().addNamespaceDeclaration
    // ("cml", "http://www.xml-cml.org/schema");
    final Document doc = builder.build(cmlcode, "");
    Logger.logging(doc.toXML());
    return doc;
  }


  /**
   * Writes a document to a CML file.
   *
   * @param doc
   *          The output document.
   * @param fileName
   *          The base filename.
   *
   * @throws IOException
   *           Problems with opening output file.
   * @throws CDKException
   *           Problems with writing the CML XOM.
   */
  public static void writeFile(final Document doc, final String fileName)
      throws IOException, CDKException {
    final OutputStream outFile = new BufferedOutputStream(new FileOutputStream(
        fileName + ".cml"));
    final PrintWriter output = new PrintWriter(outFile);
    output.write(XOMUtil.toPrettyXML(doc));
    output.flush();
    output.close();
  }


  /**
   * Writes a document to a CML file.
   *
   * @param doc
   *          The output document.
   * @param fileName
   *          The base filename.
   * @param extension
   *          The additional extension.
   */
  public static void writeXom(final Document doc, final String fileName,
                              final String extension) {
    final String basename = FilenameUtils.getBaseName(fileName);
    final String file = basename + "-" + extension;
    try {
      FileHandler.writeFile(doc, file);
    } catch (final IOException e) {
      Logger.error("IO error: Can't write file " + file + "\n");
      e.printStackTrace();
    } catch (final CDKException e) {
      Logger.error("Not a valid CDK structure to write: " + e.getMessage()
          + "\n");
      e.printStackTrace();
    }
  }

}
