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

import com.progressiveaccess.cmlspeech.sre.SreAttribute;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.concurrent.Callable;

/**
 * Callables for Cactus Futures.
 */
public class CactusCallable implements Callable<SreAttribute> {

  private String id = "";
  private final CactusType type;
  private IAtomContainer container = null;


  /**
   * Constructs a thread for calls to Cactus web service.
   *
   * @param id
   *          Name of the thread.
   * @param type
   *          Type of expected result.
   * @param container
   *          Atom container for query.
   */
  public CactusCallable(final String id, final CactusType type,
      final IAtomContainer container) {
    super();
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
  public SreAttribute call() throws CactusException {
    final String result = this.type.getCaller().apply(this.container);
    return new SreAttribute(this.type.getTag(), result);
  }

}
