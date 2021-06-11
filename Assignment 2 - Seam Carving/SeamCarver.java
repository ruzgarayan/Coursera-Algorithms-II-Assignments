import java.awt.Color;
import java.io.File;

import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdDraw;

public class SeamCarver {
	private int[][] pixels;
	private double[][] energy;
	private int width, height;
	

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		if (picture == null)
			throw new IllegalArgumentException();

		width = picture.width();
		height = picture.height();
		
		//System.out.println(width);
		//System.out.println(height);
		
		pixels = new int[width][height];
		energy = new double[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i][j] = picture.getRGB(i, j);
			}
		}
		recalculateEnergies();
	}

	// current picture
	public Picture picture() {
		Picture newPicture = new Picture(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newPicture.setRGB(i, j, pixels[i][j]);
			}
		}
		return newPicture;
	}

	// width of current picture
	public int width() {
		return width;
	}

	// height of current picture
	public int height() {
		return height;
	}

	private int red(int rgb) {
	    return (rgb >> 16) & 0xFF;
	}

	private int green(int rgb) {
	    return (rgb >> 8) & 0xFF;
	}

	private int blue(int rgb) {
	    return (rgb) & 0xFF;
	}
	
	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			throw new IllegalArgumentException();
		if (x == 0 || x == (width - 1) || y == 0 || y == (height - 1))
			return 1000;

		int r_x = red(pixels[x + 1][y]) - red(pixels[x - 1][y]);
		int g_x = green(pixels[x + 1][y]) - green(pixels[x - 1][y]);
		int b_x = blue(pixels[x + 1][y]) - blue(pixels[x - 1][y]);

		int r_y = red(pixels[x][y + 1]) - red(pixels[x][y - 1]);
		int g_y = green(pixels[x][y + 1]) - green(pixels[x][y - 1]);
		int b_y = blue(pixels[x][y + 1]) - blue(pixels[x][y - 1]);

		int delta_x_squared = (r_x * r_x) + (g_x * g_x) + (b_x * b_x);
		int delta_y_squared = (r_y * r_y) + (g_y * g_y) + (b_y * b_y);

		return Math.sqrt(delta_x_squared + delta_y_squared);
	}

	private void recalculateEnergies()
	{
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				energy[i][j] = energy(i, j);
			}
		}
	}
	
	private double computeVerticalSingleSourceSP(int s, int[] returnedSeam)
	{
		double[] distTo = new double[width * height];
		int[] edgeTo1 = new int[width * height];
		int[] edgeTo2 = new int[width * height];
		
		for (int i = 0; i < distTo.length; i++)
			distTo[i] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;
		
		for (int i = 0; i < height - 1; i++) {
			for (int j = 0; j < width; j++) {
				int startingVertex = mapCoordinates(j, i);
				if (j != 0) {
					int leftVertex = mapCoordinates(j - 1, i + 1);
					relax(distTo, edgeTo1, edgeTo2, startingVertex, leftVertex);
				}
				if (j != width - 1) {
					int rightVertex = mapCoordinates(j + 1, i + 1);
					relax(distTo, edgeTo1, edgeTo2, startingVertex, rightVertex);
				}
				
				int middleVertex = mapCoordinates(j, i + 1);
				relax(distTo, edgeTo1, edgeTo2, startingVertex, middleVertex);
			}
		}
		
		int minPathStarting = 0;
		int minPathEnding = 0;
		double minPathLength = Double.POSITIVE_INFINITY;
		
		for (int j = 0; j < width; j++) {
			if (distTo[mapCoordinates(j, height - 1)] < minPathLength) {
				minPathLength = distTo[mapCoordinates(j, height - 1)];
				minPathStarting = mapCoordinates(s, 0);
				minPathEnding = mapCoordinates(j, height - 1);
			}
		}
		
		int nextVertex = minPathEnding;
		for (int i = height - 1; i >= 0; i--) {
			returnedSeam[i] = mapBackToX(nextVertex);
			nextVertex = edgeTo2[nextVertex];
		}
		
		return minPathLength;
	}
	
	private double computeHorizontalSingleSourceSP(int s, int[] returnedSeam)
	{
		double[] distTo = new double[width * height];
		int[] edgeTo1 = new int[width * height];
		int[] edgeTo2 = new int[width * height];
		
		for (int i = 0; i < distTo.length; i++)
			distTo[i] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;
		
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height; j++) {
				int startingVertex = mapCoordinates(i, j);
				if (j != 0) {
					int upVertex = mapCoordinates(i + 1, j - 1);
					relax(distTo, edgeTo1, edgeTo2, startingVertex, upVertex);
				}
				if (j != height - 1) {
					int downVertex = mapCoordinates(i + 1, j + 1);
					relax(distTo, edgeTo1, edgeTo2, startingVertex, downVertex);
				}
				
				int middleVertex = mapCoordinates(i + 1, j);
				relax(distTo, edgeTo1, edgeTo2, startingVertex, middleVertex);
			}
		}
		
		int minPathStarting = 0;
		int minPathEnding = 0;
		double minPathLength = Double.POSITIVE_INFINITY;
		
		for (int j = 0; j < height; j++) {
			if (distTo[mapCoordinates(width - 1, j)] < minPathLength) {
				minPathLength = distTo[mapCoordinates(width - 1, j)];
				minPathStarting = mapCoordinates(0, s);
				minPathEnding = mapCoordinates(width - 1, j);
			}
		}
		
		int nextVertex = minPathEnding;
		for (int i = width - 1; i >= 0; i--) {
			returnedSeam[i] = mapBackToY(nextVertex);
			nextVertex = edgeTo2[nextVertex];
		}
		
		return minPathLength;
	}
	
	private void relax(double[] distTo, int[] edgeTo1, int[] edgeTo2, int from, int to )
	{
		//System.out.println(mapBackToX(to) + " " + mapBackToY(to));
		if (distTo[to] > distTo[from] + energy[mapBackToX(to)][mapBackToY(to)]) {
            distTo[to] = distTo[from] + energy[mapBackToX(to)][mapBackToY(to)];
            edgeTo1[to] = to;
            edgeTo2[to] = from;
        }
	}
	
	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		int[] seam = new int[width];
		double minPathLength = Double.POSITIVE_INFINITY;
		int startingVertex = 0;
		for (int i = 0; i < height; i++) {
			double returnedLength = computeHorizontalSingleSourceSP(mapCoordinates(0, i), seam);
			if (returnedLength < minPathLength)
			{
				minPathLength = returnedLength;
				startingVertex = mapCoordinates(0, i);
			}
		}
		
		//System.out.println("minPathStarting: " + mapBackToX(minPathStarting) + " " + mapBackToY(minPathStarting));
		//System.out.println("minPathEnding: " + mapBackToX(minPathEnding) + " " + mapBackToY(minPathEnding));
		
		computeHorizontalSingleSourceSP(startingVertex, seam);
		return seam;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		int[] seam = new int[height];
		double minPathLength = Double.POSITIVE_INFINITY;
		int startingVertex = 0;
		for (int i = 0; i < width; i++) {
			double returnedLength = computeVerticalSingleSourceSP(mapCoordinates(i, 0), seam);
			if (returnedLength < minPathLength)
			{
				minPathLength = returnedLength;
				startingVertex = mapCoordinates(i, 0);
			}
		}
		
		//System.out.println("minPathStarting: " + mapBackToX(minPathStarting) + " " + mapBackToY(minPathStarting));
		//System.out.println("minPathEnding: " + mapBackToX(minPathEnding) + " " + mapBackToY(minPathEnding));
		
		computeVerticalSingleSourceSP(startingVertex, seam);
		return seam;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null || seam.length != width || height <= 1)
			throw new IllegalArgumentException();

		for (int i = 0; i < width; i++) {
			//System.out.println(seam[i]);
			if (seam[i] < 0 || seam[i] >= height || (i != width -1 && (seam[i] - seam[i + 1] > 1 || seam[i] - seam[i + 1] < -1)))
				throw new IllegalArgumentException();
			
			for (int j = seam[i]; j < height - 1; j++) {
				pixels[i][j] = pixels[i][j + 1];
			}
		}

		height--;
		recalculateEnergies();
	}
	
	
	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		if (seam == null || seam.length != height || width <= 1)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < height; i++) {
			//System.out.println(seam[i]);
			if (seam[i] < 0 || seam[i] >= width || (i != height -1 && (seam[i] - seam[i + 1] > 1 || seam[i] - seam[i + 1] < -1)))
				throw new IllegalArgumentException();

			for (int j = seam[i]; j < width - 1; j++) {
				pixels[j][i] = pixels[j + 1][i];
			}
		}

		width--;
		recalculateEnergies();
	}

	private int mapCoordinates(int x, int y) {
		return y * width + x;
	}
	
	private int mapBackToX(int a)
	{
		return a % width;
	}
	
	private int mapBackToY(int a)
	{	
		return a / width;
	}
	
	//unit testing (optional)
	public static void main(String[] args) {
		Picture p = new Picture(new File("C:\\Users\\zzlawlzz\\eclipse-workspace\\Seam Carving\\src\\deneme.png"));
		SeamCarver sc = new SeamCarver(p);
		for (int i = 0; i < 10; i++)
		{
			int[] seam = sc.findHorizontalSeam();
			sc.removeHorizontalSeam(seam);
		}
		Picture result = sc.picture();
		result.save("C:\\Users\\zzlawlzz\\eclipse-workspace\\Seam Carving\\src\\result.png");
	}
}
