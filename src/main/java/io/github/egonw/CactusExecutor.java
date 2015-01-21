/**
 * @file   CactusExector.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Apr 28 01:41:35 2014
 * 
 * @brief  Class for multi-threaded Cactus call.
 * 
 */


//
package io.github.egonw;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
 *
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
