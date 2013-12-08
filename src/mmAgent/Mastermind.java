package mmAgent;

import java.io.*;
import java.util.*;

public class Mastermind {

    public static void main(String[] args) {
	int[] toGuess = { 1, 2, 3, 4};
	
	String command = "timeout 1000 ./" + args[0];

	try {
	
	    Process child = Runtime.getRuntime().exec(command);
	    PrintWriter childOutput = new PrintWriter(child.getOutputStream());
	    Scanner childInput = new Scanner(child.getInputStream());
	    
	    int[] guess = { -1, -1, -1, -1} ;
	    childOutput.write(5 + " " + 4); // Number of possible keys followed by number of pegs
	    while(! listEquals(guess, toGuess)) {

		String[] stringGuess = childInput.nextLine().split(" ");
	    
		for(int i = 0; i < stringGuess.length; i++) {
		    guess[i] = Integer.parseInt(stringGuess[i]);
		}
		childOutput.println(numRightPlace(toGuess, guess) + " " + guess);
		childOutput.flush();
	    }
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
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
	int[] hist1 = histogram(list1);
	int[] hist2 = histogram(list2);

	Vector<Integer> newHist = new Vector<Integer>();

	for(int i = 0; i < Math.min(hist1.length, hist2.length); i++) {
	    newHist.add(Math.min(hist1[i], hist2[i]));
	}

	int sum = 0;

	for(int i = 0; i < newHist.size(); i++) {
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

    public static boolean listEquals(int[] list1, int[] list2) {
	boolean test = true;
	for(int i = 0; i < list1.length; i++) {
	    test = test && list1[i] == list2[i];
	}
	return test;
    }

}
