package mmAgent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/**
 * This class represents the MasterMind driver
 *  *** Don't know where to go with it yet.
 * @author Matt Reeping & John Wright
 * @version 12/7/2013
 */
public class MMDriver {
	static ArrayList<Integer> allowedGuesses = new ArrayList<Integer>();
	static ArrayList<Integer> colors = new ArrayList<Integer>(); 
	static int guessNumber = 1;
	static ArrayList<Integer> answer = new ArrayList<Integer>();
	static ArrayList<ArrayList<Integer>> guessed = new ArrayList<ArrayList<Integer>>();
	static Scanner input = new Scanner(System.in);
	
	
	public static void main(String[] args){
		System.out.println("This is the Master Mind driver!!!");
		System.out.println("how many possible colors??");
		int numColors = input.nextInt();
		allowedColors(numColors);
		System.out.println("how many spots??");
		int numSpots = input.nextInt();
		initializeColorsArray(numSpots);
		System.out.println("get answer file: ");
		String fileName = input.next();
		answerToArray(fileName, numSpots);
		ArrayList<Integer> rightColors = getColors(answer);
//		ArrayList<Integer> example = new ArrayList<Integer>();
//		for(int i = 0; i<4; i++){
//			example.add(i);
//		}
		guessAll(rightColors, numSpots);
		
		
		
	}
	
	public static void answerToArray(String FileName, int numSpots){
//		for(int i=0; i<5;i++){
//			answer.add(i);
//		}
		Scanner fileScanner = null;
		try
		{
			FileInputStream file = new FileInputStream(FileName);
			fileScanner = new Scanner(file);

        }
		catch (FileNotFoundException e)
		{
            System.out.println("File not found " + e.getMessage());
            System.exit(-1);
        }
		
		for(int i=0; i<numSpots;i++){
			answer.add(fileScanner.nextInt());
		}
	}
	
	public static void allowedColors(int numColors){
		for(int i = 0; i <= numColors-1; i++){
			allowedGuesses.add(i);
		}
	}
	
	public static void initializeColorsArray(int numSpots){
		for(int i = 0; i< numSpots; i++){
			colors.add(-1);
		}
	}
	
	public static boolean listEquals(ArrayList<Integer> answer2, ArrayList<Integer> list2) {
		boolean test = true;
		for(int i = 0; i < answer2.size(); i++) {
			test = test && answer2.get(i) == list2.get(i);
		}
		return test;
	}
	
	public static int numRightPlace(int[] list1, int[] list2) {
		int counter = 0;
		for(int i = 0; i < list1.length; i++) {
			if(list1[i] == list2[i]) {
				counter++;
			}
		}
		return counter;
	}
	
	public static int numRight(ArrayList<Integer> list1, ArrayList<Integer> answer2) {
		//get the count of each number in the list
		int[] hist1 = histogram(list1);
		int[] hist2 = histogram(answer2);

		Vector<Integer> newHist = new Vector<Integer>();

		//Math.min(hist1.length, hist2.length) finds the shortest list
		for(int i = 0; i < Math.min(hist1.length, hist2.length); i++) {
			//gets the lower number of a certain number. THis is the number of matches for a
			//specific number.  If there are 3 1's in hist1 but only 2 1's in hist2 then there
			//are only 2 matching number
			newHist.add(Math.min(hist1[i], hist2[i]));
		}
		int sum = 0;

		for(int i = 0; i < newHist.size(); i++) {
			//newHist stores the number of matching.  adding it to sum keeps track of numRight
			sum += newHist.get(i);
		}
		return sum;
	}

	public static int max(int[] m) {
		int max = -1;
		for(int i = 0; i < m.length; i++) {
			if(m[i] > max) {
				max = m[i];
			}
		}
		return max;
	}

	public static int[] histogram(int[] list1) {
		int[] hist = new int[max(list1) + 1];
		
		for(int i = 0; i < list1.length; i++) {
			hist[list1[i]]++;
		}
		return hist;
	}
	
	public static int max(ArrayList<Integer> m) {
		int max = -1;
		for(int i = 0; i < m.size(); i++) {
			if(m.get(i) > max) {
				max = m.get(i);
			}
		}
		return max;
	}

	public static int[] histogram(ArrayList<Integer> list1) {
		int[] hist = new int[max(list1) + 1];
		
		for(int i = 0; i < list1.size(); i++) {
			hist[list1.get(i)]++;
		}
		return hist;
	}
	
	public static ArrayList<Integer> getColors(ArrayList<Integer> answer2){
		ArrayList<Integer> correctColors = new ArrayList<Integer>();
		for(int i=0; i<allowedGuesses.size(); i++){
			for(int j=0; j<answer2.size(); j++){
				colors.set(j, allowedGuesses.get(i));
			}
			printGuess(colors);
			guessed.add(colors);
			if(listEquals(answer, colors)){
				System.out.println("SOLVED: " + guessNumber);
				System.exit(0);
			}
			guessNumber ++;
			int rightColor = numRight(colors, answer2);
			for(int k=0; k<rightColor; k++){
				correctColors.add(allowedGuesses.get(i));	
			}
		}
		return correctColors;
	}
	
	public static boolean guessed(ArrayList<Integer> guess){
		for(int i=0;i<guessed.size();i++)
			if(guessed.get(i)==guess){
				return true;
			}
		guessed.add(guess);
		return false;
	}
	
	public static void guessAll(ArrayList<Integer> list1, int k){
		int n = list1.size();
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		for(int i=0; i<n; i++){
			list2.add(-1);
		}
		guessAll(list1, list2, n, 0, k);
	}
	
	public static void guessAll(ArrayList<Integer> l1, ArrayList<Integer> l2, int n, int index, int k){
		if(k==0){
			if(numRight(l2,answer) == answer.size()){
				//guessed.add(l2);
				printGuess(l2);
				if(listEquals(answer, l2)){
					System.out.println("SOLVED: " + guessNumber);
					System.exit(0);
				}
				guessNumber++;
			}
			return;
		}
		for(int i=0; i< n; i++){
			l2.set(index, l1.get(i));
			guessAll(l1, l2, n, index+1, k-1);
		}
	}

	public static void printGuess(ArrayList<Integer> list){
		System.out.print("Guess " + guessNumber + ": ");
		for(int i=0; i<list.size(); i++){
			System.out.print(list.get(i));
		}
		System.out.println();
	}
}
