package Self_Organizing_Map;

/**
 *
 * @author Ojall Michael
 */

public class Self_Organizing_Map
{
	private static final double Decay_Rate = 0.3;  // To represent the number of iterations above 7 it increases exponentially i.e 0.7 to generate 9 iterations
	private static final int Vec_Length= 4;
	private static final int Input_Patterns = 4;
	private static final int Input_Tests = 4;
	private static final double Min_Alpha = 0.01;
	private static final double Radius_Reduce = 0.023;     
	private static final int Maximum_Nodes = 5;               // number nodes cannot exceed more than 5 nodes
	private static double alpha = 0.2;
        
	private static final double d[] = new double[Maximum_Nodes];            //nodes
	
	//    Weight matrix with randomly chosen values between 0.0 and 1.0
        
	private static final double w[][] = {{0.1, 0.6, 0.5, 0.9, 0.4, 0.2, 0.8},
	                                                    {0.9, 0.3, 0.5, 0.4, 0.5, 0.6, 0.3},
	                                                    {0.8, 0.5, 0.7, 0.2, 0.6, 0.9, 0.5},
	                                                    {0.6, 0.4, 0.9, 0.3, 0.7, 0.2, 0.4},
	                                                    {0.8, 0.9, 0.7, 0.9, 0.4, 0.2, 0.5}};
	
	private static final int training_set[ ][ ] = {{1, 0, 1, 0, 0, 0, 0},
	                                                               {0, 0, 0, 0, 1, 1, 1},
	                                                               {0, 0, 1, 0, 1, 0, 0},
	                                                               {1, 0, 0, 1, 0, 0, 1},
	                                                               {1, 0, 1, 0, 1, 0, 1}};
	
	private static final int test_set[ ][ ] = {{1, 1, 0, 1, 0, 0, 0},
	                                                        {0, 1, 1, 0, 1, 1, 1},
	                                                        {0, 1, 0, 1, 0, 1, 0},
	                                                        {0, 1, 0, 1, 0, 0, 1},
	                                                        {0, 0, 0, 1, 1, 1, 1}};
	
	private static void competitiveTraining()
	{
            int iterations = 0;
	    boolean reductionFlag = false;
	    int reductionPoint = 0;
	    int dMinimum = 0;

	    while(alpha > Min_Alpha)
	    {
	        iterations += 1;
                //for each input vector
	        for(int vecNum = 0; vecNum <= (Input_Patterns - 1); vecNum++)
	        {
	            //Compute input for all nodes.
	            computeInput(training_set, vecNum);

	            //See which is smaller?
	            dMinimum = minimum(d);

	            //Update the weights on the winning unit.
	            updateWeights(vecNum, dMinimum);

	        } // VecNum

	        //Reduction of the learning rate.
	        alpha = Decay_Rate * alpha;

	        //Reduce radius at specified point.
	        if(alpha < Radius_Reduce){
	            if(reductionFlag == false){
	                reductionFlag = true;
	                reductionPoint = iterations;
	            }
	        }
	    }

	    System.out.println("Iterations: " + iterations);
		
	    System.out.println("Neighborhood radius reduced after " + reductionPoint + " iterations.");
		
	}
	
	private static void computeInput(int[][] vectorArray, int vectorNumber)
	{
            clearArray(d);
	    for(int i = 0; i <= (Maximum_Nodes - 1); i++){
	        for(int j = 0; j <= (Vec_Length- 1); j++){
	            d[i] += Math.pow((w[i][j] - vectorArray[vectorNumber][j]), 2);
	        } // j
	    } // i
	}
	
