// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   IsoTable.java
 * @author Volker Sorge
 *          <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Aug  3 00:16:06 2015
 *
 * @brief  Complete iso-639 language code table.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * Lookup table for iso-639 language codes.
 */
public final class IsoTable {

  private static final Map<String, String> ISO_TABLE = new HashMap<>();

  static {
    ISO_TABLE.put("abkhaz", "ab");
    ISO_TABLE.put("afar", "aa");
    ISO_TABLE.put("afrikaans", "af");
    ISO_TABLE.put("akan", "ak");
    ISO_TABLE.put("albanian", "sq");
    ISO_TABLE.put("amharic", "am");
    ISO_TABLE.put("arabic", "ar");
    ISO_TABLE.put("aragonese", "an");
    ISO_TABLE.put("armenian", "hy");
    ISO_TABLE.put("assamese", "as");
    ISO_TABLE.put("avaric", "av");
    ISO_TABLE.put("avestan", "ae");
    ISO_TABLE.put("aymara", "ay");
    ISO_TABLE.put("azerbaijani", "az");
    ISO_TABLE.put("bambara", "bm");
    ISO_TABLE.put("bashkir", "ba");
    ISO_TABLE.put("basque", "eu");
    ISO_TABLE.put("belarusian", "be");
    ISO_TABLE.put("bengali", "bn");
    ISO_TABLE.put("bihari", "bh");
    ISO_TABLE.put("bislama", "bi");
    ISO_TABLE.put("bosnian", "bs");
    ISO_TABLE.put("breton", "br");
    ISO_TABLE.put("bulgarian", "bg");
    ISO_TABLE.put("burmese", "my");
    ISO_TABLE.put("catalan", "ca");
    ISO_TABLE.put("chamorro", "ch");
    ISO_TABLE.put("chechen", "ce");
    ISO_TABLE.put("nyanja", "ny");
    ISO_TABLE.put("chinese", "zh");
    ISO_TABLE.put("chuvash", "cv");
    ISO_TABLE.put("cornish", "kw");
    ISO_TABLE.put("corsican", "co");
    ISO_TABLE.put("cree", "cr");
    ISO_TABLE.put("croatian", "hr");
    ISO_TABLE.put("czech", "cs");
    ISO_TABLE.put("danish", "da");
    ISO_TABLE.put("divehi", "dv");
    ISO_TABLE.put("dutch", "nl");
    ISO_TABLE.put("dzongkha", "dz");
    ISO_TABLE.put("english", "en");
    ISO_TABLE.put("esperanto", "eo");
    ISO_TABLE.put("estonian", "et");
    ISO_TABLE.put("ewe", "ee");
    ISO_TABLE.put("faroese", "fo");
    ISO_TABLE.put("fijian", "fj");
    ISO_TABLE.put("finnish", "fi");
    ISO_TABLE.put("french", "fr");
    ISO_TABLE.put("fula", "ff");
    ISO_TABLE.put("galician", "gl");
    ISO_TABLE.put("georgian", "ka");
    ISO_TABLE.put("german", "de");
    ISO_TABLE.put("greek", "el");
    ISO_TABLE.put("guarani", "gn");
    ISO_TABLE.put("gujarati", "gu");
    ISO_TABLE.put("haitian", "ht");
    ISO_TABLE.put("hausa", "ha");
    ISO_TABLE.put("hebrew ", "he");
    ISO_TABLE.put("herero", "hz");
    ISO_TABLE.put("hindi", "hi");
    ISO_TABLE.put("hiri", "ho");
    ISO_TABLE.put("hungarian", "hu");
    ISO_TABLE.put("interlingua", "ia");
    ISO_TABLE.put("indonesian", "id");
    ISO_TABLE.put("interlingue", "ie");
    ISO_TABLE.put("irish", "ga");
    ISO_TABLE.put("igbo", "ig");
    ISO_TABLE.put("inupiaq", "ik");
    ISO_TABLE.put("ido", "io");
    ISO_TABLE.put("icelandic", "is");
    ISO_TABLE.put("italian", "it");
    ISO_TABLE.put("inuktitut", "iu");
    ISO_TABLE.put("japanese", "ja");
    ISO_TABLE.put("javanese", "jv");
    ISO_TABLE.put("kalaallisut", "kl");
    ISO_TABLE.put("kannada", "kn");
    ISO_TABLE.put("kanuri", "kr");
    ISO_TABLE.put("kashmiri", "ks");
    ISO_TABLE.put("kazakh", "kk");
    ISO_TABLE.put("khmer", "km");
    ISO_TABLE.put("kikuyu", "ki");
    ISO_TABLE.put("kinyarwanda", "rw");
    ISO_TABLE.put("kyrgyz", "ky");
    ISO_TABLE.put("komi", "kv");
    ISO_TABLE.put("kongo", "kg");
    ISO_TABLE.put("korean", "ko");
    ISO_TABLE.put("kurdish", "ku");
    ISO_TABLE.put("kwanyama", "kj");
    ISO_TABLE.put("latin", "la");
    ISO_TABLE.put("letzeburgesch", "lb");
    ISO_TABLE.put("ganda", "lg");
    ISO_TABLE.put("limburgish", "li");
    ISO_TABLE.put("lingala", "ln");
    ISO_TABLE.put("lao", "lo");
    ISO_TABLE.put("lithuanian", "lt");
    ISO_TABLE.put("luba-Katanga", "lu");
    ISO_TABLE.put("latvian", "lv");
    ISO_TABLE.put("manx", "gv");
    ISO_TABLE.put("macedonian", "mk");
    ISO_TABLE.put("malagasy", "mg");
    ISO_TABLE.put("malay", "ms");
    ISO_TABLE.put("malayalam", "ml");
    ISO_TABLE.put("maltese", "mt");
    ISO_TABLE.put("maori", "mi");
    ISO_TABLE.put("marathi", "mr");
    ISO_TABLE.put("marshallese", "mh");
    ISO_TABLE.put("mongolian", "mn");
    ISO_TABLE.put("nauru", "na");
    ISO_TABLE.put("navajo", "nv");
    ISO_TABLE.put("nndebele", "nd");
    ISO_TABLE.put("nepali", "ne");
    ISO_TABLE.put("ndonga", "ng");
    ISO_TABLE.put("bokmal", "nb");
    ISO_TABLE.put("nynorsk", "nn");
    ISO_TABLE.put("norwegian", "no");
    ISO_TABLE.put("nuosu", "ii");
    ISO_TABLE.put("sndebele", "nr");
    ISO_TABLE.put("occitan", "oc");
    ISO_TABLE.put("ojibwe", "oj");
    ISO_TABLE.put("slavonic", "cu");
    ISO_TABLE.put("oromo", "om");
    ISO_TABLE.put("oriya", "or");
    ISO_TABLE.put("ossetian", "os");
    ISO_TABLE.put("panjabi", "pa");
    ISO_TABLE.put("pali", "pi");
    ISO_TABLE.put("persian", "fa");
    ISO_TABLE.put("polish", "pl");
    ISO_TABLE.put("pashto", "ps");
    ISO_TABLE.put("portuguese", "pt");
    ISO_TABLE.put("quechua", "qu");
    ISO_TABLE.put("romansh", "rm");
    ISO_TABLE.put("kirundi", "rn");
    ISO_TABLE.put("romanian", "ro");
    ISO_TABLE.put("russian", "ru");
    ISO_TABLE.put("sanskrit", "sa");
    ISO_TABLE.put("sardinian", "sc");
    ISO_TABLE.put("sindhi", "sd");
    ISO_TABLE.put("sami", "se");
    ISO_TABLE.put("samoan", "sm");
    ISO_TABLE.put("sango", "sg");
    ISO_TABLE.put("serbian", "sr");
    ISO_TABLE.put("gaelic", "gd");
    ISO_TABLE.put("shona", "sn");
    ISO_TABLE.put("sinhala", "si");
    ISO_TABLE.put("slovak", "sk");
    ISO_TABLE.put("slovene", "sl");
    ISO_TABLE.put("somali", "so");
    ISO_TABLE.put("southern Sotho", "st");
    ISO_TABLE.put("spanish", "es");
    ISO_TABLE.put("sundanese", "su");
    ISO_TABLE.put("swahili", "sw");
    ISO_TABLE.put("swati", "ss");
    ISO_TABLE.put("swedish", "sv");
    ISO_TABLE.put("tamil", "ta");
    ISO_TABLE.put("telugu", "te");
    ISO_TABLE.put("tajik", "tg");
    ISO_TABLE.put("thai", "th");
    ISO_TABLE.put("tigrinya", "ti");
    ISO_TABLE.put("tibetan", "bo");
    ISO_TABLE.put("turkmen", "tk");
    ISO_TABLE.put("tagalog", "tl");
    ISO_TABLE.put("tswana", "tn");
    ISO_TABLE.put("tonga", "to");
    ISO_TABLE.put("turkish", "tr");
    ISO_TABLE.put("tsonga", "ts");
    ISO_TABLE.put("tatar", "tt");
    ISO_TABLE.put("twi", "tw");
    ISO_TABLE.put("tahitian", "ty");
    ISO_TABLE.put("uyghur", "ug");
    ISO_TABLE.put("ukrainian", "uk");
    ISO_TABLE.put("urdu", "ur");
    ISO_TABLE.put("uzbek", "uz");
    ISO_TABLE.put("venda", "ve");
    ISO_TABLE.put("vietnamese", "vi");
    ISO_TABLE.put("volapuk", "vo");
    ISO_TABLE.put("walloon", "wa");
    ISO_TABLE.put("welsh", "cy");
    ISO_TABLE.put("wolof", "wo");
    ISO_TABLE.put("frisian", "fy");
    ISO_TABLE.put("xhosa", "xh");
    ISO_TABLE.put("yiddish", "yi");
    ISO_TABLE.put("yoruba", "yo");
    ISO_TABLE.put("zhuang", "za");
    ISO_TABLE.put("zulu", "zu");
  }


