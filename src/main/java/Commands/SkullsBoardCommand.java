package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Database.UserSkulls;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
 * @version 1.4.0
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
                EmbedBuilder embedBuilder;

                if (messageText.length != 1)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **skullsboard** command takes **no** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "skullsboard```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    ArrayList<UserSkulls> userSkullsList = SQLiteConnection.getSkullsLeaderboard();

                    embedBuilder = CommonEmbeds.skullsBoardEmbed(event.getJDA(), event.getAuthor().getAsTag(),
                            event.getAuthor().getAvatarUrl(), userSkullsList);
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
