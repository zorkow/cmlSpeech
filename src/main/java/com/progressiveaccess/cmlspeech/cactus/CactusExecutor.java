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
 * @file   CactusExector.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Apr 28 01:41:35 2014
 *
 * @brief  Class for multi-threaded Cactus call.
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import com.progressiveaccess.cmlspeech.base.Logger;
import com.progressiveaccess.cmlspeech.sre.SreAttribute;
import com.progressiveaccess.cmlspeech.sre.SreUtil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import nu.xom.Document;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Executes Cactus calls and collects their results.
 */

public class CactusExecutor {

  /** Pool of callables for Cactus. */
  private final List<CactusCallable> pool = new ArrayList<>();
  /** Registry for futures expecting results from Cactus calls. */
  private final Multimap<CactusCallable, Future<String>> registry =
      HashMultimap.create();
  /** The executor service that runs the callables. */
  private ExecutorService executor;


  /**
   * Register callables for cactus in the pool.
   *
   * @param callable
   *          A callable to register.
   */
  public void register(final CactusCallable callable) {
    this.pool.add(callable);
  }


  /** Execute all callables currently in the pool. */
  public void execute() {
    this.executor = Executors.newFixedThreadPool(this.pool.size());
    for (final CactusCallable callable : this.pool) {
      final Future<String> future = this.executor.submit(callable);
      this.registry.put(callable, future);
    }
  }


  /**
   * Adds attributes from returned by all current Cactus futures to a document.
   *
   * @param doc
   *          The current document.
   */
  public void addResults(final Document doc) {
    for (final Map.Entry<CactusCallable, Future<String>> entry : this.registry
        .entries()) {
      final Consumer<String> consumer = entry.getKey().getSetter();
      final Future<String> future = entry.getValue();
      try {
        consumer.accept(future.get());
      } catch (final Throwable e) {
        Logger.error("Cactus Error: " + e.getMessage() + "\n");
        continue;
      }
    }
  }


  /** Shut down the Cactus executor. */
  public void shutdown() {
    this.executor.shutdown();
  }

}
