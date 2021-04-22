package Database;

/**
 * Database: UserStats.
 * <ul>
 *     <li> An internal class responsible for organizing user win/loss amount, as well as skull amount won/lost.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.3.0
 */
public class UserStats
{
    /**
     * The user's amount of wins/losses.
     */
    private final int matches;
    /**
     * The user's amount of skulls won/lost.
     */
    private final int skullAmount;
    /**
     * Whether the remaining attributes are regarding wins (true) or losses (false).
     */
    private final boolean win;
    /**
     * Class Constructor: UserStats.
     * @param matches The user's amount of wins/losses.
     * @param skullAmount The user's amount of skulls won/lost.
     * @param win Whether the remaining attributes are regarding wins (true) or losses (false).
     */
    public UserStats(int matches, int skullAmount, boolean win)
    {
        this.matches = matches;
        this.skullAmount = skullAmount;
        this.win = win;
    }
    /**
     * Get function for the amount of wins/losses.
     * @return The user's amount of wins/losses.
     */
    public int getMatches()
    {
        return this.matches;
    }
    /**
     * Get function for the amount of skulls won/lost.
     * @return The user's amount of skulls won/lost.
     */
    public int getSkullAmount()
    {
        return this.skullAmount;
    }
    /**
     * Get function for whether the remaining attributes are regarding wins or losses.
     * @return True if the remaining attributes are regarding wins, and false otherwise.
     */
    public boolean isWin()
    {
        return this.win;
    }
}
