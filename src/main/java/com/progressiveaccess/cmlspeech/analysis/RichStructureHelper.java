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
import java.util.SortedMap;
import java.util.TreeMap;

/** Utility class that holds useful mappings for rich chemical objects. */
public final class RichStructureHelper {

  /** Dummy constructor. */
  private RichStructureHelper() {
    throw new AssertionError("Instantiating utility class...");
  }


  public static RichMolecule richMolecule;
  public static SortedMap<String, RichStructure<?>> richAtoms;
  public static SortedMap<String, RichStructure<?>> richBonds;
  public static SortedMap<String, RichStructure<?>> richAtomSets;


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
   * Returns the rich atom for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich atom if it exists.
   */
  public static RichAtom getRichAtom(final String id) {
    return (RichAtom) RichStructureHelper.richAtoms.get(id);
  }


  /**
   * Creates and registers a rich atom from a chemical object.
   *
   * @param atom The chemical object.
   *
   * @return The newly created rich atom.
   */
  public static RichStructure<?> setRichAtom(final IAtom atom) {
    return RichStructureHelper.setRichStructure(RichStructureHelper.richAtoms,
        atom.getID(), new RichAtom(atom));
  }


  /**
   * Returns the rich bond for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich bond if it exists.
   */
  public static RichBond getRichBond(final String id) {
    return (RichBond) RichStructureHelper.richBonds.get(id);
  }


  /**
   * Creates and registers a rich bond from a chemical object.
   *
   * @param bond The chemical object.
   *
   * @return The newly created rich bond.
   */
  public static RichStructure<?> setRichBond(final IBond bond) {
    return RichStructureHelper.setRichStructure(RichStructureHelper.richBonds,
        bond.getID(), new RichBond(bond));
  }


  /**
   * Returns the rich atom set for the given id.
   *
   * @param id Name of a rich structure.
   *
   * @return The rich atom set if it exists.
   */
  public static RichAtomSet getRichAtomSet(final String id) {
    return (RichAtomSet) RichStructureHelper.richAtomSets.get(id);
  }


  /**
   * Creates and registers a rich atom set from a chemical object.
   *
   * @param atomSet The chemical object.
   *
   * @return The newly created rich atom set.
   */
  public static RichAtomSet setRichAtomSet(final RichAtomSet atomSet) {
    return (RichAtomSet) RichStructureHelper.setRichStructure(
        RichStructureHelper.richAtomSets, atomSet.getId(), atomSet);
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
   * Inserts a rich structure into the provided rich structure mapping.
   *
   * @param map The rich structure mapping.
   * @param id The name of the structure.
   * @param structure The actual rich structure.
   *
   * @return The rich structure.
   */
  private static RichStructure<?> setRichStructure(
      final SortedMap<String, RichStructure<?>> map, final String id,
      final RichStructure<?> structure) {
    map.put(id, structure);
    return structure;
  }


  /**
   * @return List of rich atoms.
   */
  @SuppressWarnings("unchecked")
  public static List<RichAtom> getAtoms() {
    return (List<RichAtom>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richAtoms.values());
  }


  /**
   * @return List of rich bonds.
   */
  @SuppressWarnings("unchecked")
  public static List<RichBond> getBonds() {
    return (List<RichBond>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richBonds.values());
  }


  /**
   * @return List of rich atom sets.
   */
  @SuppressWarnings("unchecked")
  public static List<RichAtomSet> getAtomSets() {
    return (List<RichAtomSet>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richAtomSets.values());
  }

}
