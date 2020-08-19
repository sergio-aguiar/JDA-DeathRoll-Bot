package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * DeathRoll Command: Duel.
 * <ul>
 *     <li> Usable by: Registered users.
 *     <li> Alias: Duel.
 *     <li> Arguments: A user mention (obligatory), a positive numeric bet value (obligatory).
 *     <li> Purpose: Challenge another user to a ranked duel.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.1.0
 * @since 1.0.0
 */
public class DuelCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Duel command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to the calling user already having a pending duel request;
     *     <li> error, due to the calling user already being in a duel;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the challenged player being a bot;
     *     <li> error, due to the challenged player being the same as the command caller;
     *     <li> error, due to the challenged player already being ina  due;
     *     <li> error, due to a valid bet amount not having been provided;
     *     <li> error, due to the calling user's score value being inferior to the provided bet amount;
     *     <li> error, due to an incorrect embed having been created;
     *     <li> success, where an interactive embed with reacts is displayed and the database updated.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "duel"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 3)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'duel' command takes exactly 2 arguments."
                                    + "\nUsage: " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserRequestingDuel(event.getAuthor().getId()))
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Invalid Challenge!")
                                    .setDescription("User " + event.getAuthor().getAsMention() + " currently has a " +
                                            "pending challenge!");
                        }
                        else if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Invalid Challenge!")
                                    .setDescription("User " + event.getAuthor().getAsMention() + " is already in a " +
                                            "duel!");
                        }
                        else
                        {
                            if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Incorrect arguments!")
                                        .setDescription("The 'duel' command takes exactly 2 arguments, the first of " +
                                                "which MUST be a player " + "mention." + "\nUsage: "
                                                + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                            }
                            else
                            {
                                if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("You can not challenge a bot to a duel!");
                                }
                                else if (event.getMessage().getMentionedMembers().get(0).getId()
                                        .equals(event.getAuthor().getId()))
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("You can not challenge yourself to a duel!");
                                }
                                else if (SQLiteConnection.isUserInDuel(event.getMessage().getMentionedMembers().get(0)
                                        .getId()))
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("User " + event.getMessage().getMentionedMembers().get(0)
                                                    .getId() + " is already in a duel!");
                                }
                                else
                                {
                                    try
                                    {
                                        int parsed = Integer.parseInt(messageText[2]);

                                        if (parsed <= 0)
                                        {
                                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                                    .setTitle("Incorrect argument value!")
                                                    .setDescription("The 'bet amount' argument must be a positive " +
                                                            "value.");
                                        }
                                        else
                                        {
                                            int userScore = SQLiteConnection.getUserScore(event.getAuthor().getId());

                                            if (parsed > userScore)
                                            {
                                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                                        .setTitle("Not enough score to bet!")
                                                        .setDescription("User " + event.getAuthor().getAsMention()
                                                                + " currently has a score of " + userScore + ".");
                                            }
                                            else
                                            {
                                                embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                                        .setTitle("Duel request:")
                                                        .setDescription("Hello " + event.getMessage()
                                                                .getMentionedMembers().get(0).getAsMention()
                                                                + ", you have been challenged to a duel by "
                                                                + event.getAuthor().getAsMention() + "!"
                                                                + "\nBet amount: " + parsed + "\nDo you accept?");

                                                SQLiteConnection.setUserRequestingDuelState(event.getAuthor().getId(), 1);
                                            }
                                        }
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                                .setTitle("Incorrect argument value!")
                                                .setDescription("The 'duel' command takes exactly 2 arguments."
                                                        + "\nUsage: " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'duel' command, you must be registered." +
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
                    if (title != null && title.equals("Duel request:"))
                    {
                        event.getMessage().addReaction("✅").queue();
                        event.getMessage().addReaction("❌").queue();
                        event.getMessage().addReaction("🗑").queue();
                    }
                }
            }
        }
    }
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Duel command usage and can result in the following:
     * <ul>
     *     <li> error, due to being unable to fetch the embed's color (please contact the developer);
     *     <li> error, due to the reacting user not being registered;
     *     <li> error, due to a valid bet amount not being found in the embed (please contact the developer);
     *     <li> error, due to the reacting user's score value being inferior to the found bet amount;
     *     <li> error, due to the reacting user already being in a duel;
     *     <li> success when accepted, where the database is updated to reflect the fact that the duel is now underway;
     *     <li> success when declined, where the database is updated to its prior state for the challenging user.
     * </ul>
     *
     * @param event The JDA event relative to a reaction having been seen added by the application in a server channel.
     */
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event)
    {
        if (!event.getUser().isBot())
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            Color color = null;
            try
            {
                color = message.getEmbeds().get(0).getColor();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();

                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                        .setTitle("Unexpected Error!")
                        .setDescription("Unable to fetch embed color!" +
                                "\nPlease contact a bot developer.");

                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }

            if (message.getEmbeds().size() == 1 && color != null && color.getRGB() == new Color(0,0,0).getRGB())
            {
                MessageEmbed messageEmbed = message.getEmbeds().get(0);
                if (messageEmbed.getTitle() != null && messageEmbed.getTitle().equals("Duel request:"))
                {
                    if(messageEmbed.getDescription() != null)
                    {
                        String[] description = messageEmbed.getDescription().split(" ");

                        String challenged = description[1].replace("<@", "")
                                .replace(">,", "").replace("!", "");

                        String challenging = description[10].replace("<@", "")
                                .replace(">!\nBet", "").replace("!", "");

                        if (event.getUser().getId().equals(challenged))
                        {
                            MessageEmbed embed = message.getEmbeds().get(0);

                            if (SQLiteConnection.isUserRegistered(event.getUserId()))
                            {
                                int parsedBet = -1;
                                try
                                {
                                    parsedBet = Integer.parseInt(description[12].split("\n")[0].trim());
                                }
                                catch (NumberFormatException e)
                                {
                                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                            .setTitle("Unexpected Error!")
                                            .setDescription("Incorrect bet amount." +
                                                    "\nPlease contact a bot developer.");

                                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                                }

                                if (parsedBet >= 0)
                                {
                                    int userScore = SQLiteConnection.getUserScore(event.getUserId());
                                    if (userScore < parsedBet)
                                    {
                                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                                .setTitle("Not enough score to accept duel!")
                                                .setDescription("User " + event.getUser().getAsMention()
                                                        + " currently has a score of " + userScore + ".");

                                        SQLiteConnection.setUserRequestingDuelState(challenging, 0);
                                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                                        message.delete().queue();
                                    }
                                    else
                                    {
                                        if (SQLiteConnection.isUserInDuel(challenged))
                                        {
                                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                                    .setTitle("Could not accept challenge!")
                                                    .setDescription("User "
                                                            + event.getJDA().retrieveUserById(challenged).complete()
                                                            .getAsMention() + " is already in a duel!");

                                            SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                                        }
                                        else
                                        {
                                            if (event.getReactionEmote().getEmoji().equals("✅"))
                                            {
                                                embedBuilder.setTitle(embed.getTitle())
                                                        .setDescription(embed.getDescription())
                                                        .setColor(DeathRollMain.EMBED_SUCCESS)
                                                        .build();

                                                message.editMessage(embedBuilder.build()).complete();

                                                embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                                        .setTitle("Challenge accepted!")
                                                        .setDescription("User "
                                                                + event.getJDA().retrieveUserById(challenged).complete()
                                                                .getAsMention() + " has accepted "
                                                                + event.getJDA().retrieveUserById(challenging)
                                                                .complete().getAsMention() + "'s duel challenge and " +
                                                                "takes the first turn!");

                                                SQLiteConnection.updateUserDuelStarted(challenged, challenging,
                                                        parsedBet, true);
                                                SQLiteConnection.updateUserDuelStarted(challenging, challenged,
                                                        parsedBet, false);

                                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                                            }
                                            else if (event.getReactionEmote().getEmoji().equals("❌"))
                                            {
                                                embedBuilder.setTitle(embed.getTitle())
                                                        .setDescription(embed.getDescription())
                                                        .setColor(DeathRollMain.EMBED_FAILURE)
                                                        .build();

                                                message.editMessage(embedBuilder.build()).complete();

                                                embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                                        .setTitle("Challenge declined!")
                                                        .setDescription("User "
                                                                + event.getJDA().retrieveUserById(challenged).complete()
                                                                .getAsMention() + " has declined "
                                                                + event.getJDA().retrieveUserById(challenging)
                                                                .complete().getAsMention() + "'s duel challenge!");

                                                SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                                            }
                                        }
                                    }
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
                                        .setDescription("To accept a duel or deny, you must be registered." +
                                                "\nTo do so, run the "
                                                + DeathRollMain.getPrefix() + "register command.");

                                SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                            }
                        }
                        else if (event.getUser().getId().equals(challenging))
                        {
                            MessageEmbed embed = message.getEmbeds().get(0);

                            if (SQLiteConnection.isUserRegistered(event.getUserId()))
                            {
                                if (event.getReactionEmote().getEmoji().equals("🗑"))
                                {
                                    embedBuilder.setTitle(embed.getTitle())
                                            .setDescription(embed.getDescription())
                                            .setColor(DeathRollMain.EMBED_FAILURE)
                                            .build();

                                    message.editMessage(embedBuilder.build()).complete();

                                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                            .setTitle("Challenge canceled!")
                                            .setDescription("User "
                                                    + event.getJDA().retrieveUserById(challenging).complete()
                                                    .getAsMention() + " has canceled their duel request to "
                                                    + event.getJDA().retrieveUserById(challenged)
                                                    .complete().getAsMention() + "!");

                                    SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                    event.getChannel().sendMessage(embedBuilder.build()).queue();
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
                                        .setDescription("To cancel a duel request, you must be registered." +
                                                "\nTo do so, run the "
                                                + DeathRollMain.getPrefix() + "register command.");

                                SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                            }
                        }
                    }
                }
            }
        }
    }
}
