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
 * @file   Logger.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:06:23 2015
 *
 * @brief  Logger facilities for logging and error output.
 *
 *
 */

//

package io.github.egonw.base;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Logger facilities:
 *
 * <p>
 * Error logging is either to file or stderr. Message logging is either to file
 * or stdout.
 * </p>
 *
 */
public class Logger {
  private static Boolean debug = false;
  private static Boolean verbose = false;
  private static PrintWriter logFile = new PrintWriter(System.out);
  private static PrintWriter errFile = new PrintWriter(System.err);

  protected Logger() {
  }

  public static void start() {
    debug = Cli.hasOption("d");
    verbose = Cli.hasOption("v");
    openLogfile("l", (final PrintWriter stream) -> {
      logFile = stream;
    });
    openLogfile("x", (final PrintWriter stream) -> {
      errFile = stream;
    });
  }

  private static void openLogfile(final String optionName,
      final Consumer<PrintWriter> logFile) {
    if (!Cli.hasOption(optionName)) {
      return;
    }
    final String fileName = Cli.getOptionValue(optionName);
    final File file = new File(fileName);
    try {
      logFile.accept(new PrintWriter(file));
    } catch (final IOException e) {
      System.err.println("Error: Can't open logfile' " + fileName);
    }
  }

  public static void error(final Object str) {
    if (debug) {
      Logger.errFile.print(str);
    }
  }

  public static void logging(final Object str) {
    if (verbose) {
      Logger.logFile.print(str);
    }
  }

  public static void end() {
    Logger.errFile.close();
    Logger.logFile.close();
  }
}
