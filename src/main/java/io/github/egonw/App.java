package io.github.egonw;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;

import com.google.common.collect.Lists;

public class App {

    private static void appendAtomSet (String title, Iterable<IAtom> atoms, Document doc) {
        CMLAtomSet set = new CMLAtomSet();

        set.setTitle(title);
	System.err.print(title + " has atoms:");
        for (IAtom atom : atoms) {
            System.err.print(" " + atom.getID());
            String query = "//*[@id='" + atom.getID() + "']";
            Nodes nodes = doc.query(query);
            set.addAtom((CMLAtom)nodes.get(0));
        }
	System.err.println();
	doc.getRootElement().appendChild(set);
    };

    // This is quadratic and should be done better!
    // All the iterator to list operations should be done exactly once!
    private static boolean isSmallest (IAtomContainer ring, List<IAtomContainer> restRings, int position){
	List<IAtom> ringAtoms = Lists.newArrayList(ring.atoms());
	for (IAtomContainer restRing : restRings) {
	    if (ring == restRing) {
		continue;
	    }
	    List<IAtom> restRingAtoms = Lists.newArrayList(restRing.atoms());
	    if (ringAtoms.containsAll(restRingAtoms)) {
		return false;
	    };
	}
	return true;
    };

    private static List<IAtomContainer> smallestSubRings(IAtomContainer ring) {
	AllRingsFinder arf = new AllRingsFinder();
	List<IAtomContainer> subRings = new ArrayList<IAtomContainer>();
	IRingSet rs;
	try {
	    rs = arf.findAllRings(ring);
	} catch (CDKException e) {
	    System.err.println("Error " + e.getMessage());
	    return subRings;
	}

	
	List<IAtomContainer> allRings = Lists.newArrayList(rs.atomContainers());
	int length = allRings.size();
	System.err.println("Length " + length);
	for (int i = 0; i < length; i++) {
	    IAtomContainer subRing = allRings.get(i);
	    if (isSmallest(subRing, allRings, i)) {
		subRings.add(subRing);
	    }
	}
	return subRings;
    };

    public static void main( String[] args ) throws Exception {
        if (args.length < 1) {
                System.err.println("enrich.sh <CMLFILES>");
                System.exit(-1);
        };
        String fileName = args[0];

        InputStream file = new FileInputStream(fileName);
        Builder builder = new CMLBuilder(); 
        Document doc = builder.build(file, "");
        System.err.println(doc.toXML());

        file = new FileInputStream(fileName);
        CMLReader reader = new CMLReader(file);
        IChemFile cFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IChemFile.class));
        reader.close();
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(cFile).get(0); // the first only

        RingSearch ringSearch = new RingSearch(mol);
        int system = 0;

        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Fused ring system " + system, ring.atoms(), doc);
	    List<IAtomContainer> subRings = smallestSubRings(ring);
	    int subSystem = 0;
	    for (IAtomContainer subRing : subRings) {
		appendAtomSet("Subring " + subSystem + " of ring system " + system, subRing.atoms(), doc);
		subSystem++;
	    }
            system++;
        }

        ringSystems = ringSearch.isolatedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Isolated ring system " + system, ring.atoms(), doc);
            system++;
        }

	// IAtomContainer cyclic = ringSearch.ringFragments();
	// appendAtomSet("Cyclic system " + system, cyclic.atoms(), doc);
	// system++;
	
        System.out.println(doc.toXML());
    }
}
