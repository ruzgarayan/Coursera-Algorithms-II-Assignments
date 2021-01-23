/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Team {

    private String name;
    private int w;
    private int l;
    private int r;
    private int[] matches;

    public Team(String name, int w, int l, int r, int[] matches) {
        this.name = name;
        this.w = w;
        this.l = l;
        this.r = r;

        this.matches = new int[matches.length];
        for (int i = 0; i < matches.length; i++)
            this.matches[i] = matches[i];
    }

    public String getName() {
        return name;
    }

    public int getW() {
        return w;
    }

    public int getL() {
        return l;
    }

    public int getR() {
        return r;
    }

    public int[] getMatches() {
        return matches;
    }
}
