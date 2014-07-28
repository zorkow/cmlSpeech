//
package uk.ac.cam.ch.wwmm.opsin;

import nu.xom.Element;

/**
 * Helper class to get to classes private to the Opsin package
 */

public class OpsinHelper {

    public static String smiles2something(String smiles) {
        SMILESFragmentBuilder builder = new SMILESFragmentBuilder(new IDManager());
        try {
            Fragment fusedRing = builder.build(smiles);
            FusedRingNumberer.numberFusedRing(fusedRing);
            for (Atom atom : fusedRing.getAtomList()) {
                System.out.println("ID: " + atom.getID() +
                                   " Locant: " + atom.getFirstLocant().toString() +
                                   " Element: " + atom.getElement());
            }
        }
        catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
        return "";
    }

}
