package Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteConnection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteConnection.class);
    private static final HikariConfig config = new HikariConfig();
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
                    "score INTEGER NOT NULL," +
                    "inDuel INTEGER NOT NULL," +
                    "requestingDuel INTEGER NOT NULL," +
                    "duelPartner STRING," +
                    "currentBet INTEGER NOT NULL," +
                    "nextRoll INTEGER NOT NULL" +
                    ");");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private SQLiteConnection() { }

    public static Connection getConnection() throws SQLException
    {
        return hikariDataSource.getConnection();
    }

    public static boolean isUserRegistered(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT discordID FROM PlayerInfo WHERE " +
                    "discordID=?");

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

    public static boolean registerUser(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO PlayerInfo(discordID, score," +
                    " inDuel, requestingDuel, currentBet, nextRoll) VALUES(?,?,?,?,?,?);");

            preparedStatement.setString(1, discordID);
            // preparedStatement.setInt(2,10000);
            preparedStatement.setInt(2,1000000);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
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

    public static int getUserScore(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT score FROM PlayerInfo WHERE " +
                    "discordID=?");

            preparedStatement.setString(1, discordID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) result = resultSet.getInt("score");

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean setUserScore(String discordID, int score)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET score = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, score);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();
            result = true;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isUserInDuel(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT inDuel FROM PlayerInfo WHERE " +
                    "discordID=?");

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

    public static boolean updateUserDuelStarted(String discordID, String duelPartner, int betAmount)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET inDuel = ?, " +
                    "duelPartner = ?, requestingDuel = ?, currentBet = ?, nextRoll = ? WHERE discordID = ?;");

            preparedStatement.setInt(1,1);
            preparedStatement.setString(2, duelPartner);
            preparedStatement.setInt(3,0);
            preparedStatement.setInt(4,betAmount);
            preparedStatement.setInt(5,betAmount * 10);
            preparedStatement.setString(6, discordID);
            preparedStatement.executeUpdate();
            result = true;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean updateUserDuelEnded(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET inDuel = ?, " +
                    "duelPartner = ?, currentBet = ?, nextRoll = ? WHERE discordID = ?;");

            preparedStatement.setInt(1,0);
            preparedStatement.setString(2, null);
            preparedStatement.setInt(3,0);
            preparedStatement.setInt(4,0);
            preparedStatement.setString(5, discordID);
            preparedStatement.executeUpdate();
            result = true;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isUserRequestingDuel(String discordID)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT requestingDuel FROM PlayerInfo " +
                    "WHERE discordID=?");

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

    public static boolean setUserRequestingDuelState(String discordID, int state)
    {
        boolean result = false;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET requestingDuel = ?" +
                    " WHERE discordID = ?;");

            preparedStatement.setInt(1, state);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();
            result = true;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCurrentBet(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT currentBet FROM PlayerInfo WHERE" +
                    " discordID=?");

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

    public static String getDuelPartner(String discordID)
    {
        String result = "";
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT duelPartner FROM PlayerInfo WHERE" +
                    " discordID=?");

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

    public static int getNextRoll(String discordID)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT nextRoll FROM PlayerInfo WHERE" +
                    " discordID=?");

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

    public static int setNextRoll(String discordID, int nextRoll)
    {
        int result = -1;
        Connection conn;
        try
        {
            conn = getConnection();

            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE PlayerInfo SET nextRoll = ? " +
                    "WHERE discordID = ?;");

            preparedStatement.setInt(1, nextRoll);
            preparedStatement.setString(2, discordID);
            preparedStatement.executeUpdate();
            result = nextRoll;

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
