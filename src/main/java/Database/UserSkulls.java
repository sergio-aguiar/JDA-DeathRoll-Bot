package Database;

/**
 * Database: UserSkulls.
 * <ul>
 *     <li> An internal class responsible for organizing user data when returning for leaderboard command usage.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.2
 * @since 1.0.0
 */
public class UserSkulls
{
    /**
     * The user's discord ID.
     */
    private final String userID;
    /**
     * The user's skulls value.
     */
    private final int skulls;
    /**
     * Class Constructor: UserSkulls.
     * @param userID The user's discord ID.
     * @param skulls The user's skulls value.
     */
    public UserSkulls(String userID, int skulls)
    {
        this.userID = userID;
        this.skulls = skulls;
    }
    /**
     * Get function for the user's discord ID.
     * @return The user's discord ID.
     */
    public String getUserID() {
        return this.userID;
    }
    /**
     * Get function for the user's skulls value.
     * @return The user's skulls value.
     */
    public int getSkulls() {
        return this.skulls;
    }
}
