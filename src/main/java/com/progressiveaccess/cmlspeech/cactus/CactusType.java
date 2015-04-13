
//

package com.progressiveaccess.cmlspeech.cactus;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.function.Function;

/**
 * The types of calls to the Cactus web service.
 */
public enum CactusType {

  IUPAC("iupac", Cactus::getIupac),
  NAME("name", Cactus::getName),
  FORMULA("formula", Cactus::getFormula);

  private final String tag;
  private final Function<IAtomContainer, String> caller;


  /**
   * Enum type for different translations via Cactus with two parameters.
   *
   * @param tag
   *          String for tag.
   * @param caller
   *          Closure with call to Cactus for that tag.
   */
  private CactusType(final String tag, final Function<IAtomContainer,
               String> caller) {
    this.caller = caller;
    this.tag = tag;
  }


  /**
   * @return The tag of the type.
   */
  public String getTag() {
    return this.tag;
  }


  /**
   * @return The caller function associated with the type.
   */
  public Function<IAtomContainer, String> getCaller() {
    return this.caller;
  }

}