  /** Dummy constructor. */
  private IsoTable() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Retrieves the Iso code for a given language. If the language does not
   * exist, Iso code for English is returned.
   *
   * @param language
   *          The given language.
   *
   * @return The Iso code of the language or en.
   */
  public static String lookup(final String language) {
    if (language == null) {
      return "en";
    }
    String lang = language.toLowerCase();
    if (ISO_TABLE.containsKey(lang)) {
      return ISO_TABLE.get(lang);
    }
    if (ISO_TABLE.containsValue(lang)) {
      return lang;
    }
    return "en";
  }


  /**
   * Checks if a given language exists.
   *
   * @param language
   *          The given language.
   *
   * @return True if the language or its Iso code is in the table.
   */
  public static Boolean exists(final String language) {
    String lang = language.toLowerCase();
    return ISO_TABLE.containsKey(lang) || ISO_TABLE.containsValue(lang);
  }


  private static final String PKG = "com.progressiveaccess.cmlspeech.speech";
  private static final String CLASS = "AtomTable";


  /**
   * Checks if a given language is implemented.
   *
   * @param iso
   *          The iso code for the given language.
   *
   * @return True if the language is implemented.
   */
  public static Boolean implemented(final String iso) {
    try {
      Class.forName(PKG + "." + iso + "." + CLASS);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  
  /** 
   * @return A list of iso names for all existing languages.
   */
  public static PriorityQueue<String> existing() {
    PriorityQueue<String> result = new PriorityQueue<String>();
    Integer index = 0;
    for (String language : ISO_TABLE.values()) {
      if (IsoTable.implemented(language)) {
      result.add(language);
      }
    }
    return result;
  }
  
}
