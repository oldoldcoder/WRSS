package BuildingBlocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class DataProcessing {
	
	public static void readData(String filename, String splitString, ArrayList<int[]> database) throws Exception {
		
		String line = null;
	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    line = in.readLine();
	    while((line = in.readLine())!=null)
	    {   
	    	String[] tempStrings = line.split(splitString);
	    	int[] tempInt = new int[2];
	    	for(int i = 0; i < 2; i++)  {
	    		tempInt[i] = (int) Math.round(new Double(tempStrings[i]));
	    	}
	    	database.add(tempInt);
	   }
	}
	
	// write the dataset to a file
	public static void writeData(ArrayList<int[]> database, String outputFile, String splitString) throws Exception {
        
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
        for(int i = 0; i < database.size(); i++) {
        	String str = String.valueOf(database.get(i)[0]) + splitString + String.valueOf(database.get(i)[1] + "\n");
        	out.write(str);
        }
        out.close();
	}
	
	// combineData and write into a file
	public static void combineDataAndWriteToFile() throws Exception {
		
		String[] filename = {"Jeans_5713.csv", "Shirts_11407.csv", "Sports Shoes_10571.csv", "Sportswear_6201.csv", "T-shirts and Polos_12735.csv", "Western Wear_15514.csv"}; 
		String splitString = ",";
		
		ArrayList<int[]> database = new ArrayList<int[]>();
		for(int i = 0; i < filename.length; i++) {
			String file = "src/" + filename[i];
		 	readData(file, splitString, database);
		}
		String outputFile = "src/cleanData.txt";
		writeData(database, outputFile, splitString);
		
	}
	
	// write data for evaluation
	public static ArrayList<int[]> readData(String filename, String splitString, int n) throws Exception {
		
		ArrayList<int[]> database = new ArrayList<int[]>();
		
		String line = null;
	    BufferedReader in = new BufferedReader(new FileReader(filename));
	    int count = 0;
	    while((count < n) && (line = in.readLine())!=null)
	    {   
	    	String[] tempStrings = line.split(splitString);
	    	int[] tempInt = new int[2];
	    	for(int i = 0; i < 2; i++)  {
	    		tempInt[i] = (int) Math.round(new Double(tempStrings[i]));
	    	}
	    	database.add(tempInt);
	    	count++;
	   }
	    return database;
	}
	
	public static void main(String[] args) throws Exception {
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int n = 20000;
		
		ArrayList<int[]> database = readData(filename, splitString, n);
		System.out.println(database.size());
		for(int i = 0; i < 10; i++) {
			Util.print(database.get(i));
		}
		
	}
}
