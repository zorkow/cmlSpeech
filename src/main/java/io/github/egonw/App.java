package io.github.egonw;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;

public class App {
    public static void main( String[] args ) throws Exception {
    	if (args.length < 1) {
    		System.out.println("enrich.sf [CMLFILE]");
    		System.exit(-1);
    	};
    	String fileName = args[0];

        InputStream file = new FileInputStream(fileName);
        Builder builder = new CMLBuilder();
        Document doc = builder.build(file, "");
        System.out.println(doc.toXML());

        file = App.class.getClassLoader().getResourceAsStream("sterane.cml");
        CMLReader reader = new CMLReader(file);
        IChemFile cFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IChemFile.class));
        reader.close();
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(cFile).get(0); // the first only
        System.out.println(mol.toString());
        
        RingSearch ringSearch = new RingSearch(mol);
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
        int system = 0;
        for (IAtomContainer ring : ringSystems) {
        	CMLAtomSet set = new CMLAtomSet();
        	set.setTitle("Ring system " + system);
        	system++;
        	System.out.println("System " + system);
        	for (IAtom atom : ring.atoms()) {
        		System.out.println("System " + system + " has " + atom.getID());
                String query = "//*[@id='" + atom.getID() + "']";
                System.out.println(query);
                Nodes nodes = doc.query(query);
                System.out.println(nodes.get(0).toXML());
                set.addAtom((CMLAtom)nodes.get(0));
                nodes.get(0).getParent().getParent().appendChild(set);
        	}
        }

        System.out.println(doc.toXML());
    }
}
