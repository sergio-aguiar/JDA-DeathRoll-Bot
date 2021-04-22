package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Database.UserStats;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: RankedRoll.
 * <ul>
 *     <li> Usable by: Registered users who are currently in a duel, given it is their turn to do so.
 *     <li> Alias: Roll, rr.
 *     <li> Arguments: None.
 *     <li> Purpose: Rolls a random number up to a previously rolled value (or to 10x the bid value if the first roll).
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.0.0
 */
public class RankedRollCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the RankedRoll command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to the calling user not being in a duel;
     *     <li> error, due to a valid roll value not being found (please contact the developer);
     *     <li> error, due to it not being the calling user's turn to roll;
     *     <li> success, where the resulting value is displayed and the database is updated accordingly.
     * </ul>
     *
     * @param event The JDA event relative to a message having been read by the application in a server channel.
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "roll")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "rr"))
            {
                EmbedBuilder embedBuilder;

                if(messageText.length != 1)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **roll** command takes **no** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "roll```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                        {
                            int nextRoll = SQLiteConnection.getNextRoll(event.getAuthor().getId());

                            if (nextRoll < 2)
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Unexpected Error",
                                        "Invalid next roll value.\nPlease contact a bot developer.",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                            else
                            {
                                if (SQLiteConnection.IsRollTurn(event.getAuthor().getId()))
                                {
                                    int rand = DeathRollMain.getRandom().nextInt(nextRoll) + 1;
                                    String duelPartner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

                                    if (rand > 1)
                                    {
                                        embedBuilder = CommonEmbeds.rankedRollEmbed(false, "Value Rolled", "User " +
                                                event.getAuthor().getAsMention() + " just rolled a **" + rand + "**!",
                                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl(),
                                                event.getJDA().retrieveUserById(duelPartner).complete().getName());

                                        SQLiteConnection.setNextRoll(event.getAuthor().getId(), rand);
                                        SQLiteConnection.setNextRoll(duelPartner, rand);
                                        SQLiteConnection.setRollTurn(event.getAuthor().getId(), false);
                                        SQLiteConnection.setRollTurn(duelPartner, true);
                                    }
                                    else
                                    {
                                        embedBuilder = CommonEmbeds.rankedRollEmbed(true, "DEATHROLL", "User " +
                                                event.getAuthor().getAsMention() + " just rolled a **" + rand + "**!",
                                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl(),
                                                event.getJDA().retrieveUserById(duelPartner).complete().getName());

                                        int currentBet = SQLiteConnection.getCurrentBet(event.getAuthor().getId());

                                        int userSkulls = SQLiteConnection.getUserSkulls(event.getAuthor().getId());
                                        int opponentSkulls = SQLiteConnection.getUserSkulls(duelPartner);

                                        UserStats userLosses = SQLiteConnection.getUserLosses(event.getAuthor()
                                                .getId());
                                        UserStats opponentWins = SQLiteConnection.getUserWins(duelPartner);

                                        SQLiteConnection.setUserLoss(event.getAuthor().getId(), userLosses.getMatches()
                                                + 1, userLosses.getSkullAmount() + currentBet);

                                        SQLiteConnection.setUserWin(duelPartner, opponentWins.getMatches() + 1,
                                                opponentWins.getSkullAmount() + currentBet);

                                        SQLiteConnection.setUserSkulls(event.getAuthor().getId(), userSkulls
                                                - currentBet);
                                        SQLiteConnection.setUserSkulls(duelPartner, opponentSkulls + currentBet);

                                        SQLiteConnection.setNextRoll(event.getAuthor().getId(), 0);
                                        SQLiteConnection.setNextRoll(duelPartner, 0);

                                        SQLiteConnection.updateUserDuelEnded(event.getAuthor().getId());
                                        SQLiteConnection.updateUserDuelEnded(duelPartner);
                                    }
                                }
                                else
                                {
                                    String partner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

                                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Turn",
                                            event.getAuthor().getAsMention() + ", it is currently " +
                                                    event.getJDA().retrieveUserById(partner).complete().getAsMention() +
                                                    "'s turn to roll!",
                                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                }
                            }
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Non-existing Duel",
                                    "User " + event.getAuthor().getAsMention() + " is not currently in a duel.\nTo " +
                                            "begin one, use the **duel** command.",
                                    event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **roll** command, you must be registered." +
                                        "\nTo do so, run the `" + DeathRollMain.getPrefix() + "register` command.",
                                event.getAuthor().getName(), "Come register, we have cookies!",
                                event.getAuthor().getAvatarUrl());
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
