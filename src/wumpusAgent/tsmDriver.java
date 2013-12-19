package tsm;

import java.awt.Point;
import java.io.*;
import java.util.*;

/**
 * This class represents the Traveling Salesman driver class
 * This agent will attempt to solve randomly generated maps, listed as x,y coordinates,
 * for the shortest path, while only touching each coordinate (city) only once.
 * The base algorithm is nearest neighbor search and the more advanced search 
 * is one created by Matt Reeping and John Wright (method called reapItRight()).
 * The project write up will have better details pertaining to the results and function of each method.
 * In order to run this file: Follow the instructions prompted on the command prompt.
 * @author Matt Reeping & John Wright
 * @version 12/7/2013
 */
public class tsmDriver {

	static Scanner stdio = new Scanner(System.in);
	static ArrayList<Point> original, path;
	static double disNN=100000, disBiNN=100000, tempDis=0;
	
	
	public static void main(String[] args){
		original = new ArrayList<Point>(100);
		System.out.println("Enter a seed number for creating a map: ");
		int seed = stdio.nextInt();
		System.out.println("Enter the number of cities in this problem (N>100): ");
		openAndReadFile(generateRandomMap(seed, stdio.nextInt()));
		
		// Finds the shortest path using Nearest Neighbor Algorithm on each city
		path = nnAlgor(new ArrayList<Point>(original), 0); // Sends a copy of the original map
		for(int i=1; i<original.size(); i++){
			tempDis=0;
			ArrayList<Point> temp = nnAlgor(new ArrayList<Point>(original), i);
			// Adds the first city back to the equation including its distance from the last city
			tempDis += distance(temp.get(0), temp.get(temp.size()-1));
			temp.add(temp.get(0));
			
			if(tempDis<disNN){ // Find the shortest path
				path = temp;
				disNN = tempDis;
				
			}
		}
		System.out.println("Nearest Neighbor's shortest path:\n"+pointList(path));
		System.out.printf("Shortest distance: %.4f\n", disNN);
		// Uses the new algo by Matt and John
		path = reapItRight(new ArrayList<Point>(original));
		System.out.println("Not so much smarter Algorithm's shortest path:\n"+pointList(path));
		System.out.printf("Shortest distance: %.4f\n", disBiNN);
	}
	
	
	/*********************************** Methods ***************************************/
	
	public static String pointList(ArrayList<Point> list){
		String str = "";
		for(Point p: list)
			str+= "("+p.x+", "+p.y+"), ";
		return str;
	}
	/**
	 * John's and Matt's algorithm which hopefully works better than naive nearest neighbor.
	 * @return The shortest path
	 */
	public static ArrayList<Point> reapItRight(ArrayList<Point> map){
		int maxX = maxX(map);
		int maxY = maxY(map);
		int hX = maxX/2, hY = maxY/2;
		
		// Set the four quadrants for further use
		ArrayList<Point> q1 = new ArrayList<Point>();
		q1.add(new Point(hX, (int) (maxY*.75)));
		ArrayList<Point> q2 = new ArrayList<Point>();
		q2.add(new Point(hX/2, hY));
		ArrayList<Point> q3 = new ArrayList<Point>();
		q3.add(new Point(hX, hY/2));
		ArrayList<Point> q4 = new ArrayList<Point>();
		q4.add(new Point((int) (maxX*.75), hY));
		Iterator<Point> iter = map.iterator();
		while(iter.hasNext()){
			Point p = iter.next();
			
			if(p.x<hX && p.y>=hX) // Q2
				addToList(p, q2);
			else if(p.x<hX && p.y<hY) // Q3
				addToList(p, q3);
			else if(p.x>=hX && p.y<hY) // Q4
				addToList(p, q4);
			else if(p.x>=hX && p.y>=hY) // Q1
				addToList(p, q1);
			else{
				System.err.println(p +" does not exist within the bindings of this map. Exiting.");
				System.exit(0);
			}
		}
		
		// Find path for Q1
		double dis1 = 100000;
		ArrayList<Point> path1 = nnAlgor(new ArrayList<Point>(q1), 0); // Sends a copy of the original map
		for(int i=1; i<q1.size(); i++){
			tempDis=0;
			ArrayList<Point> temp = nnAlgor(new ArrayList<Point>(q1), i);
			tempDis += distance(q4.get(0), temp.get(temp.size()-1));
			if(tempDis<dis1){ // Find the shortest path
				path1 = temp;
				dis1 = tempDis;
			}
		}
		
		// Find path for Q2
		double dis2 = 100000;
		ArrayList<Point> path2 = nnAlgor(new ArrayList<Point>(q2), 0); // Sends a copy of the original map
		for(int i=1; i<q2.size(); i++){
			tempDis=0;
			ArrayList<Point> temp = nnAlgor(new ArrayList<Point>(q2), i);
			tempDis += distance(q1.get(0), temp.get(temp.size()-1));
			if(tempDis<dis2){ // Find the shortest path
				path2 = temp;
				dis2 = tempDis;
			}
		}
		
		// Find path for Q3
		double dis3 = 100000;
		ArrayList<Point> path3 = nnAlgor(new ArrayList<Point>(q3), 0); // Sends a copy of the original map
		for(int i=1; i<q3.size(); i++){
			tempDis=0;
			ArrayList<Point> temp = nnAlgor(new ArrayList<Point>(q3), i);
			tempDis += distance(q2.get(0), temp.get(temp.size()-1));
			if(tempDis<dis3){ // Find the shortest path
				path3 = temp;
				dis3 = tempDis;
				
			}
		}
		
		// Find path for Q4
		double dis4 = 100000;
		ArrayList<Point> path4 = nnAlgor(new ArrayList<Point>(q4), 0); // Sends a copy of the original map
		for(int i=1; i<q4.size(); i++){
			tempDis=0;
			ArrayList<Point> temp = nnAlgor(new ArrayList<Point>(q4), i);
			tempDis += distance(q3.get(0), temp.get(temp.size()-1));
			if(tempDis<dis4){ // Find the shortest path
				path4 = temp;
				dis4 = tempDis;
				
			}
		}
		 // Remove the template node which started the search
		dis1 -= distance(q1.get(0), q1.get(1));
		q1.remove(0);
		dis2 -= distance(q2.get(0), q2.get(1));
		q2.remove(0);
		dis3 -= distance(q3.get(0), q3.get(1));
		q3.remove(0);
		dis4 -= distance(q4.get(0), q4.get(1));
		q4.remove(0);

		ArrayList<Point> path = new ArrayList<Point>();
		double distance=0;
		
		// Add all the paths together to make one path
		path.addAll(q1);
		distance += dis1;
		distance += distance(q1.get(q1.size()-1), q4.get(0));
		path.addAll(q4);
		distance += dis4;
		distance += distance(q4.get(q4.size()-1), q3.get(0));
		path.addAll(q3);
		distance += dis3;
		distance += distance(q3.get(q3.size()-1), q2.get(0));
		path.addAll(q2);
		distance += dis2;
		distance += distance(q2.get(q2.size()-1), q1.get(0));
		path.add(q1.get(0));
		
		disBiNN = distance;
		return path;
	}
	