	private static void updateWeights(int vectorNumber, int dMinimum)
	{
		for(int i = 0; i <= (Vec_Length- 1); i++)
	    {
	        //Update the winner.
	        w[dMinimum][i] = w[dMinimum][i] + (alpha * (training_set[vectorNumber][i] - w[dMinimum][i]));

	        //Only include neighbors before radius reduction point is reached.
                
	        if(alpha > Radius_Reduce){
	            if((dMinimum > 0) && (dMinimum < (Maximum_Nodes - 1))){
	                //Update neighbor to the left...
	                w[dMinimum - 1][i] = w[dMinimum - 1][i] + (alpha * (training_set[vectorNumber][i] - w[dMinimum - 1][i]));
	                //and update neighbor to the right.
	                w[dMinimum + 1][i] = w[dMinimum + 1][i] + (alpha * (training_set[vectorNumber][i] - w[dMinimum + 1][i]));
	            } else {
	                if(dMinimum == 0){
	                    //Update neighbor to the right.
	                    w[dMinimum + 1][i] = w[dMinimum + 1][i] + (alpha * (training_set[vectorNumber][i] - w[dMinimum + 1][i]));
	                } else {
	                    //Update neighbor to the left.
	                    w[dMinimum - 1][i] = w[dMinimum - 1][i] + (alpha * (training_set[vectorNumber][i] - w[dMinimum - 1][i]));
	                }
	            }
	        }
	    } 
	}
	
	private static void clearArray(double[] nodeArray)
	{
		for(int i = 0; i <= (Maximum_Nodes - 1); i++)
	    {
	        nodeArray[i] = 0.0;
	    } // i
	}
	
	private static int minimum(double[] nodeArray)
	{
		int winner = 0;
	    boolean foundNewWinner = false;
	    boolean done = false;

	    while(!done)
	    {
	        foundNewWinner = false;
	        for(int i = 0; i <= (Maximum_Nodes - 1); i++)
	        {
	            if(i != winner){            
                        //Avoid self-comparison.
                        
	                if(nodeArray[i] < nodeArray[winner]){
	                    winner = i;
	                    foundNewWinner = true;
	                }
	            }
	        } // i

	        if(foundNewWinner == false){
	            done = true;
	        }
	    }
	    return winner;
	}
	
	private static void printResults()
	{
		int dMinimum = 0;

		//Print clusters created.
		    System.out.println("Training The Weights: ");
		    for(int vecNum = 0; vecNum <= (Input_Patterns - 1); vecNum++)
		    {
		        //Compute input.
		        computeInput(training_set, vecNum);

		        //See which is smaller.
		        dMinimum = minimum(d);

		        System.out.print("Vector (");
		        for(int i = 0; i <= (Vec_Length- 1); i++)
		        {
		        	System.out.print(training_set[vecNum][i] + ", ");
		        } // i
		        System.out.print(") Belongs to node " + dMinimum + "\n");

		    } // VecNum

		//Print weight matrix.
		    System.out.println("***************************************************************");
		    for(int i = 0; i <= (Maximum_Nodes - 1); i++)
		    {
		    	System.out.println("New Weights for Node " + i + " Connections:");
		    	System.out.print("     ");
		        for(int j = 0; j <= (Vec_Length- 1); j++)
		        {
		        	String temp = String.format("%.3f", w[i][j]);
		        	System.out.print(temp + ", ");
		        } // j
		        System.out.print("\n");
		    } // i

		//Print post-competitiveTraining tests.
		    System.out.println("***************************************************************");
		                                    System.out.println("Test Input:");
		    for(int vecNum = 0; vecNum <= (Input_Tests - 1); vecNum++)
		    {
		        //Compute input for all nodes.
		        computeInput(test_set, vecNum);

		        //See which is smaller.
		        dMinimum = minimum(d);

		        System.out.print("Vector (");
		        for(int i = 0; i <= (Vec_Length- 1); i++)
		        {
		        	System.out.print(test_set[vecNum][i] + ", ");
		        } // i
		        System.out.print(") Belongs To Node " + dMinimum + "\n");

		    } // VecNum
	}
	
	public static void main(String[] args)
	{
		competitiveTraining();
		printResults();
	}

}
