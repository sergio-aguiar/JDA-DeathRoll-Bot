package Main;

import Commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.Random;

/**
 * Main: DeathRollMain
 * <ul>
 *     <li> The main application class.
 *     <li> Contains variables relative to embed colors.
 *     <li> Creates the JDA instance based off the Discord Bot's Key.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.2
 * @since 1.0.0
 */
public class DeathRollMain
{
    /**
     * The prefix used by the application to recognise commands.
     */
    private static String prefix = "+";
    /**
     * The discord bot's token.
     */
    private static String token = "";
    /**
     * The skulls value attributed to newly registering players.
     */
    private static int baseSkulls = -1;
    /**
     * Numeric value that represents a shade of Green.
     * Used for successful embed operations.
     */
    public static final int EMBED_SUCCESS = 0x19ed0e;
    /**
     * Numeric value that represents a shade of Red.
     * Used for failed embed operations.
     */
    public static final int EMBED_FAILURE = 0xe50b0e;
    /**
     * Numeric value that represents a shade of Black.
     * Used for informational and query embeds.
     */
    public static final int EMBED_NEUTRAL = 0x000000;
    /**
     *  This class's Random class instantiation.
     */
    private static final Random random = new Random();
    /**
     * The application's main function.
     * <ul>
     *     <li> Creates the JDA instance based off the Discord Bot's Key.
     *     <li> Associated the implemented commands to JDA variable.
     * </ul>
     * @param args Default main function's arguments (not used).
     */
    public static void main(String[] args)
    {
        try
        {
            JDA jda = JDABuilder.createDefault(token).build();
            jda.addEventListener(new ClaimAdminPermissionsCommand());
            jda.addEventListener(new DuelCommand());
            jda.addEventListener(new ForfeitCommand());
            jda.addEventListener(new FreeRollCommand());
            jda.addEventListener(new GiveSkullsCommand());
            jda.addEventListener(new HelpCommand());
            jda.addEventListener(new ProfileCommand());
            jda.addEventListener(new RankedRollCommand());
            jda.addEventListener(new RegisterCommand());
            jda.addEventListener(new SkullsBoardCommand());
            jda.addEventListener(new SkullsCommand());
            jda.addEventListener(new ShutDownCommand());
            jda.addEventListener(new TakeSkullsCommand());
            jda.awaitReady();
        }
        catch (LoginException e)
        {
            e.printStackTrace();
            System.exit(201);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            System.exit(202);
        }
    }
    /**
     * Get function for the prefix used by the application to recognise commands.
     * @return The prefix used by the application to recognise commands.
     */
    public static String getPrefix()
    {
        return prefix;
    }
    /**
     * Get function for the class's Random class instantiation.
     * @return The class's Random class instantiation.
     */
    public static Random getRandom()
    {
        return random;
    }
    /**
     * Set function for the discord bot's token.
     * @param token The discord bot's token.
     */
    public static void setToken(String token)
    {
        DeathRollMain.token = token;
    }
    /**
     * Set function for the skulls value attributed to newly registering players.
     * @param baseSkulls The skulls value attributed to newly registering players.
     */
    public static void setBaseSkulls(int baseSkulls)
    {
        DeathRollMain.baseSkulls = baseSkulls;
    }
    /**
     * Get function for the skulls value attributed to newly registering players.
     * @return The skulls value attributed to newly registering players.
     */
    public static int getBaseSkulls()
    {
        return baseSkulls;
    }
}
