/* This is based on the LongestAliphaticChainDescriptor class from the CDK.  The
 * intention is to get the actual chains out, not just a number for the longest
 * chain.
 */
//
package io.github.egonw.analysis;


import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import java.util.HashMap;
import com.google.common.collect.Lists;

/**
 *  Class that returns a list of aliphatic chains in a given container.
 *
 */
// TODO (sorge): Refactor this to return the chains rather than a descriptor.
// TODO (sorge): Chains of length 2 don't seem to work.
public class AliphaticChain extends AbstractMolecularDescriptor implements IMolecularDescriptor {
    // VS: changed this to true.
    private boolean checkRingSystem = true;

    private static final String[] names = {"allLAC"};

    // VS: added this.
    // Containers of chains.
    private List<IAtomContainer> chain = new ArrayList<>();
    // Minimum length of aliphatic chain to extract. 
    private Integer minLength = 2;

    /**
     *  Constructor for the AliphaticChainDescriptor object.
     */
    public AliphaticChain() { }

    public AliphaticChain(Integer minLength) {
        this.minLength = minLength;
    }

    // VS: added this.
    // The longest chain container.
    public List<IAtomContainer> extract() {
        return chain;
    };

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                                           "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#longestAliphaticChain",
                                           this.getClass().getName(),
                                           "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the LongestAliphaticChainDescriptor object.
     *
     * This descriptor takes one parameter, which should be Boolean to indicate whether
     * aromaticity has been checked (TRUE) or not (FALSE).
     * 
     * @param  params            The new parameters value
     * @exception  CDKException if more than one parameter or a non-Boolean parameter is specified
     * @see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("AliphaticChainDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("Both parameters must be of type Boolean");
        }
        // ok, all should be fine
        checkRingSystem = (Boolean) params[0];
    }


    /**
     *  Gets the parameters attribute of the AliphaticChainDescriptor object.
     *
     * @return    The parameters value
     * @see #setParameters
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = checkRingSystem;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
                                   getParameters(), new IntegerResult((int) Double.NaN), getDescriptorNames(), e);
    }

    /**
     * Calculate the count of atoms of the longest aliphatic chain in the supplied {@link IAtomContainer}.
     * 
     *  The method require one parameter:
     *  if checkRingSyste is true the CDKConstant.ISINRING will be set
     *
     *@param  atomContainer  The {@link IAtomContainer} for which this descriptor is to be calculated
     *@return                   the number of atoms in the longest aliphatic chain of this AtomContainer
     *@see #setParameters
     */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer atomContainer) {
         
        IAtomContainer container = atomContainer;
        IRingSet rs;
    	if (checkRingSystem) {
            try {
                rs = new SpanningTree(container).getBasicRings();
            } catch (NoSuchAtomException e) {
                return getDummyDescriptorValue(e);
            }
            for (int i=0;i<container.getAtomCount();i++){
            	if (rs.contains(container.getAtom(i))){
                    container.getAtom(i).setFlag(CDKConstants.ISINRING,true);
            	}
            }
        }

        int longestChainAtomsCount=0;
        int tmpLongestChainAtomCount;
    	List<IAtom> startSphere;
    	List<IAtom> path;

    	for (int i =0;i<container.getAtomCount();i++){
            container.getAtom(i).setFlag(CDKConstants.VISITED, false);
        }

    	for (int i =0;i<container.getAtomCount();i++){
            IAtom atomi = container.getAtom(i);
            if (atomi.getSymbol().equals("H")) continue;

            if (!atomi.getFlag(CDKConstants.ISAROMATIC) &&
                !atomi.getFlag(CDKConstants.ISINRING) &&
                atomi.getSymbol().equals("C") &&
                !atomi.getFlag(CDKConstants.VISITED)) {
                
                startSphere = new ArrayList<IAtom>();
                path = new ArrayList<IAtom>();
                startSphere.add(atomi);
                try {
                    breadthFirstSearch(container, startSphere, path);
                } catch (CDKException e) {
                    return getDummyDescriptorValue(e);
                }
                IAtomContainer aliphaticChain =createAtomContainerFromPath(container,path);
                if (aliphaticChain.getAtomCount() >= this.minLength){
                    double[][] conMat = ConnectionMatrix.getMatrix(aliphaticChain);
                    Integer[][] pathMatrix = new Integer[conMat.length][conMat.length];
                    int[][] apsp = computeFloydAPSP(conMat, pathMatrix);
                    int pathCoordinates[] = new int[] {0, 0};
                    tmpLongestChainAtomCount=getLongestChainPath(apsp, pathCoordinates);
                    IAtomContainer longestAliphaticChain =
                    createAtomContainerFromPath(aliphaticChain,
                                                longestPath(pathMatrix, pathCoordinates[0], pathCoordinates[1], aliphaticChain));
                    // VS: added this.
                    // The longest chain container.
                    this.chain.add(longestAliphaticChain);
                    if (tmpLongestChainAtomCount>=longestChainAtomsCount){
                        longestChainAtomsCount=tmpLongestChainAtomCount;
                    }
                }
            }    		
    	}

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                                   new IntegerResult(longestChainAtomsCount),
                                   getDescriptorNames());
    }

    public List<IAtom> longestPath(Integer pathMatrix[][], int pathStart,
                                   int pathEnd, IAtomContainer chain) {
        List<IAtom> path = new ArrayList<>();
        if (pathMatrix[pathStart][pathEnd] == null) {
            return path;
        }
        List<IAtom> chainAtoms = Lists.newArrayList(chain.atoms());
        path.add(chainAtoms.get(pathStart));
        while (pathStart != pathEnd) {
            pathStart = pathMatrix[pathStart][pathEnd];
            path.add(chainAtoms.get(pathStart));
        }
        return path;
    }
        
    public int[][] computeFloydAPSP(double costMatrix[][], Integer pathMatrix[][]) {
        int i, j, k;
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (i = 0; i < nrow; i++) {
            for (j = 0; j < nrow; j++) {
                if (costMatrix[i][j] == 0) {
                    distMatrix[i][j] = 999999999;
                    pathMatrix[i][j] = null;
                } else {
                    distMatrix[i][j] = 1;
                    pathMatrix[i][j] = j;
                }
            }
        }
        for (i = 0; i < nrow; i++) {
            distMatrix[i][i] = 0;
        }
        for (k = 0; k < nrow; k++) {
            for (i = 0; i < nrow; i++) {
                for (j = 0; j < nrow; j++) {
                    if (distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j]) {
                        distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
                        pathMatrix[i][j] = pathMatrix[i][k];        // k is included in the shortest path
                    }
                }
            }
        }
        return distMatrix;
    }



    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResult(1);
    }

    private void printAtomMatrix (IAtom[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                if (matrix[i][j] != null) {
                    System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j].getID());
                }
            }
        }
    }
    
    private void printIntMatrix (int[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
            }
        }
    }
    
    private void printIntMatrix (Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
            }
        }
    }
    
    private void printDoubMatrix (double[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                System.out.println("i,j: " + i + "," + j + ": " + matrix[i][j]);
            }
        }
    }
    

    private void printAtomList (List<IAtom> atoms) {
        int i = 0;
        for (IAtom atom : atoms) {
            System.out.println("Atom " + i + ": " + atom.getID());
            i++;
        }
    }

    private int getLongestChainPath(int[][] apsp, int[] path){
        int longestPath=0;
        for (int i = 0; i < apsp.length; i++){
            for (int j = 0; j < apsp.length; j++){
                if (apsp[i][j]+1 > longestPath){
                    path[0] = i;
                    path[1] = j;
                    longestPath = apsp[i][j]+1;
                }
            }
        }
        return longestPath;
    }
    
    
    
    private IAtomContainer createAtomContainerFromPath(IAtomContainer container, List<IAtom> path){
    	IAtomContainer aliphaticChain = container.getBuilder().newInstance(IAtomContainer.class);
        for (int i=0;i<path.size()-1;i++){
            IAtom iAtom = path.get(i);
            if (!aliphaticChain.contains(iAtom)){
                aliphaticChain.addAtom(iAtom);
            }
            for (int j=1;j<path.size();j++){
                IAtom jAtom = path.get(j);
                IBond bond = container.getBond(iAtom, jAtom);
                if (bond != null){
                    if (!aliphaticChain.contains(jAtom)){
                        aliphaticChain.addAtom(jAtom);
                    }
                    if (!aliphaticChain.contains(bond)){
                        aliphaticChain.addBond(bond);
                    }
                }
            }
    	}
    	
    	//for (int i=0;i<aliphaticChain.getAtomCount();i++){
    	//	logger.debug("container-->atom:"+i+" Nr: "+container.getAtomNumber(aliphaticChain.getAtomAt(i))+" maxBondOrder:"+aliphaticChain.getMaximumBondOrder(aliphaticChain.getAtomAt(i))+" Aromatic:"+aliphaticChain.getAtomAt(i).getFlag(CDKConstants.ISAROMATIC)+" Ring:"+aliphaticChain.getAtomAt(i).getFlag(CDKConstants.ISINRING)+" FormalCharge:"+aliphaticChain.getAtomAt(i).getFormalCharge()+" Charge:"+aliphaticChain.getAtomAt(i).getCharge()+" Flag:"+aliphaticChain.getAtomAt(i).getFlag(CDKConstants.VISITED));
    	//}
    	//logger.debug("BondCount:"+aliphaticChain.getBondCount());
    	if (aliphaticChain.getBondCount()==0){
            aliphaticChain.removeAllElements();
    	}
    	return aliphaticChain;
    }
    
    /**
     *  Performs a breadthFirstSearch in an AtomContainer starting with a
     *  particular sphere, which usually consists of one start atom, and searches
     *  for a pi system. 
     *
     *@param  container                                              The AtomContainer to
     *      be searched
     *@param  sphere                                          A sphere of atoms to
     *      start the search with
     *@param  path                                          A vector which stores the atoms belonging to the pi system
     *@exception  org.openscience.cdk.exception.CDKException  Description of the
     *      Exception
     */
    private void breadthFirstSearch(IAtomContainer container, List<IAtom> sphere, List<IAtom> path) throws CDKException{
        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (IAtom atom : sphere) {
            List<IBond> bonds = container.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                nextAtom = bond.getConnectedAtom(atom);
                if ((!nextAtom.getFlag(CDKConstants.ISAROMATIC) && !nextAtom.getFlag(CDKConstants.ISINRING) & nextAtom.getSymbol().equals("C")) & !nextAtom.getFlag(CDKConstants.VISITED)) {
                    path.add(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                    if (container.getConnectedBondsCount(nextAtom) > 1) {
                        newSphere.add(nextAtom);
                    }
                } else {
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
        }
        if (newSphere.size() > 0){
            breadthFirstSearch(container, newSphere, path);
        }
    }

    
    /**
     *  Gets the parameterNames attribute of the AliphaticChainDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkRingSystem";
        return params;
    }



    /**
     *  Gets the parameterType attribute of the AliphaticChainDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return true;
    }
}
