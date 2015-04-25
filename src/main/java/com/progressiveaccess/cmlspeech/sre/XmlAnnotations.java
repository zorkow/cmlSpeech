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
 * @file   XMLAnnotations.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Thu Apr  9 12:23:40 2015
 *
 * @brief  Interface for XML annotations output.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

/**
 * The basic XML annotations every enriched object should produce.
 */

public interface XmlAnnotations {

  SreNamespace.Tag tag();

}
