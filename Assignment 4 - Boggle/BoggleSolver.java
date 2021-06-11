import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver
{
    private TST trieSET; //USE TST
    private List<Integer[]>[][] neighbors;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary)
    {
        trieSET = new TST();
        for (int i = 0 ; i < dictionary.length; i++)
            trieSET.put(dictionary[i], i);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        int r = board.rows();
        int c = board.cols();
        Set<String> allWords = new HashSet<>();
        neighbors = new List[r][c];

        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                neighbors[i][j] = new ArrayList<>();
                for (int i2 = i - 1; i2 <= i + 1; i2++)
                {
                    for (int j2 = j - 1; j2 <= j + 1; j2++)
                    {
                        if (i2 < board.rows() && i2 >= 0 && j2 < board.cols() && j2 >= 0)
                        {
                            Integer[] pair = new Integer[2];
                            pair[0] = i2;
                            pair[1] = j2;
                            neighbors[i][j].add(pair);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                searchAllStartingFrom(i,j, board, new boolean[r][c] ,"", allWords);
            }
        }
        return allWords;
    }

    private void searchAllStartingFrom(int x, int y, BoggleBoard bb, boolean[][] visited, String curr, Set<String> allWords)
    {
        char nextLetter = bb.getLetter(x,y);
        curr = curr + (nextLetter == 'Q' ? "QU" : nextLetter);
        if (trieSET.contains(curr) && curr.length() >= 3) allWords.add(curr);
        else {
            boolean empty = true;
            Iterable<String> prefix = trieSET.keysWithPrefix(curr);
            for (String s : prefix) {
                empty = false;
                break;
            }
            if (empty)
            {
                visited[x][y] = false;
                return;
            }
        }

        visited[x][y] = true;
        for (Integer[] pair : neighbors[x][y])
            if (!visited[pair[0]][pair[1]])
                searchAllStartingFrom(pair[0], pair[1], bb, visited, curr, allWords);
        visited[x][y] = false;
    }


    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word)
    {
        if (trieSET.contains(word))
        {
            if (word.length() == 3 || word.length() == 4)
                return 1;
            else if (word.length() == 5)
                return 2;
            else if (word.length() == 6)
                return 3;
            else if (word.length() == 7)
                return 5;
            else if (word.length() >= 8)
                return 11;
            else
                return 0;
        }
        else
            return 0;
    }

    public static void main(String[] args) {
        In in = new In("dictionary-yawl.txt");
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard("board-points100.txt");

        for (int i = 0; i < 1000; i++)
        {
            solver.getAllValidWords(board);
            System.out.println(i);
        }


        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);

    }
}