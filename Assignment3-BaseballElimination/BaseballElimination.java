import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination {

    private HashMap<String, Integer> map;
    private List<Team> teams;
    private List<List<String>> certificates;
    private int[] eliminated;
    private int teamCount;

    public BaseballElimination(String filename) {
        parseFile(filename);
        certificates = new ArrayList<>();
        for (int i = 0; i < teamCount; i++)
            certificates.add(null);
        eliminated = new int[teamCount];
        for (int i = 0; i < teamCount; i++)
            eliminated[i] = -1;
    }                    // create a baseball division from given filename in format specified below

    private void parseFile(String filename)
    {
        In in = new In(filename);
        teamCount = in.readInt();
        teams = new ArrayList<Team>();
        map = new HashMap<>();

        int[] matches = new int[teamCount];
        String name;
        int w, l, r;
        for (int i = 0; i < teamCount; i++)
        {
            name = in.readString();
            w = in.readInt();
            l = in.readInt();
            r = in.readInt();
            for (int j = 0 ; j < teamCount; j++)
            {
                matches[j] = in.readInt();
            }
            teams.add(new Team(name, w, l ,r, matches));
            map.put(name, i);
        }
    }

    public int numberOfTeams() {
        return teamCount;
    }                        // number of teams

    public Iterable<String> teams() {
        return map.keySet();
    }                                // all teams

    public int wins(String team) {
        if (map.containsKey(team))
            return teams.get(map.get(team)).getW();
        else
            throw new IllegalArgumentException();
    }                           // number of wins for given team

    public int losses(String team) {
        if (map.containsKey(team))
            return teams.get(map.get(team)).getL();
        else
            throw new IllegalArgumentException();
    }                         // number of losses for given team

    public int remaining(String team) {
        if (map.containsKey(team))
            return teams.get(map.get(team)).getR();
        else
            throw new IllegalArgumentException();
    }                      // number of remaining games for given team

    public int against(String team1, String team2) {
        if (map.containsKey(team1) && map.containsKey(team2))
            return teams.get(map.get(team1)).getMatches()[map.get(team2)];
        else
            throw new IllegalArgumentException();
    }         // number of remaining games between team1 and team2

    private boolean checkTrivialElimination(int teamId)
    {
        int maxPossible = teams.get(teamId).getW() + teams.get(teamId).getR();
        for (int i = 0 ; i < teamCount; i++)
        {
            if (maxPossible < teams.get(i).getW())
            {
                ArrayList<String> certificate = new ArrayList<>();
                certificate.add(teams.get(i).getName());
                certificates.set(teamId, certificate);
                return true;
            }
        }
        return false;
    }

    public boolean isEliminated(String team) {
        if (!map.containsKey(team))
            throw new IllegalArgumentException();

        int teamId = map.get(team);
        if (eliminated[teamId] == 1) return true;
        else if (eliminated[teamId] == 0) return false;

        if (checkTrivialElimination(teamId))
            return true;

        int other = teamCount - 1;
        int otherComb = (other * (other - 1))/2;
        int v = otherComb + other + 2;
        FlowNetwork fn = new FlowNetwork(v);

        int maxFlowPossible = 0;

        int curr = 1;
        for (int i = 0; i < teamCount; i++)
        {
            if (i == teamId) continue;
            for (int j = i + 1; j < teamCount; j++)
            {
                if (j == teamId) continue;
                int matchesBetween = teams.get(i).getMatches()[j];
                maxFlowPossible += matchesBetween;
                fn.addEdge(new FlowEdge(0, curr, matchesBetween));
                fn.addEdge(new FlowEdge(curr, i > teamId ? otherComb + i : otherComb + 1 + i,  Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(curr, j > teamId ? otherComb + j : otherComb + 1 + j, Double.POSITIVE_INFINITY));
                curr++;
            }
        }

        for (int i = 0; i < teamCount; i++)
        {
            if (i == teamId) continue;
            fn.addEdge(new FlowEdge(i > teamId ? otherComb + i : otherComb + 1 + i, v - 1,
                                    teams.get(teamId).getW() + teams.get(teamId).getR() - teams.get(i).getW()));
        }

        FordFulkerson ff = new FordFulkerson(fn, 0, v - 1);

        if (certificates.get(teamId) == null)
        {
            ArrayList<String> certificate = new ArrayList<>();
            for (int i = 0; i < teamCount; i++)
                if (i != teamId && ff.inCut(i > teamId ? otherComb + i : otherComb + 1 + i))
                    certificate.add(teams.get(i).getName());
            certificates.set(teamId, certificate);
        }

        eliminated[teamId] = ((int) ff.value()) != maxFlowPossible ? 1 : 0;
        return ((int) ff.value()) != maxFlowPossible;
    }                   // is given team eliminated?

    public Iterable<String> certificateOfElimination(String team) {
        if (!map.containsKey(team))
            throw new IllegalArgumentException();

        int teamId = map.get(team);
        if (eliminated[teamId] == -1) isEliminated(team);

        if (eliminated[teamId] == 0) return null;
        else return certificates.get(teamId);
    }       // subset R of teams that eliminates given team; null if not eliminated

    public static void main(String[] args)
    {
        BaseballElimination division = new BaseballElimination("teams54.txt");
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
