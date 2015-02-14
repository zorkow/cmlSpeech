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
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Apr 28 01:41:35 2014
 * 
 * @brief  Class for multi-threaded Cactus call.
 * 
 */


//
package io.github.egonw.cactus;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.github.egonw.base.Logger;
import io.github.egonw.sre.SreAttribute;
import io.github.egonw.sre.SreUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;


/**
 * Executes Cactus calls and collects their results.
 */

public class CactusExecutor {
    
    /** Pool of callables for Cactus. */
    private List<CactusCallable> pool = new ArrayList<>();
    /** Registry for futures expecting results from Cactus calls. */
    private Multimap<String, Future<SreAttribute>> registry = HashMultimap.create();
    private ExecutorService executor;
        
    /**
     * Register callables for cactus in the pool.
     * @param callable A callable to register.
     */
    public void register(CactusCallable callable) {
        pool.add(callable);
    }

    /** Execute all callables currently in the pool. */
    public void execute() {
        this.executor = Executors.newFixedThreadPool(pool.size());
        for (CactusCallable callable : pool) {
            Future<SreAttribute> future = executor.submit(callable);
            this.registry.put(callable.id, future);
        }
    }

    /**
     * Adds attributes from returned by all current Cactus futures to a document.
     * @param doc The current document.
     */
    public void addResults(Document doc) {
        for (Map.Entry<String, Future<SreAttribute>> entry : this.registry.entries()) {
            String id = entry.getKey();
            Future<SreAttribute> future = entry.getValue();
            try {
                Element element = SreUtil.getElementById(doc, id);
                SreAttribute result = future.get();
                element.addAttribute(result);
            }
            catch (Throwable e) {
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
