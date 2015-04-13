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
 * @file   TypeComparator.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Mon Aug  4 19:39:56 2014
 *
 * @brief Rich Structure comparison methods wrt. molecular weight of atom sets.
 *
 */

//

package io.github.egonw.analysis;

import io.github.egonw.structure.RichAtom;
import io.github.egonw.structure.RichAtomSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.AtomicProperties;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.IOException;

/**
 * Comparison of atom sets with respect to their molecular weight.
 */

public class WeightComparator extends DefaultComparator {

  @Override
  public int compare(final RichAtomSet set1, final RichAtomSet set2) {
    final IAtomContainer container1 = set1.getStructure();
    final IAtomContainer container2 = set2.getStructure();
    final double weightA = AtomContainerManipulator
        .getNaturalExactMass(container1);
    final double weightB = AtomContainerManipulator
        .getNaturalExactMass(container2);

    return (int) Math.signum(weightB - weightA);
  }

  @Override
  public int compare(final RichAtom atom1, final RichAtom atom2) {
    try {
      final double weightA = AtomicProperties.getInstance().getMass(
          atom1.getStructure().getSymbol());
      final double weightB = AtomicProperties.getInstance().getMass(
          atom2.getStructure().getSymbol());

      return (int) Math.signum(weightB - weightA);
    } catch (final IOException e) {
      return 0;
    }
  }
}
