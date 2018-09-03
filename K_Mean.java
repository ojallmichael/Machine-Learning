package kmeans;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class K_Mean {

    int k;
    int values;
    ArrayList<Integer> elements;
    ArrayList<Integer> new_coins;
    ArrayList<Integer> shillings;
    ArrayList<Integer> row;
    ArrayList<ArrayList<Integer>> groups;
    Scanner input;

    public K_Mean(int k, int values) {
        this.k = k;
        this.values = values;
        elements = new ArrayList<>();
        new_coins = new ArrayList<>();
        shillings = new ArrayList<>();
        row = new ArrayList<>();
        groups = new ArrayList<>();
        input = new Scanner(System.in);

        for (int i = 0; i < k; i++) {
            groups.add(new ArrayList<>());
        }

        for (int i = 0; i < values; i++) {
            System.out.println("Element: " + (i + 1));
            elements.add(input.nextInt());
            if (i < k) {
                new_coins.add(elements.get(i));
                System.out.println("X" + (i + 1) + " is " + new_coins.get(i));
            }
        }
        int iter = 1;
        do {
            elements.stream().map((aItem) -> {
                new_coins.forEach((c) -> {
                    row.add(abs(c - aItem));
                });
                return aItem;
            }).map((aItem) -> {
                groups.get(row.indexOf(Collections.min(row))).add(aItem);
                return aItem;
            }).forEachOrdered((_item) -> {
                row.removeAll(row);
            });
            for (int i = 0; i < k; i++) {
                if (iter == 1) {
                    shillings.add(new_coins.get(i));
                } else {
                    shillings.set(i, new_coins.get(i));
                }
                if (!groups.get(i).isEmpty()) {
                    new_coins.set(i, average(groups.get(i)));
                }
            }
            if (!new_coins.equals(shillings)) {
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).removeAll(groups.get(i));
                }
            }
            iter++;
        } while (!new_coins.equals(shillings));
        for (int i = 0; i < new_coins.size(); i++) {
            System.out.println("New X" + (i + 1) + " " + new_coins.get(i));
        }
        for (int i = 0; i < groups.size(); i++) {
            System.out.println("Group " + (i + 1));
            System.out.println(groups.get(i).toString());
        }
        System.out.println("Iterations: " + iter);
    }

    public static void main(String[] args) {
    	@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
    	
    	System.out.print("Enter Size of data set: ");
        int values = input.nextInt();
       
        System.out.println("\n Enter Value of K. ( Number of groups to be formed)");
        int k = input.nextInt();
        
        System.out.println("Enter the element values: ");
        
        K_Mean k_Mean;
        k_Mean = new K_Mean(k, values);
    }

    public static int average(ArrayList<Integer> list) {
        int sum = 0;
        for (Integer value : list) {
            sum = sum + value;
        }
        return sum / list.size();
    }
}
 
