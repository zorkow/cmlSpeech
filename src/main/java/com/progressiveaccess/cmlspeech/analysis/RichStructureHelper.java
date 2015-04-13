
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

public final class RichStructureHelper {

  public static RichMolecule richMolecule;
  public static SortedMap<String, RichStructure<?>> richAtoms;
  public static SortedMap<String, RichStructure<?>> richBonds;
  public static SortedMap<String, RichStructure<?>> richAtomSets;

  public static boolean isAtom(final String id) {
    return RichStructureHelper.richAtoms.containsKey(id);
  }

  public static boolean isBond(final String id) {
    return RichStructureHelper.richBonds.containsKey(id);
  }

  public static boolean isAtomSet(final String id) {
    return RichStructureHelper.richAtomSets.containsKey(id);
  }

  public static void init() {
    RichStructureHelper.richMolecule = null;
    RichStructureHelper.richAtoms = new TreeMap<>(new CmlNameComparator());
    RichStructureHelper.richBonds = new TreeMap<>(new CmlNameComparator());
    RichStructureHelper.richAtomSets = new TreeMap<>(new CmlNameComparator());
  }

  public static RichAtom getRichAtom(final String id) {
    return (RichAtom) RichStructureHelper.richAtoms.get(id);
  }

  public static RichStructure<?> setRichAtom(final IAtom atom) {
    return RichStructureHelper.setRichStructure(RichStructureHelper.richAtoms,
        atom.getID(), new RichAtom(atom));
  }

  public static RichBond getRichBond(final String id) {
    return (RichBond) RichStructureHelper.richBonds.get(id);
  }

  public static RichStructure<?> getRichBond(final IBond bond) {
    return RichStructureHelper.getRichBond(bond.getID());
  }

  public static RichStructure<?> setRichBond(final IBond bond) {
    return RichStructureHelper.setRichStructure(RichStructureHelper.richBonds,
        bond.getID(), new RichBond(bond));
  }

  public static RichAtomSet getRichAtomSet(final String id) {
    return (RichAtomSet) RichStructureHelper.richAtomSets.get(id);
  }

  public static RichAtomSet setRichAtomSet(final RichAtomSet atomSet) {
    return (RichAtomSet) RichStructureHelper.setRichStructure(
        RichStructureHelper.richAtomSets, atomSet.getId(), atomSet);
  }

  public static RichStructure<?> setRichStructure(
      final SortedMap<String, RichStructure<?>> map, final String id,
      final RichStructure<?> structure) {
    map.put(id, structure);
    return structure;
  }

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

  @SuppressWarnings("unchecked")
  public static List<RichAtom> getAtoms() {
    return (List<RichAtom>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richAtoms.values());
  }

  @SuppressWarnings("unchecked")
  public static List<RichBond> getBonds() {
    return (List<RichBond>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richBonds.values());
  }

  @SuppressWarnings("unchecked")
  public static List<RichAtomSet> getAtomSets() {
    return (List<RichAtomSet>) (List<?>) new ArrayList<RichStructure<?>>(
        RichStructureHelper.richAtomSets.values());
  }

}
