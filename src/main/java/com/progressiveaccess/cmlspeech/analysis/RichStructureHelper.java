package com.progressiveaccess.cmlspeech.analysis;

import com.progressiveaccess.cmlspeech.base.CmlNameComparator;
import com.progressiveaccess.cmlspeech.structure.RichAtom;
import com.progressiveaccess.cmlspeech.structure.RichAtomSet;
import com.progressiveaccess.cmlspeech.structure.RichBond;
import com.progressiveaccess.cmlspeech.structure.RichMolecule;
import com.progressiveaccess.cmlspeech.structure.RichStructure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/** Utility class that holds useful mappings for rich chemical objects. */
public final class RichStructureHelper {

  /** Dummy constructor. */
  private RichStructureHelper() {
    throw new AssertionError("Instantiating utility class...");
  }


  private static RichMolecule richMolecule;
  private static SortedMap<String, RichAtom> richAtoms;
  private static SortedMap<String, RichBond> richBonds;
  private static SortedMap<String, RichAtomSet> richAtomSets;


  /** Initialises and re-initialises helper structure. */
  public static void init() {
    RichStructureHelper.richMolecule = null;
    RichStructureHelper.richAtoms = new TreeMap<>(new CmlNameComparator());
    RichStructureHelper.richBonds = new TreeMap<>(new CmlNameComparator());
    RichStructureHelper.richAtomSets = new TreeMap<>(new CmlNameComparator());
  }


  /**
   * Predicate to check if an id represents a rich atom.
   *
   * @param id Name of a rich structure.
   *
   * @return True if id represents an atom.
   */
  public static boolean isAtom(final String id) {
    return RichStructureHelper.richAtoms.containsKey(id);
  }


  /**
   * Predicate to check if an id represents a rich bond.
   *
   * @param id Name of a rich structure.
   *
   * @return True if id represents an bond.
   */
  public static boolean isBond(final String id) {
    return RichStructureHelper.richBonds.containsKey(id);
  }


  /**
   * Predicate to check if an id represents a rich atom set.
   *
   * @param id Name of a rich structure.
   *
   * @return True if id represents an atom set.
   */
  public static boolean isAtomSet(final String id) {
    return RichStructureHelper.richAtomSets.containsKey(id);
  }


  /**
   * Predicate to check if an id represents the rich molecule.
   *
   * @param id Name of a rich structure.
   *
   * @return True if id represents the molecule.
   */
  public static boolean isMolecule(final String id) {
    return id.equals(RichStructureHelper.richMolecule.getId());
  }


  /**
   * @return The rich molecule.
   */
  public static RichMolecule getRichMolecule() {
    return RichStructureHelper.richMolecule;
  }


  /**
   * Creates and registers a rich molecule from a chemical object.
   *
   * @param molecule The chemical object.
   */
  public static void setRichMolecule(final RichMolecule molecule) {
    RichStructureHelper.richMolecule = molecule;
  }


  /**
   * Returns the rich atom for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich atom if it exists.
   */
  public static RichAtom getRichAtom(final String id) {
    return RichStructureHelper.richAtoms.get(id);
  }


  /**
   * Creates and registers a rich atom from a chemical object.
   *
   * @param atom The chemical object.
   */
  public static void setRichAtom(final IAtom atom) {
    RichStructureHelper.richAtoms.put(atom.getID(), new RichAtom(atom));
  }


  /**
   * Returns the rich bond for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich bond if it exists.
   */
  public static RichBond getRichBond(final String id) {
    return RichStructureHelper.richBonds.get(id);
  }


  /**
   * Creates and registers a rich bond from a chemical object.
   *
   * @param bond The chemical object.
   */
  public static void setRichBond(final IBond bond) {
    RichStructureHelper.richBonds.put(bond.getID(), new RichBond(bond));
  }


  /**
   * Returns the rich atom set for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich atom set if it exists.
   */
  public static RichAtomSet getRichAtomSet(final String id) {
    return RichStructureHelper.richAtomSets.get(id);
  }


  /**
   * Registers an already existing rich atom set.
   *
   * @param atomSet The chemical object.
   */
  public static void setRichAtomSet(final RichAtomSet atomSet) {
    RichStructureHelper.richAtomSets.put(atomSet.getId(), atomSet);
  }


  /**
   * Returns the rich structure for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich structure if it exists.
   */
  public static RichStructure<?> getRichStructure(final String id) {
    RichStructure<?> structure = RichStructureHelper.richAtoms.get(id);
    if (structure != null) {
      return structure;
    }
    structure = RichStructureHelper.richBonds.get(id);
    if (structure != null) {
      return structure;
    }
    return RichStructureHelper.richAtomSets.get(id);
  }


  /**
   * @return List of rich atoms.
   */
  public static List<RichAtom> getAtoms() {
    return new ArrayList<RichAtom>(RichStructureHelper.richAtoms.values());
  }


  /**
   * @return List of rich bonds.
   */
  public static List<RichBond> getBonds() {
    return new ArrayList<RichBond>(RichStructureHelper.richBonds.values());
  }


  /**
   * @return List of rich atom sets.
   */
  public static List<RichAtomSet> getAtomSets() {
    return new ArrayList<RichAtomSet>(
        RichStructureHelper.richAtomSets.values());
  }


  /**
   * @return Set of atoms ids.
   */
  public static Set<String> getAtomIds() {
    return RichStructureHelper.richAtoms.keySet();
  }


  /**
   * @return Set of bonds ids.
   */
  public static Set<String> getBondIds() {
    return RichStructureHelper.richBonds.keySet();
  }


  /**
   * @return Set of atom set ids.
   */
  public static Set<String> getAtomSetIds() {
    return RichStructureHelper.richAtomSets.keySet();
  }

}
