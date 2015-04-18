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
 * @brief  Callable class for Cactus Futures.
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import com.progressiveaccess.cmlspeech.base.Logger;
import com.progressiveaccess.cmlspeech.sre.SreAttribute;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Callables for Cactus Futures.
 */
public class CactusCallable implements Callable<String> {

  private final String id;
  private final CactusType type;
  private final IAtomContainer container;
  private final Consumer<String> setter;

  /**
   * Constructs a thread for calls to Cactus web service.
   *
   * @param id
   *          Name of the thread.
   * @param setter
   *          A function consuming a string.
   * @param type
   *          Type of expected result.
   * @param container
   *          Atom container for query.
   */
  public CactusCallable(final String id, final Consumer<String> setter,
      final CactusType type, final IAtomContainer container) {
    super();
    this.setter = setter;
    this.id = id;
    this.type = type;
    this.container = container;
  }


  /**
   * @return The id of the callable thread.
   */
  public String getId() {
    return this.id;
  }


  @Override
  public String call() throws CactusException {
    Logger.logging("Executing Cactus call for " + this.id +
                   " " + this.type.getTag());
    final String result = this.type.getCaller().apply(this.container);
    //this.setter.accept(result);
    return result;
  }


  public Consumer<String> getSetter() {
    return this.setter;
  }

}
