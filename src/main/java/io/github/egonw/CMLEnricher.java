package io.github.egonw;

import io.github.egonw.Cli;
import io.github.egonw.Logger;

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
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;

import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor;
import org.openscience.cdk.qsar.DescriptorValue;

import com.google.common.collect.Lists; 

public class CMLEnricher {
    private Cli cli;
    private Logger logger;

    public CMLEnricher(Cli initCli, Logger initLogger) {
	cli = initCli;
	logger = initLogger;
    }

    public void enrichFiles() {
	// check output option here!
	for (String file : cli.files) {
	    Document doc;
	    try {
		doc = enrichFile(file);
	    }
	    catch (Exception e) {
		this.logger.error("Something went wrong when parsing File " + file);
		continue;
	    }
	    System.out.println(doc);
	    //outputFile(file, doc);
	}
    }

    private Document enrichFile(String fileName) throws Exception {
	InputStream file = new FileInputStream(fileName);
	Builder builder = new CMLBuilder(); 
	Document doc = builder.build(file, "");
	System.err.println(doc.toXML());
	file = new FileInputStream(fileName);
        CMLReader reader = new CMLReader(file);
        IChemFile cFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IChemFile.class));
        reader.close();
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(cFile).get(0); // the first only
	enrichCML(doc, mol);
	return(doc);
    }

    private void enrichCML (Document doc, IAtomContainer mol) {
        RingSearch ringSearch = new RingSearch(mol);
        int systemCounter = 0;

	//	enrichFusedRings(systemCounter,ringSearch, )
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Fused ring system " + systemCounter, ring.atoms(), doc);
	    List<IAtomContainer> subRings = this.cli.cl.hasOption("sssr") ?
		sssrSubRings(ring) : smallestSubRings(ring);
	    int subSystem = 0;
	    for (IAtomContainer subRing : subRings) {
		appendAtomSet("Subring " + subSystem + " of ring system " + systemCounter, subRing.atoms(), doc);
		subSystem++;
	    }
            systemCounter++;
        }

        // for (IAtomContainer ring : ringSystems) {
        //     appendAtomSet("Essential ring system " + systemCounter, ring.atoms(), doc);
        //     systemCounter++;


        ringSystems = ringSearch.isolatedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Isolated ring system " + systemCounter, ring.atoms(), doc);
            systemCounter++;
        }

	// IAtomContainer cyclic = ringSearch.ringFragments();
	// appendAtomSet("Cyclic system " + systemCounter, cyclic.atoms(), doc);
	// systemCounter++;


	LongestAliphaticChainDescriptor chain = new LongestAliphaticChainDescriptor();
	DescriptorValue descr = chain.calculate(mol);
	System.err.println(descr.getValue().toString());
	
        System.out.println(doc.toXML());
    }

    private static void appendAtomSet(String title, Iterable<IAtom> atoms, Document doc) {
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
    private static boolean isSmallest(IAtomContainer ring, List<IAtomContainer> restRings, int position){
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
	for (int i = 0; i < length; i++) {
	    IAtomContainer subRing = allRings.get(i);
	    if (isSmallest(subRing, allRings, i)) {
		subRings.add(subRing);
	    }
	}
	return subRings;
    };

    private List<IAtomContainer> sssrSubRings(IAtomContainer ring) {
	this.logger.logging("SSSR sub ring computation.");
    	SSSRFinder sssr = new SSSRFinder(ring);
    	IRingSet essentialRings = sssr.findSSSR();
    	return Lists.newArrayList(essentialRings.atomContainers());
    }

}
