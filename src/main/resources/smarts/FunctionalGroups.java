// THIS IS NOT WORKING CODE!
//
package home.sorge.git.egonw.cmlSpeech;

import org.openscience.cdk.smiles.smarts.SmartsPattern;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.DefaultChemObjectBuilder;
/**
 *
 */

public class FunctionalGroups {
    
    String[] functionalGroups = {
                     "O=CO", "O=C-[NH]", "CC(C)C"
      };

    method compute() {
        try {
            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
            SmartsPattern pattern = SmartsPattern.create("CC(C)C", builder);
        }
        catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
      for (String group : functionalGroups) {
             SmartsPattern pattern = SmartsPattern.create(group, bldr);
             System.out.println("Group " + group);
             if (pattern.matches(mol)) {
	       Mappings mappings = pattern..matchAll(mol);
	       if (mappings.count() > 0) {
		 Iterator<int[]> iter = mappings.iterator();
		 while (iter.hasNext()) {
		   int[] matchingAtoms = iter.next();
		   System.out.print("  matches ");
		   for (int match : matchingAtoms) {
		     System.out.print(match + " ");
		   }
		   System.out.println();
		 }
	       }
	     }
      }
    }
}

