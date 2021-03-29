package Commands;

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
 *     <li> Purpose: Roll a random number up to the previously rolled value (or to 10x the bid value if the first roll).
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.1
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
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if(messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'roll' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "roll");
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
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Unexpected error!")
                                        .setDescription("Invalid next roll value." +
                                                "\nPlease contact a bot developer.");
                            }
                            else
                            {
                                if (SQLiteConnection.IsRollTurn(event.getAuthor().getId()))
                                {
                                    int rand = DeathRollMain.getRandom().nextInt(nextRoll) + 1;

                                    if (rand > 1)
                                    {
                                        embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                                .setTitle("Value rolled:")
                                                .setDescription("User " + event.getAuthor().getAsMention()
                                                        + " just rolled a " + rand + "!");

                                        String duelPartner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

                                        SQLiteConnection.setNextRoll(event.getAuthor().getId(), rand);
                                        SQLiteConnection.setNextRoll(duelPartner, rand);
                                        SQLiteConnection.setRollTurn(event.getAuthor().getId(), false);
                                        SQLiteConnection.setRollTurn(duelPartner, true);
                                    }
                                    else
                                    {
                                        embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                                .setTitle("DEATH ROLL:")
                                                .setDescription("User " + event.getAuthor().getAsMention()
                                                        + " just rolled a " + rand + "!");

                                        int currentBet = SQLiteConnection.getCurrentBet(event.getAuthor().getId());
                                        String duelPartner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

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

                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Incorrect turn!")
                                            .setDescription(event.getAuthor().getAsMention() + ", it is currently " +
                                                    event.getJDA().retrieveUserById(partner).complete().getAsMention() +
                                                    "'s turn to roll!");
                                }
                            }
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
                                .setDescription("To use the 'roll' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
