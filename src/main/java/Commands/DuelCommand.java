package Commands;

import Common.CommonEmbeds;
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
 *     <li> Alias: Duel, d.
 *     <li> Arguments: A user mention (obligatory), a positive numeric bet value (obligatory).
 *     <li> Purpose: Challenges another user to a ranked duel.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
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
     *     <li> error, due to the challenged player already being in a duel;
     *     <li> error, due to a valid bet amount not having been provided;
     *     <li> error, due to the calling user's skulls value being inferior to the provided bet amount;
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "duel")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "d"))
            {
                EmbedBuilder embedBuilder;

                if (messageText.length != 3)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **duel** command takes exactly **2** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserRequestingDuel(event.getAuthor().getId()))
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Invalid Challenge",
                                    "User " + event.getAuthor().getAsMention() + " currently has a pending challenge.",
                                    event.getAuthor().getName(), "Stop being so impatient...",
                                    event.getAuthor().getAvatarUrl());
                        }
                        else if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Invalid Challenge",
                                    "User " + event.getAuthor().getAsMention() + " is already in a duel!",
                                    event.getAuthor().getName(), "You can wait for your turn to lose.",
                                    event.getAuthor().getAvatarUrl());
                        }
                        else
                        {
                            if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Invalid Arguments",
                                        "The **duel** command takes exactly **2** arguments, the first of which " +
                                                "**must** be a player mention.\n\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                            else
                            {
                                if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Challenge",
                                            "You cannot challenge a bot user to a duel.",
                                            event.getAuthor().getName(), "No bullying bot users!",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else if (event.getMessage().getMentionedMembers().get(0).getId()
                                        .equals(event.getAuthor().getId()))
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Challenge",
                                            "You cannot challenge yourself to a duel.",
                                            event.getAuthor().getName(), "Stop hitting yourself!" ,
                                            event.getAuthor().getAvatarUrl());
                                }
                                else if (SQLiteConnection.isUserInDuel(event.getMessage().getMentionedMembers().get(0)
                                        .getId()))
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Challenge",
                                            "User " + event.getMessage().getMentionedMembers().get(0).getAsMention() +
                                                    " is already in a duel.",
                                            event.getAuthor().getName(), "You can wait for your turn to lose.",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else
                                {
                                    try
                                    {
                                        int parsed = Integer.parseInt(messageText[2]);

                                        if (parsed <= 0)
                                        {
                                            embedBuilder = CommonEmbeds.errorEmbed("Invalid Argument",
                                                    "The **bet amount** argument must be a positive value.",
                                                    event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                        }
                                        else
                                        {
                                            int userSkulls = SQLiteConnection.getUserSkulls(event.getAuthor().getId());

                                            if (parsed > userSkulls)
                                            {
                                                embedBuilder = CommonEmbeds.errorEmbed("Insufficient Skulls",
                                                        "User " + event.getAuthor().getAsMention() + " currently has " +
                                                                userSkulls + " skulls.",
                                                        event.getAuthor().getName(), "Your pockets seem a bit... " +
                                                                "\"Light\", don't they?",
                                                        event.getAuthor().getAvatarUrl());
                                            }
                                            else
                                            {
                                                embedBuilder = CommonEmbeds.activeReactEmbed("Duel Request",
                                                        "Hello " + event.getMessage().getMentionedMembers().get(0)
                                                        .getAsMention() + ", you have been challenged to a duel by " +
                                                        event.getAuthor().getAsMention() + "!" + "\nBet amount: " +
                                                        parsed + "\nDo you accept?");

                                                SQLiteConnection.setUserRequestingDuelState(event.getAuthor().getId(),
                                                        1);
                                            }
                                        }
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                                                "The **duel** command takes exactly **2** arguments.\n\n" +
                                                        "**Usage:**\n" +
                                                        "```• " + DeathRollMain.getPrefix() + "duel [@player] " +
                                                        "[bet amount]```",
                                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **duel** command, you must be registered." +
                                        "\nTo do so, run the `" + DeathRollMain.getPrefix() + "register` command.",
                                event.getAuthor().getName(), "Come register, we have cookies!",
                                event.getAuthor().getAvatarUrl());
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
        else
        {
            // TODO: GENERALIZE FOR OTHER BOTS (asking for bot ID on the form)
            if (event.getAuthor().getId().equals("731819691479269426")
                || event.getAuthor().getId().equals("743881549392511027"))
            {
                if (event.getMessage().getEmbeds().size() != 1)
                {
                    EmbedBuilder embedBuilder;

                    embedBuilder = CommonEmbeds.errorEmbed("Unexpected Error",
                            "Target message either composed by multiple embeds or none.\nPlease contact a bot " +
                                    "developer.",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());

                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
                else
                {
                    String title = event.getMessage().getEmbeds().get(0).getTitle();
                    if (title != null && title.equals("Duel Request"))
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
     *     <li> error, due to the reacting user's skulls value being inferior to the found bet amount;
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

                embedBuilder = CommonEmbeds.errorEmbed("Unexpected Error",
                        "Unable to fetch embed color!\nPlease contact a bot developer.",
                        event.getUser().getName(), event.getUser().getAvatarUrl());

                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }

            if (message.getEmbeds().size() == 1 && color != null && color.getRGB() == CommonEmbeds.ACTIVE_EMBED_INT)
            {
                MessageEmbed messageEmbed = message.getEmbeds().get(0);
                if (messageEmbed.getTitle() != null && messageEmbed.getTitle().equals("Duel Request"))
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
                                    embedBuilder = CommonEmbeds.errorEmbed("Unexpected Error",
                                            "Incorrect bet amount.\nPlease contact a bot developer.",
                                            event.getUser().getName(), event.getUser().getAvatarUrl());

                                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                                }

                                if (parsedBet >= 0)
                                {
                                    int userSkulls = SQLiteConnection.getUserSkulls(event.getUserId());
                                    if (userSkulls < parsedBet)
                                    {
                                        embedBuilder = CommonEmbeds.errorEmbed("Insufficient Skulls",
                                                "User " + event.getUser().getAsMention() + " currently has " +
                                                        userSkulls + " skulls.",
                                                event.getUser().getName(), event.getUser().getAvatarUrl());

                                        SQLiteConnection.setUserRequestingDuelState(challenging, 0);
                                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                                        message.delete().queue();
                                    }
                                    else
                                    {
                                        if (SQLiteConnection.isUserInDuel(challenged))
                                        {
                                            embedBuilder = CommonEmbeds.errorEmbed("Failed Duel Request",
                                                    "User " + event.getJDA().retrieveUserById(challenged).complete()
                                                            .getAsMention() + " is already in a duel!",
                                                    event.getUser().getName(), "You can work for your turn to lose.",
                                                    event.getUser().getAvatarUrl());

                                            SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                                        }
                                        else
                                        {
                                            if (event.getReactionEmote().getEmoji().equals("✅"))
                                            {
                                                embedBuilder.setTitle(embed.getTitle())
                                                        .setDescription(embed.getDescription())
                                                        .setColor(CommonEmbeds.EMBED_SUCCESS)
                                                        .build();

                                                message.editMessage(embedBuilder.build()).complete();

                                                embedBuilder = CommonEmbeds.successEmbed("Challenge Accepted",
                                                        "User " + event.getJDA().retrieveUserById(challenged).complete()
                                                        .getAsMention() + " has accepted " + event.getJDA()
                                                        .retrieveUserById(challenging).complete().getAsMention() +
                                                        "'s duel challenge and takes the first turn!",
                                                        "Good luck, " + event.getUser().getName() + "!",
                                                        event.getUser().getAvatarUrl());

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
                                                        .setColor(CommonEmbeds.EMBED_FAILURE)
                                                        .build();

                                                message.editMessage(embedBuilder.build()).complete();

                                                embedBuilder = CommonEmbeds.successEmbed("Challenge Declined",
                                                        "User " + event.getJDA().retrieveUserById(challenged).complete()
                                                        .getAsMention() + " has declined " + event.getJDA()
                                                        .retrieveUserById(challenging).complete().getAsMention() +
                                                        "'s duel challenge!", "Hey, " + event.getJDA()
                                                        .retrieveUserById(challenging).complete().getName() +
                                                        "! How does it feel to get friend-zoned by " + event.getUser()
                                                        .getName() + "?", event.getUser().getAvatarUrl());

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
                                        .setColor(CommonEmbeds.EMBED_FAILURE)
                                        .build();

                                message.editMessage(embedBuilder.build()).complete();

                                embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                        "To accept or decline a duel, you must be registered.\nTo do so, run the `" +
                                                DeathRollMain.getPrefix() + "register` command.",
                                        event.getUser().getName(), "Come register, we have cookies!" ,
                                        event.getUser().getAvatarUrl());

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
                                            .setColor(CommonEmbeds.EMBED_FAILURE)
                                            .build();

                                    message.editMessage(embedBuilder.build()).complete();

                                    embedBuilder = CommonEmbeds.successEmbed("Challenge Canceled",
                                            "User " + event.getJDA().retrieveUserById(challenging).complete()
                                            .getAsMention() + " has canceled their duel request to " + event.getJDA()
                                            .retrieveUserById(challenged).complete().getAsMention() + "!",
                                            "So " + event.getUser().getName() + ", I see you've regretted your " +
                                            "decisions?", event.getUser().getAvatarUrl());

                                    SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                                }
                            }
                            else
                            {
                                embedBuilder.setTitle(embed.getTitle())
                                        .setDescription(embed.getDescription())
                                        .setColor(CommonEmbeds.EMBED_FAILURE)
                                        .build();

                                message.editMessage(embedBuilder.build()).complete();

                                embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                        "To cancel a duel request, you must be registered.\nTo do so, run the `" +
                                                DeathRollMain.getPrefix() + "register` command.",
                                        event.getUser().getName(), "Come register, we have cookies!" ,
                                        event.getUser().getAvatarUrl());

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
