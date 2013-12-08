package wumpusAgent;

import java.io.*;

import java.util.*;

public class UltimateWumpus {
	public static final int SIZE = 1000;
	public static final int MAX_MOVES = 1000;
	public static final int MAX_KEY = 10;
	
	
	public static void main(String[] args) {

		try {
			StaticGraph graph =  generateGraph(SIZE);
			StaticGraph graph2 =  generateGraph(SIZE);

			Process team1 = Runtime.getRuntime().exec(args[0]);
			Process team2 = Runtime.getRuntime().exec(args[1]);
			
			PrintWriter print1 = new PrintWriter(team1.getOutputStream());
			PrintWriter print2 = new PrintWriter(team2.getOutputStream());

			Scanner s1 = new Scanner(team1.getInputStream());
			Scanner s2 = new Scanner(team2.getInputStream());

			InputThing scan1 = new InputThing(s1);	
			InputThing scan2 = new InputThing(s2);

			scan1.start();
			scan2.start();

			int[] keys1 = getKeys();
			int[] keys2 = getKeys();
			
			Hashtable<String, Integer> keyTable1 = new Hashtable<String, Integer>();
			Hashtable<String, Integer> keyTable2 = new Hashtable<String, Integer>();
			
			
			sendGraph(print1, graph);
			sendGraph(print2, graph);

			sendKeys(keys1, print1);
			sendKeys(keys2, print2);
			
			assignKeys(keyTable1, graph);
			assignKeys(keyTable2, graph2);
	
			sendKeyList(print1, keyTable1);
			sendKeyList(print2, keyTable2);

			Vector<String> moves1 = new Vector<String>();
			Vector<String> moves2 = new Vector<String>();

			moves1.add("1");
			moves2.add("1");

			String tradeBuffer1, tradeBuffer2;
			tradeBuffer1 = tradeBuffer2 = null;
			
			for(int moveCounter = 0; moveCounter < MAX_MOVES || 
(moves1.get(moves1.size()-1).equals("g") && moves2.get(moves2.size()-1).equals("g")); moveCounter++) {
				try {
					Thread.sleep(100);
				}
				catch(Exception e) {
					
				}
				
				Hashtable<String, String> commands1 = scan1.readUntilMove();
				Hashtable<String, String> commands2 = scan2.readUntilMove();

				if(commands1 != null) {
					processMove(commands1.get("Move"), graph, moves1, print1, 
keyTable1, keys1);


					if(commands1.get("Trade") != null) {
						tradeBuffer1 = commands1.get("Trade");					
						print2.write("Request " + commands1.get("Trade").split(" ")[1] + " " + commands1.get("Trade").split(" ")[2] + "\n");
						print2.flush();
					}
					if(commands1.get("Response") != null) {
						handleTrade(tradeBuffer2, commands1.get("Response"), keys2, 
keys1, print2, print1);
					}
				}

				if(commands2 != null) {
					processMove(commands2.get("Move"), graph2, moves2, print2, 
keyTable2, keys2);
					if(commands2.get("Trade") != null) {
						tradeBuffer2 = commands2.get("Trade");
						print1.write("Request " + commands2.get("Trade").split(" ")[1] + " " + commands2.get("Trade").split(" ")[2] + "\n");
						print1.flush();
					}
					if(commands2.get("Response") != null) {
						handleTrade(tradeBuffer1, commands2.get("Response"), keys1, 
keys2, print1, print2);
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Trade keyOffered keyDesired (Trade | Defect)
	//Response (Accept | Reject) keyOfferd keyDesired (Trade | Defect)

	public static void handleTrade(String proposed, String response, int[] keys1, int[] keys2, 
PrintWriter print1, PrintWriter print2) {
		System.out.println("&" + proposed + " " + response + " &");
		if(! response.contains("Accept") || proposed == null) {
			return;	
		}
		String[] parseOfTrade = proposed.split(" ");
		String[] parseOfResponse = response.split(" ");

		if(! parseOfTrade[1].equals(parseOfResponse[2]) || ! 
parseOfTrade[2].equals((parseOfResponse[3]))) {
			return;
		}

		int[] results = {-1, -1};
		if(parseOfTrade[3].equals("Trade")) {
			results[1] = Integer.parseInt(parseOfTrade[1]);
		}
		if(parseOfResponse[4].equals("Trade")) {
			results[0] = Integer.parseInt(parseOfTrade[2]);
		}
		if(results[0] > -1) {
			keys1[results[0]]++;

		}
		if(results[1] > -1) {
			keys1[results[1]]++;

		}
		print2.write("Key " + results[1] + "\n");
		print2.flush();
		print1.write("Key " + results[0] + "\n");
		print1.flush();

	}


	public static void processMove(String move, StaticGraph g, Vector<String> moves, PrintWriter 
playerOut, Hashtable<String, Integer> keyTable, int[] keys) {
		if(move == null) {
			return;
		}
		String moveTo = move.split(" ")[1];
		String lastMove = moves.get(moves.size() - 1);
		if(g.getEdge(lastMove, moveTo) > 0 && keys[keyTable.get(moveTo)] > 0) {
			keys[keyTable.get(moveTo)]--;
			moves.add(moveTo);
			playerOut.write("At " + moveTo + "\n");
			playerOut.flush();
		}
	}

	public static void sendKeys(int[] keys, PrintWriter output) {
		output.write("Keys\n");
		for(int i = 0; i < keys.length; i++) {
			output.write(keys[i] + "\n");
		}
		output.write("End Keys\n");
		output.flush();
	}

	public static StaticGraph generateGraph(int size) {
		StaticGraph g = new StaticGraph();
		for(int i = 0; i < size; i++) {
			g.addNode(i+"");
			int rand = ((int) (Math.random() * 3)) + 1;
			for(int j = 0; j < rand; j++) {
				g.addSymLink(i+"", g.getNodes().get((int) (Math.random()) * 
g.getNodes().size()));
			}
		}
		return g;
	}

	public static void sendGraph(PrintWriter s1, StaticGraph g) {
		s1.print("Graph\n");
		Vector<String> nodes = g.getNodes();
		for(String node1 : nodes) {
			for(String node2 : nodes) {
				if(g.getEdge(node1, node2) > 0) {
					s1.println(node1 + " " + node2);
				}
			}
		}
		s1.print("End Graph\n");
		s1.flush();
	}
	
	public static int[] getKeys() {
		int[] keys = new int[10];
		for(int i = 0; i < 10; i++) {
			keys[i] = 1;
		}
		return keys;
	}
	public static void assignKeys(Hashtable<String, Integer> keytable, StaticGraph g) {
		Vector<String> nodes = g.getNodes();
		
		for(int i = 0; i < SIZE / 10; i++) {
			String node = nodes.get((int)( Math.random()*nodes.size()));
			Integer key = new Integer((int) (Math.random() * MAX_KEY));
			keytable.put(node, key );
		}


	}
	
	public static void sendKeyList(PrintWriter s1, Hashtable<String, Integer> keytable) {
		Set<String> keys = keytable.keySet();
		s1.print("KeyTable\n");
		for(String s : keys) {
			s1.print(s + " " + keytable.get(s) + "\n");
		}
		s1.print("End\n");
		s1.flush();
	}

	

	

}





class InputThing extends Thread {
	public Vector<String> buffer = new Vector<String>();
	public Scanner s;

	public InputThing(Scanner s) {
		this.s = s;
	}

	public void run() {
		while(true) {
			if(s.hasNext()) {
				buffer.add(s.nextLine());
				System.out.println(buffer.get(buffer.size()-1));
			}
		}
	}

	public Hashtable<String, String> readUntilMove() {
		//System.out.println("Called " + buffer.size());
		if(empty())
			return null;
		Hashtable<String, String> results = new Hashtable<String, String>();
		while(! empty() && ! buffer.get(0).contains("Move")) {
			String s = pop();
			if(s.contains("Response")) {
				results.put("Response", s);
			}
			else if(s.contains("Trade")) {
				results.put("Trade", s);
			}
		}
		if(! empty())
			results.put("Move", pop());
		return results;
	}



	public String pop() {
		if(buffer.size() == 0) {
			return null;
		}
		return buffer.remove(0);
	}

	public boolean hasNext() {
		return buffer.size() != 0;
	}

	public int size() {
		return buffer.size();
	}

	public boolean empty() {
		return buffer.size() == 0;
	}

	public String peek() {
		if(buffer.size() > 0) {
			return null;
		}
		return buffer.get(0);
	}

}

class StaticGraph {

	private Hashtable<String, Vector<String>> edges = new Hashtable<String, Vector<String>>();
	
	public Vector<String> getNodes() {
		return new Vector<String>(edges.keySet());
	}

	public void addNode(String string) {
		if(! edges.containsKey(string)) {
			edges.put(string, new Vector<String>());
		}
		
	}

	public void addSymLink(String string, String string2) {
		addLink(string, string2);
		addLink(string2, string);
		
	}
	
	public void addLink(String a, String b) {
		addNode(a);
		addNode(b);
		edges.get(a).add(b);
	}

	public int getEdge(String lastMove, String moveTo) {
		if(edges.get(lastMove).contains(moveTo)){
			return 1;
		}
		else {
			return 0;
		}
	}

}


