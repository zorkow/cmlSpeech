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
 * @file   SpiderExector.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Tue Jul 21 20:12:00 2015
 *
 * @brief  Class for multi-threaded Spider call.
 *
 */

//

package com.progressiveaccess.cmlspeech.cactus;

import com.progressiveaccess.cmlspeech.base.Cli;
import com.progressiveaccess.cmlspeech.base.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Executes Spider calls and collects their results.
 */

public class SpiderExecutor {

  /** Pool of callables for Spider. */
  private final List<SpiderCallable> pool = new ArrayList<>();
  /** Registry for futures expecting results from Spider calls. */
  private final Multimap<SpiderCallable, Future<SpiderNames>> registry =
      HashMultimap.create();
  /** The executor service that runs the callables. */
  private ExecutorService executor;


  /**
   * Register callables for cactus in the pool.
   *
   * @param callable
   *          A callable to register.
   */
  public void register(final SpiderCallable callable) {
    this.pool.add(callable);
  }


  /** Execute all callables currently in the pool. */
  public void execute() {
    this.executor = Executors.newFixedThreadPool(this.pool.size());
    Integer time = null;
    if (Cli.hasOption("time_nih")) {
      try {
        time = Integer.parseInt(Cli.getOptionValue("time_nih"));
      } catch (NumberFormatException e) {
        Logger.error("Spider Error: Illegal time format.\n");
      }
    }
    for (final SpiderCallable callable : this.pool) {
      final Future<SpiderNames> future = this.executor.submit(callable);
      this.registry.put(callable, future);
      System.out.println(callable.getId());
      if (time != null) {
        try {
          Thread.sleep(time);
        } catch (final Throwable e) {
          Logger.error("Spider Error: " + e.getMessage() + "\n");
        }
      }
    }
  }


  /**
   * Adds attributes from returned by all current Spider futures to a document.
   */
  public void addResults() {
    System.out.println("Adding results!");
    for (final Map.Entry<SpiderCallable, Future<SpiderNames>> entry
           : this.registry.entries()) {
      final Future<SpiderNames> future = entry.getValue();
      try {
        System.out.println(future.get());
      } catch (final Throwable e) {
        Logger.error("Spider Error: " + e.getMessage() + "\n");
        continue;
      }
    }
  }


  /** Shut down the Spider executor. */
  public void shutdown() {
    this.executor.shutdown();
  }

}
