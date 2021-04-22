package Common;

import Database.UserSkulls;
import Database.UserStats;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Common: CommonEmbeds.
 * <ul>
 *     <li> An internal class that contains embed generation code templates.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.4.0
 */
public class CommonEmbeds
{
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
     * Numeric value that represents a shade of Gray.
     * Used for informational and query embeds.
     */
    public static final int EMBED_NEUTRAL = 0xdadada;
    /**
     * Numeric value that represents a shade of Black.
     * Used for when a DeathRoll happens.
     */
    public static final int EMBED_DEATHROLL = 0x000000;
    /**
     * Color object that represents a shade of Gray.
     * Used for active embeds.
     */
    public static final int ACTIVE_EMBED_INT = new Color(218, 218, 218).getRGB();
    /**
     * Embed creation function that returns an error EmbedBuilder with a default message.
     * @param title The embed's title.
     * @param description The embed's description.
     * @param playerName A player's name (used in the default message).
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return An error EmbedBuilder with a default message.
     */
    public static EmbedBuilder errorEmbed(String title, String description, String playerName, String playerAvatarUrl)
    {
        return new EmbedBuilder()
                .setColor(EMBED_FAILURE)
                .setTitle(title)
                .setDescription(description)
                .setFooter("Let us hope " + playerName + " doesn't ignore this...", playerAvatarUrl);
    }
    /**
     * Embed creation function that returns an error EmbedBuilder with a default message.
     * @param title The embed's title.
     * @param description The embed's description.
     * @param playerName A player's name.
     * @param errorMessage An error message to be displayed in the footer.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return An error EmbedBuilder.
     */
    public static EmbedBuilder errorEmbed(String title, String description, String playerName, String errorMessage,
                                          String playerAvatarUrl)
    {
        return new EmbedBuilder()
                .setColor(EMBED_FAILURE)
                .setTitle(title)
                .setDescription(description)
                .setFooter("Hey, " + playerName + "! " + errorMessage, playerAvatarUrl);
    }
    /**
     * Embed creation function that returns a success EmbedBuilder.
     * @param title The embed's title.
     * @param description The embed's description.
     * @param successMessage A success message to be displayed in the footer.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return A success EmbedBuilder.
     */
    public static EmbedBuilder successEmbed(String title, String description, String successMessage,
                                            String playerAvatarUrl)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(EMBED_SUCCESS)
                .setTitle(title)
                .setDescription(description);

        if (!successMessage.equals("") && !playerAvatarUrl.equals(""))
            embedBuilder.setFooter(successMessage, playerAvatarUrl);

