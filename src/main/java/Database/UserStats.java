package Database;

public class UserStats
{
    private final int matches;
    private final int skullAmount;
    private final boolean win;

    public UserStats(int matches, int skullAmount, boolean win)
    {
        this.matches = matches;
        this.skullAmount = skullAmount;
        this.win = win;
    }

    public int getMatches()
    {
        return this.matches;
    }

    public int getSkullAmount()
    {
        return this.skullAmount;
    }

    public boolean isWin()
    {
        return this.win;
    }
}
