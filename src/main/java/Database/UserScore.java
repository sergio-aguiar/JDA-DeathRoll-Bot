package Database;

/**
 * Database: UserScore.
 * <ul>
 *     <li> An internal class responsible for organizing user data when returning for leaderboard command usage.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.1.1
 * @since 1.0.0
 */
public class UserScore
{
    /**
     * The user's discord ID.
     */
    private final String userID;
    /**
     * The user's score value.
     */
    private final int score;
    /**
     * Class Constructor: UserScore.
     * @param userID The user's discord ID.
     * @param score The user's score value.
     */
    public UserScore(String userID, int score)
    {
        this.userID = userID;
        this.score = score;
    }
    /**
     * Get function for the user's discord ID.
     * @return The user's discord ID.
     */
    public String getUserID() {
        return userID;
    }
    /**
     * Get function for the user's score value.
     * @return The user's score value.
     */
    public int getScore() {
        return score;
    }
}
