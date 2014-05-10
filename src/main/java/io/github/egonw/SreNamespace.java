
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

    public enum Tag {
        ANNOTATIONS ("annotations"),
        ANNOTATION ("annotation"),

        COMPONENT ("componentOf"),

        SUBSYSTEM ("subSystem"),
        SUPERSYSTEM ("superSystem"),
        
        INTERNALBONDS ("internalBonds"),
        EXTERNALBONDS ("externalBonds"),
        EXTERNALATOMS ("externalAtoms"),

        BOND ("bond"),
        ATOM ("atom"),
        ATOMSET ("atomSet"),

        DESCRIPTIONS ("descriptions"),
        DESCRIPTION ("description");

        public final String tag;
        
        private Tag (String tag) {
            this.tag = "sre:" + tag;
        }

    }

}
