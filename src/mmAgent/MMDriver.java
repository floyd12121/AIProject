package mmAgent;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class represents the MasterMind driver
 *  *** Don't know where to go with it yet.
 * @author Matt Reeping & John Wright
 * @version 12/7/2013
 */
public class MMDriver {
	static int[] allowedGuesses = {0,1,2,3,4,5,6,7,8,9};
	static int[] colors = {-1,-1,-1,-1,-1}; 
	static int guessNumber = 1;
	static int[] answer = {1,3,1,8,7};
	static int[] tester = {1,2,3,4,5};
	static ArrayList<int[]> guessed = new ArrayList<int[]>();
	
	public static void main(String[] args){
		System.out.println("This is the Master Mind driver!!!");
		int[] test = getColors(answer);
		guessAll(test, 5);
		
	}
	
	public static boolean listEquals(int[] list1, int[] list2) {
		boolean test = true;
		for(int i = 0; i < list1.length; i++) {
			test = test && list1[i] == list2[i];
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
	
	public static int numRight(int[] list1, int[] list2) {
		//get the count of each number in the list
		int[] hist1 = histogram(list1);
		int[] hist2 = histogram(list2);

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
	
	public static int[] getColors(int[] list){
		int[] correctColors = {-1,-1,-1,-1,-1};
		int counter = 0;
		for(int i=0; i<allowedGuesses.length; i++){
			for(int j=0; j<list.length; j++){
				colors[j] = allowedGuesses[i];
			}
			printGuess(colors);
			guessed.add(colors);
			if(listEquals(answer, colors)){
				System.out.println("SOLVED: " + guessNumber);
				System.exit(0);
			}
			guessNumber ++;
			int rightColor = numRight(colors, list);
			for(int k=0; k<rightColor; k++){
				correctColors[counter] = allowedGuesses[i];
				counter++;
			}
		}
		return correctColors;
	}
	
	public static boolean guessed(int[] guess){
		for(int i=0;i<guessed.size();i++)
			if(guessed.get(i)==guess){
				return true;
			}
		guessed.add(guess);
		return false;
	}
	
	public static void guessAll(int[] list1, int k){
		int n = list1.length;
		int[] list2 = {-1,-1,-1,-1,-1};
		guessAll(list1, list2, n, 0, k);
	}
	
	public static void guessAll(int[] l1, int[] l2, int n, int index, int k){
		if(k==0){
			if(numRight(answer,l2) == 5){
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
			l2[index] = l1[i];
			guessAll(l1, l2, n, index+1, k-1);
		}
	}

	public static void printGuess(int[] list){
		System.out.print("Guess " + guessNumber + ": ");
		for(int i=0; i<list.length; i++){
			System.out.print(list[i]);
		}
		System.out.println();
	}
}
