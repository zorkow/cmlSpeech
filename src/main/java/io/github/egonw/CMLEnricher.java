/**
 * @file   CMLEnricher.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Apr 28 01:41:35 2014
 * 
 * @brief  Main class to enrich CML files.
 * 
 * 
 */

package io.github.egonw;

import io.github.egonw.Cli;
import io.github.egonw.Logger;

import java.util.function.Function;
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
    private final Cli cli;
    private final Logger logger;

    private Document doc;
    private IAtomContainer mol;
    private int idCount;

    /** 
     * Constructor
     * 
     * @param initCli 
     * @param initLogger 
     * 
     * @return 
     */    
    public CMLEnricher(Cli initCli, Logger initLogger) {
	cli = initCli;
	logger = initLogger;
    }

    /** 
     * Enriches all CML files given as input arguments.
     * 
     */
    public void enrichFiles() {
	for (String file : cli.files) {
	    enrichFile(file);
	}
    }

    /** 
     * Convenience method to enrich a CML file.
     * 
     * @param fileName File to enrich.
     */
    private void enrichFile(String fileName) {
	this.idCount = 0;
	try {
	    readFile(fileName);
	} catch (Exception e) {
	    this.logger.error("Something went wrong when parsing File " + fileName);
	    return;
	}
	enrichCML();
	System.out.println(this.doc.toXML());
	//writeFile(file);
    }

    /** 
     * Loads current CML file and IAtomContainer.
     * 
     * @param fileName File to load.
     * @throws Exception When file can not be loaded or is not a proper CML file.
     */
    private void readFile(String fileName) throws Exception {
	InputStream file = new FileInputStream(fileName);
	Builder builder = new CMLBuilder(); 
	this.doc = builder.build(file, "");
	this.logger.logging(this.doc.toXML());
	file = new FileInputStream(fileName);
        CMLReader reader = new CMLReader(file);
        IChemFile cFile = reader.read(SilentChemObjectBuilder.getInstance().
				      newInstance(IChemFile.class));
        reader.close();
        this.mol = ChemFileManipulator.getAllAtomContainers(cFile).get(0);
    }

    /** 
     * Enriches the current CML documment.
     *
     */
    private void enrichCML() {
        RingSearch ringSearch = new RingSearch(mol);

	if (this.cli.cl.hasOption("s")) {
	    getFusedRings(ringSearch);
	} else {
	    getFusedRings(ringSearch, this.cli.cl.hasOption("sssr") ?
			  (ring) -> sssrSubRings(ring) : 
			  (ring) -> smallestSubRings(ring));
	}

        List<IAtomContainer> ringSystems = ringSearch.isolatedRingFragments();
        for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Isolated ring system " + this.idCount, ring.atoms());
            this.idCount++;
        }

	LongestAliphaticChainDescriptor chain = new LongestAliphaticChainDescriptor();
	DescriptorValue descr = chain.calculate(this.mol);
	this.logger.logging(descr.getValue().toString());
    }

    /** 
     * Computes Fused rings without subsystems.
     * 
     * @param ringSearch 
     */    
    private void getFusedRings(RingSearch ringSearch) {
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
	for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Fused ring system " + this.idCount, ring.atoms());
            this.idCount++;
        }
    }

    /** 
     * Computes fused rings and their subsystems.
     * 
     * @param ringSearch 
     * @param subRingMethod Method to compute subrings.
     */    
    private void getFusedRings(RingSearch ringSearch,
			       Function<IAtomContainer, List<IAtomContainer>> subRingMethod) {
        List<IAtomContainer> ringSystems = ringSearch.fusedRingFragments();
	for (IAtomContainer ring : ringSystems) {
            appendAtomSet("Fused ring system " + this.idCount, ring.atoms());
	    List<IAtomContainer> subRings = subRingMethod.apply(ring);
	    int subSystem = 0;
	    // TODO: Sort out the id count properly.
	    for (IAtomContainer subRing : subRings) {
		appendAtomSet("Subring " + subSystem + " of ring system " + this.idCount, subRing.atoms());
		subSystem++;
	    }
            this.idCount++;
        }
    }

    /** 
     * Predicate that tests if a particular ring has no other ring as proper subset.
     * 
     * @param ring The ring to be tested.
     * @param restRings The other rings (possibly including the first ring).
     * 
     * @return True if ring has smallest coverage.
     */
    // This is quadratic and should be done better!
    // All the iterator to list operations should be done exactly once!
    private static boolean isSmallest(IAtomContainer ring, List<IAtomContainer> restRings){
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

    /** 
     * Method to compute smallest rings via subset coverage.
     * 
     * @param ring Fused ring to be broken up.
     * 
     * @return Subrings as atom containers.
     */    
    private List<IAtomContainer> smallestSubRings(IAtomContainer ring) {
	AllRingsFinder arf = new AllRingsFinder();
	List<IAtomContainer> subRings = new ArrayList<IAtomContainer>();
	IRingSet rs;
	try {
	    rs = arf.findAllRings(ring);
	} catch (CDKException e) {
	    this.logger.error("Error " + e.getMessage());
	    return subRings;
	}

	List<IAtomContainer> allRings = Lists.newArrayList(rs.atomContainers());
	int length = allRings.size();
	for (int i = 0; i < length; i++) {
	    IAtomContainer subRing = allRings.get(i);
	    if (isSmallest(subRing, allRings)) {
		subRings.add(subRing);
	    }
	}
	return subRings;
    };

    /** 
     * Method to compute smallest rings via SSSR finder.
     * 
     * @param ring Fused ring to be broken up.
     * 
     * @return Subrings as atom containers.
     */
    private List<IAtomContainer> sssrSubRings(IAtomContainer ring) {
	this.logger.logging("SSSR sub ring computation.\n");
    	SSSRFinder sssr = new SSSRFinder(ring);
    	IRingSet essentialRings = sssr.findSSSR();
    	return Lists.newArrayList(essentialRings.atomContainers());
    }

    /** 
     * Append Atom Sets to the CML documents.
     * 
     * @param title Title of the atom set to be added. 
     * @param atoms Iterable atom list.
     */
    private void appendAtomSet(String title, Iterable<IAtom> atoms) {
        CMLAtomSet set = new CMLAtomSet();
        set.setTitle(title);
	this.logger.logging(title + " has atoms:");
        for (IAtom atom : atoms) {
            this.logger.logging(" " + atom.getID());
            String query = "//*[@id='" + atom.getID() + "']";
            Nodes nodes = this.doc.query(query);
            set.addAtom((CMLAtom)nodes.get(0));
        }
	this.logger.logging("\n");
	this.doc.getRootElement().appendChild(set);
    };

}
