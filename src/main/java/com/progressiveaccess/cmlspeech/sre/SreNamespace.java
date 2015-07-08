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
 * @file   SreNamespace.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sat Feb 14 12:31:44 2015
 *
 * @brief  Namespace definitions for Sre.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

/**
 * Reference to the basic namespace for speech tags.
 */
public class SreNamespace {
  private final String uri = "http://www.chemaccess.org/sre-schema";
  private final String prefix = "sre";
  private static volatile SreNamespace instance = null;


  /**
   * @return The instance of the SreNamespace class.
   */
  public static SreNamespace getInstance() {
    if (instance == null) {
      instance = new SreNamespace();
    }
    return instance;
  }


  /**
   * @return The namespace URI.
   */
  public final String getUri() {
    return uri;
  }


  /**
   * @return The namespace prefix.
   */
  public final String getPrefix() {
    return prefix;
  }


  /**
   * Different Tags for the SRE speech annotations. Some notes;
   *
   * <p>
   * External bonds -- Bonds that are attached to a substructure but not part of
   * it.
   * </p>
   *
   * <p>
   * Connecting bonds -- Bonds that are external, but not internal to any other
   * structures, i.e. the truely connect it to another structure or atom.
   * </p>
   *
   * <p>
   * Connecting atoms -- Atoms that are shared with another structure.
   * </p>
   */
  public enum Tag {
    ANNOTATIONS("annotations"),
    ANNOTATION("annotation"),

    CONTEXT("context"),
    COMPONENT("component"),

    SUBSYSTEM("subSystem"),
    SUPERSYSTEM("superSystem"),

    INTERNALBONDS("internalBonds"),
    EXTERNALBONDS("externalBonds"),
    CONNECTINGBONDS("connectingBonds"),
    CONNECTINGATOMS("connectingAtoms"),

    ATOM("atom"),
    ATOMSET("atomSet"),
    BOND("bond"),
    UNKNOWN("unknown"),

    CONNECTIONS("connections"),
    BRIDGE("bridge"),
    SHAREDBOND("sharedBond"),
    BRIDGEATOM("bridgeAtom"),
    SHAREDATOM("sharedAtom"),
    SPIROATOM("spiroAtom"),
    CONNECTINGBOND("connectingBond"),

    DESCRIPTIONS("descriptions"),
    DESC("desc"),
    SUBDESC("subdesc"),
    CONTENT("content"),

    PARENTS("parents"),
    PARENT("parent"),
    CHILDREN("children"),
    CHILD("child"),
    NEIGHBOURS("neighbours"),
    NEIGHBOUR("neighbour"),
    POSITIONS("positions"),
    POSITION("position"),
    VIA("via");

    private final String tag;


    /**
     * Constructs an tag.
     *
     * @param tag
     *          The name of the tag.
     */
    private Tag(final String tag) {
      this.tag = "sre:" + tag;
    }


    /**
     * @return The tag name.
     */
    public final String getTag() {
      return this.tag;
    }

  }


  /**
   * Different Attributes for the SRE speech annotations.
   */
  public enum Attribute {
    LEVEL("level"),
    ELEMENTS("elements"),

    SPEECH("speech"),
    SPEECH2("speech2"),
    ORDER("order"),
    ATOM("bond"),
    BOND("bond"),
    LOCATION("location"),
    TYPE("type");
    
    private final String attribute;


    /**
     * Constructs an attribute.
     *
     * @param attribute
     *          The name of the attribute.
     */
    private Attribute(final String attribute) {
      this.attribute = attribute;
    }


    /**
     * @return The attribute name.
     */
    public final String getAttribute() {
      return this.attribute;
    }

  }

}
