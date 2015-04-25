
//
package com.progressiveaccess.cmlspeech.sre;

/**
 *
 */

public interface XmlVisitable {

  default void accept(XmlVisitor visitor) {
      visitor.visit(this);
  }
}
