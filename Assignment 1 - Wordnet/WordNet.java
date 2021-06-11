import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

public class WordNet {

	private Digraph graph;
	private HashMap<Integer, String> idToNouns;
	private HashMap<String, ArrayList<Integer>> nounToId;
	private SAP sap;

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		if (synsets == null || hypernyms == null)
			throw new IllegalArgumentException();
		
		idToNouns = new HashMap<Integer, String>();
		nounToId = new HashMap<String, ArrayList<Integer>>();

		In in1 = new In(synsets);
		In in2 = new In(hypernyms);

		while (in1.hasNextLine()) {
			String line = in1.readLine();
			String[] parts = line.split(",");
			int id = Integer.parseInt(parts[0]);
			idToNouns.put(id, parts[1]);

			String[] nouns = parts[1].split(" ");
			for (String s : nouns) {
				if (nounToId.containsKey(s))
					nounToId.get(s).add(id);
				else {
					nounToId.put(s, new ArrayList<Integer>());
					nounToId.get(s).add(id);
				}
			}
		}

		graph = new Digraph(idToNouns.size());

		while (in2.hasNextLine()) {
			String line = in2.readLine();
			String[] parts = line.split(",");
			for (int i = 1; i < parts.length; i++) {
				graph.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[i]));
			}
		}
		
		if (!isRootedDAG(graph))
			throw new IllegalArgumentException(); 
		
		sap = new SAP(graph);
	}
	
	private boolean isRootedDAG(Digraph g)
	{
		DirectedCycle dc = new DirectedCycle(g);
			if (dc.hasCycle())
				return false;
		
		int rootCount = 0;
		int adjCount;
		for (int i = 0; i < g.V(); i++)
		{
			adjCount = 0;
			for (int adj : g.adj(i))
			{
				adjCount++;
			}
			if (adjCount == 0)
				rootCount++;
		}
		
		if (rootCount != 1)
			return false;
		
		return true;
	}

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return nounToId.keySet();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		if (word == null)
			throw new IllegalArgumentException(); 
		return nounToId.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		if (nounA == null || nounB == null)
			throw new IllegalArgumentException();
		return sap.length(nounToId.get(nounA), nounToId.get(nounB));
	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA
	// and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {
		if (nounA == null || nounB == null)
			throw new IllegalArgumentException();
		return idToNouns.get(sap.ancestor(nounToId.get(nounA), nounToId.get(nounB)));
	}

	// do unit testing of this class
	public static void main(String[] args) {
		WordNet deneme = new WordNet("C:\\Users\\zzlawlzz\\Desktop\\synsets.txt",
				"C:\\Users\\zzlawlzz\\Desktop\\hypernyms.txt");
		System.out.println(deneme.sap("arm", "leg"));
		System.out.println(deneme.distance("arm", "eyes"));
		System.out.println(deneme.distance("eyes", "mouth"));
	}

}
