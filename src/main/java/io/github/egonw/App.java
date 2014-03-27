package io.github.egonw;

import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

public class App {
    public static void main( String[] args ) throws Exception
    {
        InputStream file = App.class.getClassLoader().getResourceAsStream("sterane.cml");
        Builder builder = new Builder();
        Document doc = builder.build(file, "");
        System.out.println(doc.toXML());
        
        file = App.class.getClassLoader().getResourceAsStream("sterane.cml");
        CMLReader reader = new CMLReader(file);
        IChemFile cFile = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IChemFile.class));
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(cFile).get(0); // the first only
        System.out.println(mol.toString());
        
        String query = "//*[@id='" + mol.getAtom(0).getID() + "']";
        System.out.println(query);
        Nodes nodes = doc.query(query);
        System.out.println(nodes.get(0).toXML());
    }
}
