
//
package io.github.egonw;

/**
 *
 */

public class SreNamespace {
    public final String uri = "http://www.chemaccess.org/sre-schema";
    public final String prefix = "sre";

    private static volatile SreNamespace instance = null;

    protected SreNamespace() {
    }

    public static SreNamespace getInstance() {
        if(instance == null) {
            instance = new SreNamespace();
        }
        return instance;
   }
}
