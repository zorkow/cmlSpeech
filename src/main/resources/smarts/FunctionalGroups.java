// THIS IS NOT WORKING CODE!
//
package home.sorge.git.egonw.cmlSpeech;

/**
 *
 */

public class FunctionalGroups {
    
    String[] functionalGroups = {
                     "O=CO", "O=C-[NH]", "CC(C)C"
      };

    method compute() {
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

