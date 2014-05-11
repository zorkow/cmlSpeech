
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


    /**
     * Different Tags for the SRE speech annotations.
     * Some notes;
     * 
     * External bonds -- Bonds that are attached to a substructure but not part
     *                   of it.
     *                   
     * Connecting bonds -- Bonds that are external, but not internal to any
     *                     other structures, i.e. the truely connect it to
     *                     another structure or atom.
     *                     
     * Connecting atoms -- Atoms that are shared with another structure.
     */
    public enum Tag {
        ANNOTATIONS ("annotations"),
        ANNOTATION ("annotation"),

        COMPONENT ("componentOf"),

        SUBSYSTEM ("subSystem"),
        SUPERSYSTEM ("superSystem"),
        
        INTERNALBONDS ("internalBonds"),
        EXTERNALBONDS ("externalBonds"),
        CONNECTINGATOMS ("connectingAtoms"),

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
