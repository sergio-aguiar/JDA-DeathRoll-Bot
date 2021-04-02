package Commands;

import Database.SQLiteConnection;
import Database.UserStats;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DeathRoll Command: Profile.
 * <ul>
 *     <li> Usable by: Registered users.
 *     <li> Alias: Profile, p.
 *     <li> Arguments: Either none or a player mention.
 *     <li> Purpose: Display a user's match and skull information.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.2
 * @since 1.3.2
 */
public class ProfileCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Profile command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the mentioned player being a bot;
     *     <li> error, due to the mentioned player not being registered;
     *     <li> success, where an embed with the player's match and skull information is displayed.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "profile")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "p"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 1 && messageText.length != 2)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'profile' command takes either 0 or 1 arguments." +
                                    "\nUsage:" +
                                    "\n(1) " + DeathRollMain.getPrefix() + "profile" +
                                    "\n(2) " + DeathRollMain.getPrefix() + "profile [@player]");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (messageText.length == 1)
                        {
                            int skulls = SQLiteConnection.getUserSkulls(event.getAuthor().getId());
                            UserStats wins = SQLiteConnection.getUserWins(event.getAuthor().getId());
                            UserStats losses = SQLiteConnection.getUserLosses(event.getAuthor().getId());

                            double winRate = (wins.getMatches() + losses.getMatches() == 0) ? 0 : (double)
                                    wins.getMatches() / ((double) wins.getMatches() + (double) losses.getMatches());

                            BigDecimal db = new BigDecimal(winRate).setScale(2, RoundingMode.HALF_UP);

                            embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                    .setTitle(event.getAuthor().getName() + "'s profile:")
                                    .setThumbnail(event.getAuthor().getAvatarUrl())
                                    .setDescription("**Skulls:** " + skulls
                                            + "\n\n**Wins:** " + wins.getMatches()
                                            + "\n**Losses:** " + losses.getMatches()
                                            + "\n**Win Rate:** " + (db.doubleValue() * 100) + "%"
                                            + "\n\n**Skulls Won:** " + wins.getSkullAmount()
                                            + "\n**Skulls Lost:** " + losses.getSkullAmount()
                                            + "\n**Net Profit:** " + (wins.getSkullAmount() - losses.getSkullAmount()));
                        }
                        else
                        {
                            if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Incorrect number of arguments!")
                                        .setDescription("The 'profile' command takes either 0 or 1 arguments." +
                                                "\nUsage:" +
                                                "\n(1) " + DeathRollMain.getPrefix() + "profile" +
                                                "\n(2) " + DeathRollMain.getPrefix() + "profile [@player]");
                            }
                            else
                            {
                                if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Invalid Profile:")
                                            .setDescription("You can not check bot profiles!");
                                }
                                else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId()))
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Invalid Profile:")
                                            .setDescription("You can not check unregistered user profiles!");
                                }
                                else
                                {
                                    int skulls = SQLiteConnection.getUserSkulls(event.getMessage().getMentionedMembers()
                                            .get(0).getUser().getId());
                                    UserStats wins = SQLiteConnection.getUserWins(event.getMessage()
                                            .getMentionedMembers().get(0).getUser().getId());
                                    UserStats losses = SQLiteConnection.getUserLosses(event.getMessage()
                                            .getMentionedMembers().get(0).getUser().getId());

                                    double winRate = (wins.getMatches() + losses.getMatches() == 0) ? 0 : (double)
                                            wins.getMatches() / ((double) wins.getMatches() + (double)
                                            losses.getMatches());

                                    BigDecimal db = new BigDecimal(winRate).setScale(2, RoundingMode.HALF_UP);

                                    embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                            .setTitle(event.getMessage().getMentionedMembers().get(0).getUser()
                                                    .getName() + "'s profile:")
                                            .setThumbnail(event.getMessage().getMentionedMembers().get(0).getUser()
                                                    .getAvatarUrl())
                                            .setDescription("**Skulls:** " + skulls
                                                    + "\n\n**Wins:** " + wins.getMatches()
                                                    + "\n**Losses:** " + losses.getMatches()
                                                    + "\n**Win Rate:** " + (db.doubleValue() * 100) + "%"
                                                    + "\n\n**Skulls Won:** " + wins.getSkullAmount()
                                                    + "\n**Skulls Lost:** " + losses.getSkullAmount()
                                                    + "\n**Net Profit:** " + (wins.getSkullAmount() -
                                                    losses.getSkullAmount()));
                                }
                            }
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'profile' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
