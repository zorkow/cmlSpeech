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
 * @file   EnglishAtomTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Fri Jun 20 02:00:25 2014
 *
 * @brief  Singleton class to translate atom names in English.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.sre;


/**
 * Maps atom identifiers to their proper names.
 */

public final class EnglishAtomTable extends AbstractAtomTable {

  private static final long serialVersionUID = 1L;

  public EnglishAtomTable() {
    this.put("Ac", "Actinium");
    this.put("Al", "Aluminum");
    this.put("Am", "Americium");
    this.put("Sb", "Antimony");
    this.put("Ar", "Argon");
    this.put("As", "Arsenic");
    this.put("At", "Astatine");
    this.put("Ba", "Barium");
    this.put("Bk", "Berkelium");
    this.put("Be", "Beryllium");
    this.put("Bi", "Bismuth");
    this.put("Bh", "Bohrium");
    this.put("B", "Boron");
    this.put("Br", "Bromine");
    this.put("Cd", "Cadmium");
    this.put("Ca", "Calcium");
    this.put("Cf", "Californium");
    this.put("C", "Carbon");
    this.put("Ce", "Cerium");
    this.put("Cs", "Cesium");
    this.put("Cl", "Chlorine");
    this.put("Cr", "Chromium");
    this.put("Co", "Cobalt");
    this.put("Cu", "Copper");
    this.put("Cm", "Curium");
    this.put("Ds", "Darmstadtium");
    this.put("Db", "Dubnium");
    this.put("Dy", "Dysprosium");
    this.put("Es", "Einsteinium");
    this.put("Er", "Erbium");
    this.put("Eu", "Europium");
    this.put("Fm", "Fermium");
    this.put("F", "Fluorine");
    this.put("Fr", "Francium");
    this.put("Gd", "Gadolinium");
    this.put("Ga", "Gallium");
    this.put("Ge", "Germanium");
    this.put("Au", "Gold");
    this.put("Hf", "Hafnium");
    this.put("Hs", "Hassium");
    this.put("He", "Helium");
    this.put("Ho", "Holmium");
    this.put("H", "Hydrogen");
    this.put("In", "Indium");
    this.put("I", "Iodine");
    this.put("Ir", "Iridium");
    this.put("Fe", "Iron");
    this.put("Kr", "Krypton");
    this.put("La", "Lanthanum");
    this.put("Lr", "Lawrencium");
    this.put("Pb", "Lead");
    this.put("Li", "Lithium");
    this.put("Lu", "Lutetium");
    this.put("Mg", "Magnesium");
    this.put("Mn", "Manganese");
    this.put("Mt", "Meitnerium");
    this.put("Md", "Mendelevium");
    this.put("Hg", "Mercury");
    this.put("Mo", "Molybdenum");
    this.put("Nd", "Neodymium");
    this.put("Ne", "Neon");
    this.put("Np", "Neptunium");
    this.put("Ni", "Nickel");
    this.put("Nb", "Niobium");
    this.put("N", "Nitrogen");
    this.put("No", "Nobelium");
    this.put("Os", "Osmium");
    this.put("O", "Oxygen");
    this.put("Pd", "Palladium");
    this.put("P", "Phosphorus");
    this.put("Pt", "Platinum");
    this.put("Pu", "Plutonium");
    this.put("Po", "Polonium");
    this.put("K", "Potassium");
    this.put("Pr", "Praseodymium");
    this.put("Pm", "Promethium");
    this.put("Pa", "Protactinium");
    this.put("Ra", "Radium");
    this.put("Rn", "Radon");
    this.put("Re", "Rhenium");
    this.put("Rh", "Rhodium");
    this.put("Rb", "Rubidium");
    this.put("Ru", "Ruthenium");
    this.put("Rf", "Rutherfordium");
    this.put("Sm", "Samarium");
    this.put("Sc", "Scandium");
    this.put("Sg", "Seaborgium");
    this.put("Se", "Selenium");
    this.put("Si", "Silicon");
    this.put("Ag", "Silver");
    this.put("Na", "Sodium");
    this.put("Sr", "Strontium");
    this.put("S", "Sulfur");
    this.put("Ta", "Tantalum");
    this.put("Tc", "Technetium");
    this.put("Te", "Tellurium");
    this.put("Tb", "Terbium");
    this.put("Tl", "Thallium");
    this.put("Th", "Thorium");
    this.put("Tm", "Thulium");
    this.put("Sn", "Tin");
    this.put("Ti", "Titanium");
    this.put("W", "Tungsten");
    this.put("Uub", "Ununbium");
    this.put("Uuh", "Ununhexium");
    this.put("Uuo", "Ununoctium");
    this.put("Uup", "Ununpentium");
    this.put("Uuq", "Ununquadium");
    this.put("Uus", "Ununseptium");
    this.put("Uut", "Ununtrium");
    this.put("Uuu", "Ununium");
    this.put("U", "Uranium");
    this.put("V", "Vanadium");
    this.put("Xe", "Xenon");
    this.put("Yb", "Ytterbium");
    this.put("Y", "Yttrium");
    this.put("Zn", "Zinc");
    this.put("Zr", "Zirconium");
  }

}
