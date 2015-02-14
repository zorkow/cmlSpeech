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
 * @file   StructuralEdge.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Sat Feb 14 12:34:38 2015
 * 
 * @brief  Edges for the structural graph.
 * 
 * 
 */

//
package io.github.egonw.graph;

import io.github.egonw.base.Cli;

import org.jgrapht.graph.DefaultEdge;

/**
 * Edges for the structural graph.
 * @extends DefaultEdge
 */

public class StructuralEdge extends DefaultEdge {
    
    private static final long serialVersionUID = 1L;

    private String label;
    private boolean shortBonds = false;

    public StructuralEdge(String label) {
        super();
        this.label = label;
        this.shortBonds = Cli.hasOption("vis_short");
    }

    public String toString() {
        if (this.shortBonds) {
            return this.label;
        }
        return "(" + this.getSource().toString() + " : " + 
            this.label + " : " + this.getTarget().toString() + ")";
    }
}
