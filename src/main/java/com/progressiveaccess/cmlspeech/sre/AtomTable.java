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
 * @file   AtomTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Fri Jun 20 02:00:25 2014
 *
 * @brief  Singleton class to translate atom names.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;

import com.progressiveaccess.cmlspeech.structure.RichAtom;

import org.openscience.cdk.interfaces.IAtom;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps atom identifiers to their proper names.
 */

public final class AtomTable {

  private static final Map<String, String> ATOM_MAP;

  static {
    ATOM_MAP = new HashMap<String, String>();
    ATOM_MAP.put("Ac", "Actinium");
    ATOM_MAP.put("Al", "Aluminum");
    ATOM_MAP.put("Am", "Americium");
    ATOM_MAP.put("Sb", "Antimony");
    ATOM_MAP.put("Ar", "Argon");
    ATOM_MAP.put("As", "Arsenic");
    ATOM_MAP.put("At", "Astatine");
    ATOM_MAP.put("Ba", "Barium");
    ATOM_MAP.put("Bk", "Berkelium");
    ATOM_MAP.put("Be", "Beryllium");
    ATOM_MAP.put("Bi", "Bismuth");
    ATOM_MAP.put("Bh", "Bohrium");
    ATOM_MAP.put("B", "Boron");
    ATOM_MAP.put("Br", "Bromine");
    ATOM_MAP.put("Cd", "Cadmium");
    ATOM_MAP.put("Ca", "Calcium");
    ATOM_MAP.put("Cf", "Californium");
    ATOM_MAP.put("C", "Carbon");
    ATOM_MAP.put("Ce", "Cerium");
    ATOM_MAP.put("Cs", "Cesium");
    ATOM_MAP.put("Cl", "Chlorine");
    ATOM_MAP.put("Cr", "Chromium");
    ATOM_MAP.put("Co", "Cobalt");
    ATOM_MAP.put("Cu", "Copper");
    ATOM_MAP.put("Cm", "Curium");
    ATOM_MAP.put("Ds", "Darmstadtium");
    ATOM_MAP.put("Db", "Dubnium");
    ATOM_MAP.put("Dy", "Dysprosium");
    ATOM_MAP.put("Es", "Einsteinium");
    ATOM_MAP.put("Er", "Erbium");
    ATOM_MAP.put("Eu", "Europium");
    ATOM_MAP.put("Fm", "Fermium");
    ATOM_MAP.put("F", "Fluorine");
    ATOM_MAP.put("Fr", "Francium");
    ATOM_MAP.put("Gd", "Gadolinium");
    ATOM_MAP.put("Ga", "Gallium");
    ATOM_MAP.put("Ge", "Germanium");
    ATOM_MAP.put("Au", "Gold");
    ATOM_MAP.put("Hf", "Hafnium");
    ATOM_MAP.put("Hs", "Hassium");
    ATOM_MAP.put("He", "Helium");
    ATOM_MAP.put("Ho", "Holmium");
    ATOM_MAP.put("H", "Hydrogen");
    ATOM_MAP.put("In", "Indium");
    ATOM_MAP.put("I", "Iodine");
    ATOM_MAP.put("Ir", "Iridium");
    ATOM_MAP.put("Fe", "Iron");
    ATOM_MAP.put("Kr", "Krypton");
    ATOM_MAP.put("La", "Lanthanum");
    ATOM_MAP.put("Lr", "Lawrencium");
    ATOM_MAP.put("Pb", "Lead");
    ATOM_MAP.put("Li", "Lithium");
    ATOM_MAP.put("Lu", "Lutetium");
    ATOM_MAP.put("Mg", "Magnesium");
    ATOM_MAP.put("Mn", "Manganese");
    ATOM_MAP.put("Mt", "Meitnerium");
    ATOM_MAP.put("Md", "Mendelevium");
    ATOM_MAP.put("Hg", "Mercury");
    ATOM_MAP.put("Mo", "Molybdenum");
    ATOM_MAP.put("Nd", "Neodymium");
    ATOM_MAP.put("Ne", "Neon");
    ATOM_MAP.put("Np", "Neptunium");
    ATOM_MAP.put("Ni", "Nickel");
    ATOM_MAP.put("Nb", "Niobium");
    ATOM_MAP.put("N", "Nitrogen");
    ATOM_MAP.put("No", "Nobelium");
    ATOM_MAP.put("Os", "Osmium");
    ATOM_MAP.put("O", "Oxygen");
    ATOM_MAP.put("Pd", "Palladium");
    ATOM_MAP.put("P", "Phosphorus");
    ATOM_MAP.put("Pt", "Platinum");
    ATOM_MAP.put("Pu", "Plutonium");
    ATOM_MAP.put("Po", "Polonium");
    ATOM_MAP.put("K", "Potassium");
    ATOM_MAP.put("Pr", "Praseodymium");
    ATOM_MAP.put("Pm", "Promethium");
    ATOM_MAP.put("Pa", "Protactinium");
    ATOM_MAP.put("Ra", "Radium");
    ATOM_MAP.put("Rn", "Radon");
    ATOM_MAP.put("Re", "Rhenium");
    ATOM_MAP.put("Rh", "Rhodium");
    ATOM_MAP.put("Rb", "Rubidium");
    ATOM_MAP.put("Ru", "Ruthenium");
    ATOM_MAP.put("Rf", "Rutherfordium");
    ATOM_MAP.put("Sm", "Samarium");
    ATOM_MAP.put("Sc", "Scandium");
    ATOM_MAP.put("Sg", "Seaborgium");
    ATOM_MAP.put("Se", "Selenium");
    ATOM_MAP.put("Si", "Silicon");
    ATOM_MAP.put("Ag", "Silver");
    ATOM_MAP.put("Na", "Sodium");
    ATOM_MAP.put("Sr", "Strontium");
    ATOM_MAP.put("S", "Sulfur");
    ATOM_MAP.put("Ta", "Tantalum");
    ATOM_MAP.put("Tc", "Technetium");
    ATOM_MAP.put("Te", "Tellurium");
    ATOM_MAP.put("Tb", "Terbium");
    ATOM_MAP.put("Tl", "Thallium");
    ATOM_MAP.put("Th", "Thorium");
    ATOM_MAP.put("Tm", "Thulium");
    ATOM_MAP.put("Sn", "Tin");
    ATOM_MAP.put("Ti", "Titanium");
    ATOM_MAP.put("W", "Tungsten");
    ATOM_MAP.put("Uub", "Ununbium");
    ATOM_MAP.put("Uuh", "Ununhexium");
    ATOM_MAP.put("Uuo", "Ununoctium");
    ATOM_MAP.put("Uup", "Ununpentium");
    ATOM_MAP.put("Uuq", "Ununquadium");
    ATOM_MAP.put("Uus", "Ununseptium");
    ATOM_MAP.put("Uut", "Ununtrium");
    ATOM_MAP.put("Uuu", "Ununium");
    ATOM_MAP.put("U", "Uranium");
    ATOM_MAP.put("V", "Vanadium");
    ATOM_MAP.put("Xe", "Xenon");
    ATOM_MAP.put("Yb", "Ytterbium");
    ATOM_MAP.put("Y", "Yttrium");
    ATOM_MAP.put("Zn", "Zinc");
    ATOM_MAP.put("Zr", "Zirconium");
  }


  /** Dummy constructor. */
  private AtomTable() {
    throw new AssertionError("Instantiating utility class...");
  }


  /**
   * Gets the name of an atom given its chemical symbol.
   *
   * @param name
   *          The atom symbol.
   *
   * @return The atom name.
   */
  public static String lookup(final String name) {
    final String result = ATOM_MAP.get(name);
    if (result == null) {
      return "";
    }
    return result;
  }


  /**
   * Gets the name of an atom.
   *
   * @param atom
   *          The atom.
   *
   * @return The atom name.
   */
  public static String lookup(final IAtom atom) {
    return lookup(atom.getSymbol());
  }


  /**
   * Gets the name of an rich atom.
   *
   * @param atom
   *          The rich atom.
   *
   * @return The atom name.
   */
  public static String lookup(final RichAtom atom) {
    return lookup(atom.getStructure());
  }

}
