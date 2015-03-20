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
 * @file   RichRing.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Fri Mar 20 21:57:45 2015
 * 
 * @brief  An abstract class for ring structures.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Set;
import org.openscience.cdk.interfaces.IAtom;
import com.google.common.collect.Sets;

/**
 *
 */

public abstract class RichRing extends RichAtomSet {
    
    protected Set<IAtom> rim = null;

    public RichRing (IAtomContainer container, String id, RichSetType type) {
        super(container, id, type);
        this.rim = Sets.newHashSet(container.atoms());
    }

    public static boolean isRing(RichAtomSet atomSet) {
        return true;
    }
}
