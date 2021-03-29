package Commands;

import Database.SQLiteConnection;
import Database.UserStats;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * DeathRoll Command: Forfeit.
 * <ul>
 *     <li> Usable by: Registered users who are currently in a duel.
 *     <li> Alias: Forfeit, df, f.
 *     <li> Arguments: None.
 *     <li> Purpose: Force the currently ongoing duel to end in a loss for the command user.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.0
 * @since 1.0.0
 */
public class ForfeitCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Forfeit command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to the calling user not being in a duel;
     *     <li> error, due to an incorrect embed having been created (please contact the developer);
     *     <li> success, where an interactive embed with reacts is displayed.
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
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "forfeit")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "df")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "f"))
            {
                if (messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'forfeit' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "forfeit");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                    .setTitle("Duel forfeit:")
                                    .setDescription(event.getAuthor().getAsMention() + ", you are trying to forfeit " +
                                            "the duel.\nThis is equivalent to a loss.\nProceed?");
                        }
                        else
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Not currently in a duel!")
                                    .setDescription("User " + event.getAuthor().getAsMention() + " is not currently" +
                                            " in a duel.\nTo begin one, use the 'duel' command.");
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'forfeit' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
        else
        {
            if (event.getAuthor().getId().equals("731819691479269426")
                    || event.getAuthor().getId().equals("743881549392511027"))
            {
                if (event.getMessage().getEmbeds().size() != 1)
                {
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Unexpected Error!")
                            .setDescription("Target message either composed by multiple embeds or none." +
                                    "\nPlease contact a bot developer.");

                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
                else
                {
                    String title = event.getMessage().getEmbeds().get(0).getTitle();
                    if (title != null && title.equals("Duel forfeit:"))
                    {
                        event.getMessage().addReaction("✅").queue();
                        event.getMessage().addReaction("❌").queue();
                    }
                }
            }
        }
    }
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Forfeit command usage and can result in the following:
     * <ul>
     *     <li> error, due to being unable to fetch the embed's color (please contact the developer);
     *     <li> error, due to the reacting user not being registered;
     *     <li> success when accepted, where the database is updated to reflect the fact that the duel ended;
     *     <li> success when declined, where the duel proceeds as if this command had never been called.
     * </ul>
     *
     * @param event The JDA event relative to a reaction having been seen added by the application in a server channel.
     */
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event)
    {
        if (!event.getUser().isBot())
        {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            Color color = null;
            try
            {
                color = message.getEmbeds().get(0).getColor();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }

            if (message.getEmbeds().size() == 1 && color != null && color.getRGB() == new Color(0,0,0).getRGB())
            {
                MessageEmbed messageEmbed = message.getEmbeds().get(0);
                if (messageEmbed.getTitle() != null && messageEmbed.getTitle().equals("Duel forfeit:"))
                {
                    if (messageEmbed.getDescription() != null)
                    {
                        String[] description = messageEmbed.getDescription().split(" ");

                        String player = description[0].replace("<@", "")
                                .replace(">,", "").replace("!", "");

                        if (event.getUser().getId().equals(player))
                        {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            MessageEmbed embed = message.getEmbeds().get(0);

                            if (SQLiteConnection.isUserRegistered(event.getUserId()))
                            {
                                if (event.getReactionEmote().getEmoji().equals("✅"))
                                {
                                    int currentBet = SQLiteConnection.getCurrentBet(event.getUser().getId());
                                    String duelPartner = SQLiteConnection.getDuelPartner(event.getUser().getId());

                                    int userSkulls = SQLiteConnection.getUserSkulls(event.getUser().getId());
                                    int opponentSkulls = SQLiteConnection.getUserSkulls(duelPartner);

                                    UserStats userLosses = SQLiteConnection.getUserLosses(event.getUser().getId());
                                    UserStats opponentWins = SQLiteConnection.getUserWins(duelPartner);

                                    SQLiteConnection.setUserLoss(event.getUser().getId(), userLosses.getMatches() + 1,
                                            userLosses.getSkullAmount() + currentBet);

                                    SQLiteConnection.setUserWin(duelPartner, opponentWins.getMatches() + 1,
                                            opponentWins.getSkullAmount() + currentBet);

                                    SQLiteConnection.setUserSkulls(event.getUser().getId(), userSkulls - currentBet);
                                    SQLiteConnection.setUserSkulls(duelPartner, opponentSkulls + currentBet);

                                    SQLiteConnection.setNextRoll(event.getUser().getId(), 0);
                                    SQLiteConnection.setNextRoll(duelPartner, 0);

                                    SQLiteConnection.updateUserDuelEnded(event.getUser().getId());
                                    SQLiteConnection.updateUserDuelEnded(duelPartner);

                                    embedBuilder.setTitle(embed.getTitle())
                                            .setDescription(embed.getDescription())
                                            .setColor(DeathRollMain.EMBED_SUCCESS)
                                            .build();

                                    message.editMessage(embedBuilder.build()).complete();

                                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                            .setTitle("Forfeit confirmed.")
                                            .setDescription("User " + event.getUser().getAsMention()
                                                    + " just confirmed their forfeit request!");

                                }
                                else
                                {
                                    embedBuilder.setTitle(embed.getTitle())
                                            .setDescription(embed.getDescription())
                                            .setColor(DeathRollMain.EMBED_FAILURE)
                                            .build();

                                    message.editMessage(embedBuilder.build()).complete();

                                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                            .setTitle("Forfeit aborted.")
                                            .setDescription("User " + event.getUser().getAsMention()
                                                    + " just aborted their forfeit request!");

                                }
                            }
                            else
                            {
                                embedBuilder.setTitle(embed.getTitle())
                                        .setDescription(embed.getDescription())
                                        .setColor(DeathRollMain.EMBED_FAILURE)
                                        .build();

                                message.editMessage(embedBuilder.build()).complete();

                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("User not registered!")
                                        .setDescription("To forfeit a duel, you must be registered." +
                                                "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");

                            }
                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                        }
                    }
                }
            }
        }
    }
}