	/**
	 * Find the max X in the list
	 * @param list List containing all points
	 * @return Max X
	 */
	public static int maxX(ArrayList<Point> list){
		int max = list.get(0).x;
		for(Point p: list){
			if(p.x>max)
				max = p.x;
		}
		return max;
	}
	
	/**
	 * Find the max y in the list
	 * @param list List containing all points
	 * @return Max Y
	 */
	public static int maxY(ArrayList<Point> list){
		int max = list.get(0).y;
		for(Point p: list){
			if(p.y>max)
				max = p.y;
		}
		return max;
	}
	public static ArrayList<Point> nnAlgor(ArrayList<Point> toVisit, int index){
		ArrayList<Point> solution = new ArrayList<Point>(toVisit.size());
		// choose a start based on the index given 
		addToList(toVisit.get(index), solution);
		toVisit.remove(index);
		while(!toVisit.isEmpty()){
			Point current = solution.get(solution.size()-1);
			Point closest = null;
			double min = 10000000;
			for(int i=0; i<toVisit.size(); i++){
				double dis = distance(current, toVisit.get(i));
				if( dis < min){
					min = dis;
					closest = toVisit.get(i);
				}
			}
			tempDis += min;
			addToList(closest, solution);
			toVisit.remove(closest);
		}
		return solution;
	}
	
	public static double distance(Point here, Point there){
		return Math.sqrt(Math.pow(here.x-there.x, 2) + Math.pow(here.y-there.y,2));
	}
	
	/**
	 * Generates a random file based off a seed file
	 * @param seed Seed for random number generator
	 * @return The name of the file containing the map
	 */
	public static String generateRandomMap(int seed, int size){
		String filename = "cities.txt";
		try {
			PrintWriter file = new PrintWriter(new File(filename));
			Random number = new Random(seed);
			for(int i=0; i<size; i++){
				int a = number.nextInt(100);
				int b = number.nextInt(100);
				file.println(a + " " + b);
			}
			file.close();
		} catch (FileNotFoundException e) {
			System.err.println("The file was not found, program will now terminate.");
			System.exit(0);
		}
		return filename;
	}
	/**
	 * Opens and reads from the file containing the coordinates to the "original" list.
	 * @param fileName Name of the file to open
	 */
	public static void openAndReadFile(String fileName){
		try {
			FileInputStream stream = new FileInputStream(fileName);
			Scanner input = new Scanner(stream);
			
			while(input.hasNext()){
				Point point = new Point(input.nextInt(), input.nextInt());
				if(!addToList(point, original))
					System.err.println("( "+point.x+", "+point.y+" ) is already contained in the list.");
			}
		} catch (FileNotFoundException e) {
			System.err.println("The file was not found, program will now terminate.");
			System.exit(0);
		}
		
	}
	
	/**
	 * Adds to a list only if the point is not already contained in the list
	 * @param p Point to add
	 * @param list List adding to
	 * @return Return true if point not contained, false otherwise
	 */
	public static boolean addToList(Point p, ArrayList<Point> list){
		if(list.contains(p))
			return false;
		// else add and return true
		list.add(p);
		return true;
	}
}
