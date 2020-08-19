package Commands;

import Database.SQLiteConnection;
import Database.UserScore;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * DeathRoll Command: Leaderboard.
 * <ul>
 *     <li> Usable by: Any user.
 *     <li> Alias: Leaderboard.
 *     <li> Arguments: None.
 *     <li> Purpose: Displays the top 10 users by (descending) score value.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.1.0
 * @since 1.0.0
 */
public class LeaderboardCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Leaderboard command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> success, where the resulting values are displayed.
     * </ul>
     *
     * @param event The JDA event relative to a message having been read by the application in a server channel.
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "leaderboard"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'leaderboard' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "leaderboard");
                }
                else
                {
                    List<UserScore> userScoreList = SQLiteConnection.getScoreLeaderboard();
                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS).setTitle("Top 10 Leaderboard");

                    StringBuilder leaderboard = new StringBuilder();
                    for (UserScore score : userScoreList)
                    {
                        leaderboard.append(event.getJDA().retrieveUserById(score.getUserID()).complete().getAsMention())
                                .append(" - ").append(score.getScore()).append("\n");
                    }
                    embedBuilder.setDescription(leaderboard.toString());
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }
}
