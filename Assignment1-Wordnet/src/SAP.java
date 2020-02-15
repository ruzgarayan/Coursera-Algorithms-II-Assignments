import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
	private Digraph graph;

	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		graph = new Digraph(G);
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		BreadthFirstDirectedPaths pathsFromV = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths pathsFromW = new BreadthFirstDirectedPaths(graph, w);

		boolean foundPath = false;

		int minLength = Integer.MAX_VALUE;
		for (int i = 0; i < graph.V(); i++) {
			if (pathsFromV.hasPathTo(i) && pathsFromW.hasPathTo(i)) {
				if (pathsFromV.distTo(i) + pathsFromW.distTo(i) < minLength) {
					minLength = pathsFromV.distTo(i) + pathsFromW.distTo(i);
					foundPath = true;
				}
			}
		}

		return (foundPath) ? (minLength) : (-1);
	}

	// a common ancestor of v and w that participates in a shortest ancestral path;
	// -1 if no such path
	public int ancestor(int v, int w) {
		BreadthFirstDirectedPaths pathsFromV = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths pathsFromW = new BreadthFirstDirectedPaths(graph, w);

		int closestAncestor = -1;
		for (int i = 0; i < graph.V(); i++) {
			if (pathsFromV.hasPathTo(i) && pathsFromW.hasPathTo(i)) {
				if (closestAncestor == -1) {
					closestAncestor = i;
				} else if (pathsFromV.distTo(i) + pathsFromW.distTo(i) < pathsFromV.distTo(closestAncestor)
						+ pathsFromW.distTo(closestAncestor)) {
					closestAncestor = i;
				}
			}
		}

		return closestAncestor;
	}

	// length of shortest ancestral path between any vertex in v and any vertex in
	// w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || w == null)
			throw new IllegalArgumentException();
		for (Integer integer : v)
			if (integer == null)
				throw new IllegalArgumentException();
		for (Integer integer : w)
			if (integer == null)
				throw new IllegalArgumentException();
		
		BreadthFirstDirectedPaths pathsFromV = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths pathsFromW = new BreadthFirstDirectedPaths(graph, w);

		boolean foundPath = false;

		int minLength = Integer.MAX_VALUE;
		for (int i = 0; i < graph.V(); i++) {
			//if (pathsFromV.hasPathTo(i) || pathsFromW.hasPathTo(i))
			//	System.out.println(pathsFromV.hasPathTo(i) + " " + pathsFromW.hasPathTo(i) + " " + i);
			if (pathsFromV.hasPathTo(i) && pathsFromW.hasPathTo(i)) {
				if (pathsFromV.distTo(i) + pathsFromW.distTo(i) < minLength) {
					minLength = pathsFromV.distTo(i) + pathsFromW.distTo(i);
					foundPath = true;
				}
			}
		}

		return (foundPath) ? (minLength) : (-1);

	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such
	// path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || w == null)
			throw new IllegalArgumentException();
		for (Integer integer : v)
			if (integer == null)
				throw new IllegalArgumentException();
		for (Integer integer : w)
			if (integer == null)
				throw new IllegalArgumentException();
		
		BreadthFirstDirectedPaths pathsFromV = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths pathsFromW = new BreadthFirstDirectedPaths(graph, w);

		int closestAncestor = -1;
		for (int i = 0; i < graph.V(); i++) {
			if (pathsFromV.hasPathTo(i) && pathsFromW.hasPathTo(i)) {
				if (closestAncestor == -1) {
					closestAncestor = i;
				} else if (pathsFromV.distTo(i) + pathsFromW.distTo(i) < pathsFromV.distTo(closestAncestor)
						+ pathsFromW.distTo(closestAncestor)) {
					closestAncestor = i;
				}
			}
		}

		return closestAncestor;
	}

	// do unit testing of this class
	public static void main(String[] args) {
		In in = new In("C:\\Users\\zzlawlzz\\Desktop\\digraph25.txt");
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
}