        return embedBuilder;
    }
    /**
     * Embed creation function that returns an active EmbedBuilder.
     * @param title The embed's title.
     * @param description The embed's description.
     * @return An active EmbedBuilder.
     */
    public static EmbedBuilder activeReactEmbed(String title, String description)
    {
        return new EmbedBuilder()
                .setColor(ACTIVE_EMBED_INT)
                .setTitle(title)
                .setDescription(description);
    }
    /**
     * Embed creation function that returns a freeRoll EmbedBuilder.
     * @param isDeathRoll Whether the roll that happened was a deathRoll or not.
     * @param title The embed's title.
     * @param description The embed's description.
     * @param playerName A player's name.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return A freeRoll EmbedBuilder.
     */
    public static EmbedBuilder freeRollEmbed(boolean isDeathRoll, String title, String description, String playerName,
                                             String playerAvatarUrl)
    {
        int embedColor;
        String footerText = "User " + playerName;

        if (isDeathRoll)
        {
            embedColor = EMBED_DEATHROLL;
            footerText += " free rolled a bit too much...";
        }
        else
        {
            embedColor = EMBED_NEUTRAL;
            footerText += " is free rolling!";
        }

        return new EmbedBuilder()
                .setColor(embedColor)
                .setTitle(title)
                .setDescription(description)
                .setFooter(footerText, playerAvatarUrl);
    }
    /**
     * Embed creation function that returns a roll EmbedBuilder.
     * @param isDeathRoll Whether the roll that happened was a deathRoll or not.
     * @param title The embed's title.
     * @param description The embed's description.
     * @param playerName A player's name.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @param opponentName Another player's name (previous one's opponent).
     * @return A roll EmbedBuilder.
     */
    public static EmbedBuilder rankedRollEmbed(boolean isDeathRoll, String title, String description, String playerName,
                                             String playerAvatarUrl, String opponentName)
    {
        int embedColor;
        String footerText = "User " + playerName;

        if (isDeathRoll)
        {
            embedColor = EMBED_DEATHROLL;
            footerText += " just made " + opponentName + " very happy...";
        }
        else
        {
            embedColor = EMBED_NEUTRAL;
            footerText += " is in a duel against " + opponentName + ".";
        }

        return new EmbedBuilder()
                .setColor(embedColor)
                .setTitle(title)
                .setDescription(description)
                .setFooter(footerText, playerAvatarUrl);
    }
    /**
     * Embed creation function that returns the main help EmbedBuilder.
     * @param botAvatarUrl The bot's Icon/Avatar URL.
     * @param playerName A player's name.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return The main help EmbedBuilder.
     */
    public static EmbedBuilder mainHelpEmbed(String botAvatarUrl, String playerName, String playerAvatarUrl)
    {
        return new EmbedBuilder()
                .setColor(EMBED_NEUTRAL)
                .setThumbnail(botAvatarUrl)
                .setTitle("List of all Commands")
                .setDescription("A list containing all available commands.\nFor more **information** on a specific " +
                        "one, use `" + DeathRollMain.getPrefix() + "help [command]`.\n*Note: You may not be able to " +
                        "use some due to being locked behind specific permissions.*\n")
                .setFooter("Currently assisting " + playerName + " with command usage!", playerAvatarUrl)
                .addField("Statistics",
                        "```• Profile\n" +
                        "• SkullsBoard\n" +
                        "• Skulls```",
                        true)
                .addField("DeathRolling",
                        "```• Duel\n" +
                        "• Forfeit\n" +
                        "• FreeRoll\n" +
                        "• RankedRoll```",
                        true)
                .addField("Miscellaneous",
                        "```• Help\n" +
                        "• Register```",
                        true)
                .addField("Admin/Restricted",
                        "```• ClaimAdminPermissions\n" +
                        "• GiveSkulls\n" +
                        "• ShutDown\n" +
                        "• TakeSkulls```",
                        true);
    }
    /**
     * Embed creation function that returns a specific help EmbedBuilder.
     * @param commandName A specific command to have info shown.
     * @param commandDescription A specific command's description.
     * @param playerName A player's name.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @return A specific help EmbedBuilder.
     */
    public static EmbedBuilder specificHelpEmbed(String commandName, String commandDescription, String playerName,
                                                 String playerAvatarUrl)
    {
        return new EmbedBuilder()
                .setColor(EMBED_NEUTRAL)
                .setTitle(commandName + " Command")
                .setDescription(commandDescription)
                .setFooter("Currently teaching " + playerName + " how to use the " + commandName + " command!",
                        playerAvatarUrl);
    }
    /**
     * Embed creation function that returns a profile EmbedBuilder.
     * @param playerName A player's name.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @param description The embed's description.
     * @param wins A UserStats object with data on wins and skulls won.
     * @param losses A UserStats object with data on losses and skulls lost.
     * @return A profile EmbedBuilder.
     */
    public static EmbedBuilder profileEmbed(String playerName, String playerAvatarUrl, String description,
                                            UserStats wins, UserStats losses)
    {
        DecimalFormat df = new DecimalFormat("#.##");

        double winRate = (wins.getMatches() + losses.getMatches() == 0) ? 0 : ((double)
                wins.getMatches() / ((double) wins.getMatches() + (double)
                losses.getMatches()) * 100);

        return new EmbedBuilder()
                .setColor(EMBED_NEUTRAL)
                .setThumbnail(playerAvatarUrl)
                .setTitle(playerName + "'s Profile")
                .setDescription(description)
                .addField("Match Statistics",
                        "```Wins    : " + wins.getMatches() + "\n" +
                        "Losses  : " + losses.getMatches() + "\n" +
                        "Win Rate: " + df.format(winRate) + "%```",
                        true)
                .addField("Skull Statistics",
                        "```Skulls Won : " + wins.getSkullAmount() + "\n" +
                        "Skulls Lost: " + losses.getSkullAmount() + "\n" +
                        "Net Profit : " + (wins.getSkullAmount() - losses.getSkullAmount() + "```"),
                        true);
    }
    /**
     * Embed creation function that returns a skullsBoard EmbedBuilder.
     * @param jda The bot's JDA object.
     * @param playerTag A player's Discord Tag.
     * @param playerAvatarUrl A player's Icon/Avatar URL.
     * @param skullList An ArrayList with info relative to the top 10 skull holders, as well as their skulls.
     * @return A skullsBoard EmbedBuilder.
     */
    public static EmbedBuilder skullsBoardEmbed(JDA jda , String playerTag, String playerAvatarUrl,
                                                ArrayList<UserSkulls> skullList)
    {
        StringBuilder players = new StringBuilder();
        StringBuilder skulls = new StringBuilder();

        String userTag;
        int userSpot = 0;
        for (int i = 0; i < skullList.size(); i++)
        {
            userTag = jda.retrieveUserById(skullList.get(i).getUserID()).complete().getAsTag();

            if (userTag.equals(playerTag)) userSpot = i + 1;

            players.append("`").append(i + 1).append("` ").append(userTag).append("\n");
            skulls.append("`").append(skullList.get(i).getSkulls()).append("`").append("\n");
        }

        String footerText;
        if (userSpot != 0) footerText = "Wow... You're #" + userSpot + "... Totally didn't expect worse or anything.";
        else footerText = "Not even on the board. Figures...";

        return new EmbedBuilder()
                .setColor(EMBED_NEUTRAL)
                .setThumbnail(jda.getSelfUser().getAvatarUrl())
                .setTitle("SkullBoard")
                .setDescription("List with the top 10 **skull** holders.")
                .setFooter(footerText, playerAvatarUrl)
                .addField("Players", players.toString(), true)
                .addField("Skulls", skulls.toString(), true);
    }
}
