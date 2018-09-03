/** OJALL MICHAEL OMONDI P15/31821/2015
 * 
 * An implementation of k nearest neighbor.
 * Use of Euclidean distance weighted by 1/distance
 * Main method to classify if entry is New_Vehicle or Old_Vehicle based on:
 * based on the year of manufacture, and the month of being manufactured.
 *
 * algorithm
 *  1. Determine the parameter K. 
 *           K = number of nearest neighbors (size of cluster)
 *   2. Calculate the distance between the query-instance and all the training samples
 *   3. Sort the distances in an ascending order and determine nearest neighbors based on the K-th minimum distance
 *   4. Gather the categories (labels) of the nearest neighbors
 *   5. Use simple majority of the category of nearest neighbors as the prediction value of the query instance

 */
package KNN;

/*
 *
 * @author Ojall Michael
*
 */

import java.util.ArrayList;
import java.util.HashMap;

public class KNN{

	private final int k;   //           The number of clusters to use
	private final ArrayList<Object> classes;
	private final ArrayList<DataEntry> dataSet;    //     The set containing training data

        //Initializing variables
	public KNN(ArrayList<DataEntry> dataSet, int k){
		this.classes = new ArrayList<>();
		this.k = k;
		this.dataSet = dataSet;

            //Load different classes
            dataSet.stream().filter((entry) -> (!classes.contains(entry.getY()))).forEachOrdered((entry) -> {
                classes.add(entry.getY());
            });
	}

        public static void main(String[] args){
		ArrayList<KNN.DataEntry> data = new ArrayList<>();
		data.add(new DataEntry(new double[ ]{2018,8}, "New_Vehicle"));
		data.add(new DataEntry(new double[ ]{2017,11}, "New_Vehicle"));
		data.add(new DataEntry(new double[ ]{2018,9}, "New_Vehicle"));
		data.add(new DataEntry(new double[ ]{2016,6}, "New_Vehicle"));
		data.add(new DataEntry(new double[ ]{2015,7}, "New_Vehicle"));
		data.add(new DataEntry(new double[ ]{1990,8}, "Old_Vehicle"));
		data.add(new DataEntry(new double[ ]{1983,5}, "Old_Vehicle"));
                                    data.add(new DataEntry(new double[ ]{2001,6}, "Old_Vehicle"));
                                    data.add(new DataEntry(new double[ ]{2002,7}, "Old_Vehicle"));
		data.add(new DataEntry(new double[ ]{2004,11}, "Old_Vehicle"));
		KNN test_set = new KNN(data, 3); // 3 clusters
                
                //Atributes of the query instance
		System.out.println("Classified as: "+test_set.classify(new DataEntry(new double[]{2013, 6},"Ignore")));
	}

        /*
        * Sort the distances in an ascending order
        * and determine nearest neighbours based on the K-th minimum distance
        */ 
	private DataEntry[ ] getNearestNeighbour(DataEntry x){
		DataEntry[ ] back = new DataEntry[this.k];
		double nearest;
            nearest = Double.MIN_VALUE;
		int index = 0;
            for (DataEntry set : this.dataSet) {
                double distance = distance(x,set);
                if(back[back.length-1] == null)
                { 
                    int j = 0;
                    while(j < back.length){
                        if(back[j] == null){
                            back[j] = set; break;
                        }
                        j++;
                    }
                    if(distance > nearest){
                        index = j;
                        nearest = distance;
                    }
                }
                else{
                    if(distance < nearest){
                        back[index] = set;
                        double f = 0.0;
                        int ind = 0;
                        for(int j = 0; j < back.length; j++){
                            double dt = distance(back[j],x);
                            if(dt > f){
                                f = dt;
                                ind = j;
                            }
                        }
                        nearest = f;
                        index = ind;
                    }
                }
            }
		return back;
	}

	private static double convertDistance(double d){
		return 1.0/d;
	}

	/**
	 * Computes Euclidean distance
     * @param Fromdata
     * @param Todata
	 * @return Distance
	 */
        //Calculate the distance between the query-instance and all the training samples
	public static double distance(DataEntry Fromdata, DataEntry Todata){
		double distance = 0.0;
		int length = Fromdata.getX().length;
		for(int i = 0; i < length; i++){
			double t = Fromdata.getX()[i]-Todata.getX()[i];
			distance = distance+t*t;
		}
		return Math.sqrt(distance);
	}
	/**
	 *
	 * @param Toclassifydata Entry to be classifies
	 * @return The class of the most probable class
	 */
	public Object classify(DataEntry Toclassifydata){
		HashMap<Object,Double> classcount = new HashMap<>();
		DataEntry[] de = this.getNearestNeighbour(Toclassifydata);
            for (DataEntry de1 : de) {
                double distance = KNN.convertDistance(KNN.distance(de1, Toclassifydata));
                if (!classcount.containsKey(de1.getY())) {
                    classcount.put(de1.getY(), distance);
                } else {
                    classcount.put(de1.getY(), classcount.get(de1.getY()) + distance);
                }
            }
		//Find right choice
		Object o = null;
		double max = 0;
		for(Object ob : classcount.keySet()){
			if(classcount.get(ob) > max){
				max = classcount.get(ob);
				o = ob;
			}
		}

		return o;
	}


public static class DataEntry{
	private final double[] x;
	private final Object y;

	public DataEntry(double[] x, Object y){
		this.x = x;
		this.y = y;
	}

		public double[] getX(){
			return this.x;
		}

		public Object getY(){
			return this.y;
		}
	}
}
