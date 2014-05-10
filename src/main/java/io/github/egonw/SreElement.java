
//
package io.github.egonw;

import io.github.egonw.SreNamespace;
import nu.xom.Element;

/**
 *
 */

public class SreElement extends Element {

    SreElement(String tag) {
        super(SreNamespace.getInstance().prefix + tag,
              SreNamespace.getInstance().uri);
    }

}
