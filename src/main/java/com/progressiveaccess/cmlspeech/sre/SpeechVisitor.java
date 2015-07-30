
//
package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.structure.ComponentsPositions;

/**
 *
 */

public interface SpeechVisitor extends XmlVisitor {

  public void setContextPositions(final ComponentsPositions positions);

  public String getSpeech();
  
}
