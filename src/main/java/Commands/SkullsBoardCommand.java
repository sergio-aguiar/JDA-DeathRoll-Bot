package Commands;

import Database.SQLiteConnection;
import Database.UserSkulls;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * DeathRoll Command: SkullBoard.
 * <ul>
 *     <li> Usable by: Any user.
 *     <li> Alias: SkullBoard, leaderboard, skb, ldb.
 *     <li> Arguments: None.
 *     <li> Purpose: Displays the top 10 users by (descending) skulls value.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.2.0
 * @since 1.0.0
 */
public class SkullsBoardCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the SkullsBoard command usage and can result in the following:
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "skullsboard")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "leaderboard")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "skb")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "ldb"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'skullsboard' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "skullsboard");
                }
                else
                {
                    List<UserSkulls> userSkullsList = SQLiteConnection.getSkullsLeaderboard();
                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS).setTitle("Top 10 SkullsBoard");

                    StringBuilder skullsboard = new StringBuilder();
                    for (UserSkulls skulls : userSkullsList)
                    {
                        skullsboard.append(event.getJDA().retrieveUserById(skulls.getUserID()).complete().getAsMention())
                                .append(" - ").append(skulls.getSkulls()).append("\n");
                    }
                    embedBuilder.setDescription(skullsboard.toString());
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }
}
