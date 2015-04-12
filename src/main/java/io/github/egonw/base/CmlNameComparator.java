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
 * @file   CMLNameComparator.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Wed Jun 11 12:19:41 2014
 * 
 * @brief  A comparator for CML naming conventions.
 * 
 * 
 */

//

package io.github.egonw.base;

import java.util.Comparator;

/**
 * Compares CML names, promoting atoms over bonds over atomsets.
 */

public class CmlNameComparator implements Comparator<String> {

  @Override
  public int compare(String name1, String name2) {
    String reg1 = "[0-9]*";
    String alpha1 = name1.replaceAll(reg1, "");
    String alpha2 = name2.replaceAll(reg1, "");
    if (alpha1.equals(alpha2)) {
      String reg2 = "[a-z]*";
      Integer numer1 = Integer.parseInt(name1.replaceAll(reg2, ""));
      Integer numer2 = Integer.parseInt(name2.replaceAll(reg2, ""));
      if (numer1 == numer2) {
        return 0;
      }
      if (numer1 < numer2) {
        return -1;
      }
      return 1;
    }
    if (alpha1.equals("as") && alpha2.equals("b")) {
      return 1;
    }
    if (alpha1.equals("b") && alpha2.equals("as")) {
      return -1;
    }
    return alpha1.compareTo(alpha2);
  }

}
