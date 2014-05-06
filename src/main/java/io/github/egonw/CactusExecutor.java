
//
package io.github.egonw;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import nu.xom.Document;
import java.util.stream.Collectors;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import nu.xom.Element;
import nu.xom.Nodes;


/**
 *
 */

public class CactusExecutor {
    
    private static List<CactusCallable> pool = new ArrayList<>();
    private static Multimap<String, Future<SreAttribute>> registry = HashMultimap.create();
    private static ExecutorService executor;
        
    public static void register(CactusCallable callable) {
        pool.add(callable);
    }

    public void execute() {
        this.executor = Executors.newFixedThreadPool(pool.size());
        for (CactusCallable callable : pool) {
            Future<SreAttribute> future = executor.submit(callable);
            registry.put(callable.id, future);
        }
    }

    public static void addResults(Document doc, Logger logger) {
        for (Map.Entry<String, Future<SreAttribute>> entry : registry.entries()) {
            String id = entry.getKey();
            Future<SreAttribute> future = entry.getValue();
            try {
                Element element = SreUtil.getElementById(doc, id);
                SreAttribute result = future.get();
                element.addAttribute(result);
            }
            catch (Throwable e) {
                logger.error("Cactus Error: " + e.getMessage());
                continue;
            }
        }
    }

    public void shutdown() {
        this.executor.shutdown();
    }

}
