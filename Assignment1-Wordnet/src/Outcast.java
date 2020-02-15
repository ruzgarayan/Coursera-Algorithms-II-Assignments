import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) // constructor takes a WordNet object
	{
		this.wordnet = wordnet;
	}

	public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
	{
		int maxDistance = 0;
		int maxDistanceIndex = 0;
		int sum;
		for (int i = 0; i < nouns.length; i++) {
			sum = 0;
			for (int j = 0; j < nouns.length; j++) {
				if (i != j)
					sum += wordnet.distance(nouns[i], nouns[j]);
			}
			if (sum > maxDistance) {
				maxDistance = sum;
				maxDistanceIndex = i;
			}
		}

		return nouns[maxDistanceIndex];
	}

	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}