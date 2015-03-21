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
 * @file   RichIsolatedRing.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Tue Feb 24 17:13:29 2015
 * 
 * @brief  Implementation of rich isolated ring.
 * 
 * 
 */

//
package io.github.egonw.structure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.AtomicProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.connection.Connection;
import io.github.egonw.connection.ConnectionType;
import org.openscience.cdk.interfaces.IBond;
import com.google.common.collect.Lists;
import java.util.SortedSet;


/**
 * Atom sets that are rich isolated rings.
 */

public class RichIsolatedRing extends RichRing {

    public RichIsolatedRing(IAtomContainer container, String id) {
        super(container, id, RichSetType.ISOLATED);
    }


    protected final void walk() {
        super.walk();
    }

}
