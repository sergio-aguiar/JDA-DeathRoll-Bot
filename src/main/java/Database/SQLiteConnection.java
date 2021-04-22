package Database;

import Main.DeathRollMain;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Database: SQLiteConnection.
 * <ul>
 *     <li> An internal class responsible for bridging the application with the local sqlite3 database.
 *     <li> If there is no database currently available, it creates one, as well as the PlayerInfo table.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.0.0
 */
public class SQLiteConnection
{
    /**
     * The class's Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteConnection.class);
    /**
     * The class's Hikari Configuration instance.
     */
    private static final HikariConfig config = new HikariConfig();
    /**
     * The class's Hikari Data Source instance.
     */
    private static final HikariDataSource hikariDataSource;

    static
    {
        try
        {
            final File dbFile = new File("deathroll.db");

            if (!dbFile.exists())
            {
                if (dbFile.createNewFile())
                {
                    LOGGER.info("Database file created successfully.");
                }
                else
                {
                    LOGGER.info("Could not create database file.");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:deathroll.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariDataSource = new HikariDataSource(config);

        try
        {
            final Statement statement = getConnection().createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS PlayerInfo(" +
                    "discordID TEXT PRIMARY KEY," +
                    "skulls INTEGER NOT NULL," +
                    "inDuel INTEGER NOT NULL," +
                    "requestingDuel INTEGER NOT NULL," +
                    "duelPartner STRING," +
                    "currentBet INTEGER NOT NULL," +
                    "nextRoll INTEGER NOT NULL," +
                    "isRollTurn INTEGER NOT NULL," +
                    "wins INTEGER NOT NULL," +
                    "losses INTEGER NOT NULL," +
                    "skullsWon INTEGER NOT NULL," +
                    "skullsLost INTEGER NOT NULL," +
                    "isAdmin INTEGER NOT NULL" +
                    ");");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     *  Class Constructor: SQLiteConnection.
     */
    private SQLiteConnection() { }
    /**
     * Get function for the database connection.
     * @return An instance of a connection to the database.
     * @throws SQLException When there was a database associated issue.
     */
    public static Connection getConnection() throws SQLException
    {
        return hikariDataSource.getConnection();
    }
    /**
     * Get function for checking whether a user is registered to the database.
     * @param discordID The user's discord ID.
     * @return True if the user in question is present in the database (registered), and false otherwise.
     */
    public static boolean isUserRegistered(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT discordID FROM PlayerInfo WHERE " +
                    "discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();
            result = resultSet.next();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }
    /**
     * Function that adds a new user to the database.
     * @param discordID The user's discord ID.
     * @return True is the user was registered with success, and false otherwise.
     */
    public static boolean registerUser(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO PlayerInfo(discordID, skulls" +
                    ", inDuel, requestingDuel, currentBet, nextRoll, isRollTurn, wins, losses, skullsWon, skullsLost," +
                    " isAdmin) VALUES(?,?,?,?,?,?,?,?,?,?,?,?);");

            preparedStatement.setString(1, discordID);
            preparedStatement.setInt(2, DeathRollMain.getBaseSkulls());
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.setInt(8, 0);
            preparedStatement.setInt(9, 0);
            preparedStatement.setInt(10, 0);
            preparedStatement.setInt(11, 0);
            preparedStatement.setInt(12, 0);
            preparedStatement.execute();
            result = true;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Get function for a user's skulls.
     * @param discordID The user's discord ID.
     * @return The given user's skulls value.
     */
    public static int getUserSkulls(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT skulls FROM PlayerInfo WHERE " +
                    "discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("skulls");

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Get function for the top users by skulls.
     * @return An array list with the top users by skulls, up to a maximum of 10.
     */
    public static ArrayList<UserSkulls> getSkullsLeaderboard()
    {
        ArrayList<UserSkulls> result = new ArrayList<>();
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT discordID, skulls FROM PlayerInfo" +
                    " ORDER BY skulls DESC LIMIT 10;");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                result.add(new UserSkulls(
                        resultSet.getString("discordID"),
                        resultSet.getInt("skulls"))
                );
            }
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for a user's skulls.
     * @param discordID The user's discord ID.
     * @param skulls The user's new skulls value.
     */
    public static void setUserSkulls(String discordID, int skulls)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET skulls = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, skulls);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get function for checking whether a user is in a duel.
     * @param discordID The user's discord ID.
     * @return True if the given user is in a duel, and false otherwise.
     */
    public static boolean isUserInDuel(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT inDuel FROM PlayerInfo WHERE " +
                    "discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("inDuel") == 1;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for when a user begins a duel.
     * @param discordID The user's discord ID.
     * @param duelPartner The user's duel partner's discord ID.
     * @param betAmount The duel's bet amount.
     * @param isRollTurn Whether it is the user's turn to start rolling.
     */
    public static void updateUserDuelStarted(String discordID, String duelPartner, int betAmount, boolean isRollTurn)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET inDuel = ?, " +
                    "duelPartner = ?, requestingDuel = ?, currentBet = ?, nextRoll = ?, isRollTurn = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1,1);
            preparedStatement.setString(2, duelPartner);
            preparedStatement.setInt(3,0);
            preparedStatement.setInt(4,betAmount);
            preparedStatement.setInt(5,betAmount * 10);
            preparedStatement.setInt(6, (isRollTurn) ? 1 : 0);
            preparedStatement.setString(7, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Set function for when a user ends a duel.
     * @param discordID The user's discord ID.
     */
    public static void updateUserDuelEnded(String discordID)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET inDuel = ?, " +
                    "duelPartner = ?, currentBet = ?, nextRoll = ?, isRollTurn = ? WHERE discordID = ?;");

            preparedStatement.setInt(1,0);
            preparedStatement.setString(2, null);
            preparedStatement.setInt(3,0);
            preparedStatement.setInt(4,0);
            preparedStatement.setInt(5,0);
            preparedStatement.setString(6, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get function for whether a user has an ongoing duel request.
     * @param discordID The user's discord ID.
     * @return True is the given user has an ongoing duel request, and false otherwise.
     */
    public static boolean isUserRequestingDuel(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT requestingDuel FROM PlayerInfo " +
                    "WHERE discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            result = resultSet.getInt("requestingDuel") == 1;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for whether a user is in a duel.
     * @param discordID The user's discord ID.
     * @param state Whether the user has an ongoing duel request.
     */
    public static void setUserRequestingDuelState(String discordID, int state)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET requestingDuel = ?" +
                    " WHERE discordID = ?;");

            preparedStatement.setInt(1, state);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get function for a user's duel's current bet value.
     * @param discordID The user's discord ID.
     * @return The given user's current duel's bet value, or -1 if the function failed.
     */
    public static int getCurrentBet(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT currentBet FROM PlayerInfo WHERE" +
                    " discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("currentBet");

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Get function for a user's duel partner.
     * @param discordID The user's discord ID.
     * @return The given user's duel partner's discord ID, or an empty string if the function failed.
     */
    public static String getDuelPartner(String discordID)
    {
        String result = "";
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT duelPartner FROM PlayerInfo WHERE" +
                    " discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getString("duelPartner");

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Get function for a user's next ranked roll value.
     * @param discordID The user's discord ID.
     * @return The given user's next ranked roll value, or -1 if the function failed.
     */
    public static int getNextRoll(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT nextRoll FROM PlayerInfo WHERE" +
                    " discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("nextRoll");

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for a user's next ranked roll value.
     * @param discordID The user's discord ID.
     * @param nextRoll The user's next ranked roll value.
     */
    public static void setNextRoll(String discordID, int nextRoll)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET nextRoll = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, nextRoll);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get function for checking whether it's a user's turn to roll in a duel.
     * @param discordID The user's discord ID.
     * @return True if it's a user's turn to roll in a duel, and false otherwise.
     */
    public static boolean IsRollTurn(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT isRollTurn FROM PlayerInfo WHERE" +
                    " discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("isRollTurn") == 1;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for whether it's a user's turn to roll in a duel.
     * @param discordID The user's discord ID.
     * @param isRollTurn Whether it is the user's turn to roll in a duel.
     */
    public static void setRollTurn(String discordID, boolean isRollTurn)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET isRollTurn = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, (isRollTurn) ? 1 : 0);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Function that cleans current duel/challenges so that a shutdown does not cause an invalid starting state.
     */
    public static void cleanShutdown()
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET inDuel = ?, " +
                    "requestingDuel = ?, duelPartner = ?, currentBet = ?, nextRoll = ?, isRollTurn = ?;");

            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, null);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get function for retrieving a user's number of wins and skulls won.
     * @param discordID The user's discord ID.
     * @return A user's number of wins and skulls won.
     */
    public static UserStats getUserWins(String discordID)
    {
        UserStats result = null;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT wins, skullsWon FROM PlayerInfo WHERE" +
                    " discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                result = new UserStats(resultSet.getInt("wins"), resultSet.getInt("skullsWon"), true);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Get function for retrieving a user's number of losses and skulls lost.
     * @param discordID The user's discord ID.
     * @return A user's number of losses and skulls lost.
     */
    public static UserStats getUserLosses(String discordID)
    {
        UserStats result = null;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT losses, skullsLost FROM PlayerInfo " +
                    "WHERE discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                result = new UserStats(resultSet.getInt("losses"), resultSet.getInt("skullsLost"), false);
            }

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Set function for a user's number of wins and skulls won.
     * @param discordID The user's discord ID.
     * @param matches The user's number of wins.
     * @param skulls The user's number of skulls won.
     */
    public static void setUserWin(String discordID, int matches, int skulls)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET wins = ?, " +
                    "skullsWon = ? WHERE discordID = ?;");

            preparedStatement.setInt(1, matches);
            preparedStatement.setInt(2, skulls);
            preparedStatement.setString(3, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Set function for a user's number of losses and skulls lost.
     * @param discordID The user's discord ID.
     * @param matches The user's number of losses.
     * @param skulls The user's number of skulls lost.
     */
    public static void setUserLoss(String discordID, int matches, int skulls)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET losses = ?, " +
                    "skullsLost = ? WHERE discordID = ?;");

            preparedStatement.setInt(1, matches);
            preparedStatement.setInt(2, skulls);
            preparedStatement.setString(3, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Set function for a user's administrator permissions.
     * @param discordID The user's discord ID.
     * @param isAdmin Whether the user will have administrator permissions or not.
     */
    public static void setUserAdminPerms(String discordID, boolean isAdmin)
    {
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET isAdmin = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, (isAdmin) ? 1 : 0);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Function that returns whether a user has administrator permissions or not.
     * @param discordID The user's discord ID.
     * @return True if the user has administrator permissions, and false otherwise.
     */
    public static boolean isUserAdmin(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT isAdmin FROM PlayerInfo WHERE " +
                    "discordID=?;");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("isAdmin") == 1;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
