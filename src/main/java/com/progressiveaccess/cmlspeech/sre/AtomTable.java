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

public class AtomTable {

  private static volatile AtomTable instance = null;
  private static final Map<String, String> atomMap;

  static {
    atomMap = new HashMap<String, String>();
    atomMap.put("Ac", "Actinium");
    atomMap.put("Al", "Aluminum");
    atomMap.put("Am", "Americium");
    atomMap.put("Sb", "Antimony");
    atomMap.put("Ar", "Argon");
    atomMap.put("As", "Arsenic");
    atomMap.put("At", "Astatine");
    atomMap.put("Ba", "Barium");
    atomMap.put("Bk", "Berkelium");
    atomMap.put("Be", "Beryllium");
    atomMap.put("Bi", "Bismuth");
    atomMap.put("Bh", "Bohrium");
    atomMap.put("B", "Boron");
    atomMap.put("Br", "Bromine");
    atomMap.put("Cd", "Cadmium");
    atomMap.put("Ca", "Calcium");
    atomMap.put("Cf", "Californium");
    atomMap.put("C", "Carbon");
    atomMap.put("Ce", "Cerium");
    atomMap.put("Cs", "Cesium");
    atomMap.put("Cl", "Chlorine");
    atomMap.put("Cr", "Chromium");
    atomMap.put("Co", "Cobalt");
    atomMap.put("Cu", "Copper");
    atomMap.put("Cm", "Curium");
    atomMap.put("Ds", "Darmstadtium");
    atomMap.put("Db", "Dubnium");
    atomMap.put("Dy", "Dysprosium");
    atomMap.put("Es", "Einsteinium");
    atomMap.put("Er", "Erbium");
    atomMap.put("Eu", "Europium");
    atomMap.put("Fm", "Fermium");
    atomMap.put("F", "Fluorine");
    atomMap.put("Fr", "Francium");
    atomMap.put("Gd", "Gadolinium");
    atomMap.put("Ga", "Gallium");
    atomMap.put("Ge", "Germanium");
    atomMap.put("Au", "Gold");
    atomMap.put("Hf", "Hafnium");
    atomMap.put("Hs", "Hassium");
    atomMap.put("He", "Helium");
    atomMap.put("Ho", "Holmium");
    atomMap.put("H", "Hydrogen");
    atomMap.put("In", "Indium");
    atomMap.put("I", "Iodine");
    atomMap.put("Ir", "Iridium");
    atomMap.put("Fe", "Iron");
    atomMap.put("Kr", "Krypton");
    atomMap.put("La", "Lanthanum");
    atomMap.put("Lr", "Lawrencium");
    atomMap.put("Pb", "Lead");
    atomMap.put("Li", "Lithium");
    atomMap.put("Lu", "Lutetium");
    atomMap.put("Mg", "Magnesium");
    atomMap.put("Mn", "Manganese");
    atomMap.put("Mt", "Meitnerium");
    atomMap.put("Md", "Mendelevium");
    atomMap.put("Hg", "Mercury");
    atomMap.put("Mo", "Molybdenum");
    atomMap.put("Nd", "Neodymium");
    atomMap.put("Ne", "Neon");
    atomMap.put("Np", "Neptunium");
    atomMap.put("Ni", "Nickel");
    atomMap.put("Nb", "Niobium");
    atomMap.put("N", "Nitrogen");
    atomMap.put("No", "Nobelium");
    atomMap.put("Os", "Osmium");
    atomMap.put("O", "Oxygen");
    atomMap.put("Pd", "Palladium");
    atomMap.put("P", "Phosphorus");
    atomMap.put("Pt", "Platinum");
    atomMap.put("Pu", "Plutonium");
    atomMap.put("Po", "Polonium");
    atomMap.put("K", "Potassium");
    atomMap.put("Pr", "Praseodymium");
    atomMap.put("Pm", "Promethium");
    atomMap.put("Pa", "Protactinium");
    atomMap.put("Ra", "Radium");
    atomMap.put("Rn", "Radon");
    atomMap.put("Re", "Rhenium");
    atomMap.put("Rh", "Rhodium");
    atomMap.put("Rb", "Rubidium");
    atomMap.put("Ru", "Ruthenium");
    atomMap.put("Rf", "Rutherfordium");
    atomMap.put("Sm", "Samarium");
    atomMap.put("Sc", "Scandium");
    atomMap.put("Sg", "Seaborgium");
    atomMap.put("Se", "Selenium");
    atomMap.put("Si", "Silicon");
    atomMap.put("Ag", "Silver");
    atomMap.put("Na", "Sodium");
    atomMap.put("Sr", "Strontium");
    atomMap.put("S", "Sulfur");
    atomMap.put("Ta", "Tantalum");
    atomMap.put("Tc", "Technetium");
    atomMap.put("Te", "Tellurium");
    atomMap.put("Tb", "Terbium");
    atomMap.put("Tl", "Thallium");
    atomMap.put("Th", "Thorium");
    atomMap.put("Tm", "Thulium");
    atomMap.put("Sn", "Tin");
    atomMap.put("Ti", "Titanium");
    atomMap.put("W", "Tungsten");
    atomMap.put("Uub", "Ununbium");
    atomMap.put("Uuh", "Ununhexium");
    atomMap.put("Uuo", "Ununoctium");
    atomMap.put("Uup", "Ununpentium");
    atomMap.put("Uuq", "Ununquadium");
    atomMap.put("Uus", "Ununseptium");
    atomMap.put("Uut", "Ununtrium");
    atomMap.put("Uuu", "Ununium");
    atomMap.put("U", "Uranium");
    atomMap.put("V", "Vanadium");
    atomMap.put("Xe", "Xenon");
    atomMap.put("Yb", "Ytterbium");
    atomMap.put("Y", "Yttrium");
    atomMap.put("Zn", "Zinc");
    atomMap.put("Zr", "Zirconium");
  }

  protected AtomTable() {
  }

  public static AtomTable getInstance() {
    if (instance == null) {
      instance = new AtomTable();
    }
    return instance;
  }

  public static String lookup(final String name) {
    final String result = atomMap.get(name);
    if (result == null) {
      return "";
    }
    return result;
  }

  public static String lookup(final IAtom atom) {
    return lookup(atom.getSymbol());
  }

  public static String lookup(final RichAtom atom) {
    return lookup(atom.getStructure());
  }

}
