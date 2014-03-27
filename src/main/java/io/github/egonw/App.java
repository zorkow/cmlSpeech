package io.github.egonw;

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
import org.xmlcml.cml.element.CMLCml;

public class App {
    public static void main( String[] args ) throws Exception
    {
        InputStream file = App.class.getClassLoader().getResourceAsStream("sterane.cml");
        Builder builder = new CMLBuilder();
        Document doc = builder.build(file, "");
        System.out.println(doc.toXML());

        CMLCml cmlRoot = null;
        if (!(doc.getRootElement() instanceof CMLCml)) {
        	cmlRoot = new CMLCml();
        	cmlRoot.appendChild(doc.getRootElement().copy());
        	doc.setRootElement(cmlRoot);
        } else {
        	cmlRoot = (CMLCml)doc.getRootElement();
        }

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
        	}
        	cmlRoot.appendChild(set);
        }

        System.out.println(doc.toXML());
    }
}
