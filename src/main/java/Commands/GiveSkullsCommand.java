package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: GiveSkulls.
 * <ul>
 *     <li> Usable by: Users with administrator permissions.
 *     <li> Alias: GiveSkulls, gs, gsk.
 *     <li> Arguments: A user mention (obligatory), a positive numeric skull amount (obligatory).
 *     <li> Purpose: Gives a player a set amount of skulls.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.3.0
 */
public class GiveSkullsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the GiveSkulls command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the mentioned user being a bot;
     *     <li> error, due to the mentioned user not being registered;
     *     <li> error, due to a valid skull amount not having been provided;
     *     <li> error, due to the calling user not having administrator permissions;
     *     <li> success, where the mentioned user is given the specified skull amount.
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
            EmbedBuilder embedBuilder;

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "giveskulls")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "gs")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "gsk"))
            {
                if (messageText.length != 3)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **giveSkulls** command takes exactly **2** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "giveSkulls [@player] [skull amount]```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                        {
                            try
                            {
                                int parsed = Integer.parseInt(messageText[2]);

                                if (parsed <= 0)
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Argument",
                                            "The **skull amount** argument must be a positive value.",
                                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                }
                                else if (event.getMessage().getMentionedMembers().size() != 1)
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                                            "The **giveSkulls** command takes exactly **2** arguments, the first of " +
                                                    "which **must** be a player mention.\n\n" +
                                            "**Usage:**\n" +
                                                    "```• " + DeathRollMain.getPrefix() + "giveSkulls [@player] " +
                                                    "[skull amount]```",
                                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                }
                                else if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Skull Giving",
                                            "You cannot give skulls to a bot user.",
                                            event.getAuthor().getName(), "Bot users need not your pity skulls.",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId()))
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Skull Giving",
                                            "You cannot give skulls to a non-registered user.",
                                            event.getAuthor().getName(), "You should nag them some more. Maybe they'll " +
                                                    "register...",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else
                                {
                                    int userSkulls = SQLiteConnection.getUserSkulls(event.getMessage().getMentionedMembers()
                                            .get(0).getUser().getId());

                                    SQLiteConnection.setUserSkulls(event.getMessage().getMentionedMembers().get(0).getUser()
                                            .getId(), userSkulls + parsed);

                                    embedBuilder = CommonEmbeds.successEmbed("Skulls Given",
                                            "Successfully given " + event.getMessage().getMentionedMembers().get(0)
                                                    .getUser().getAsMention() + " " + parsed + " skulls.",
                                            "Emptying out the skulls you had in your closet, huh?",
                                            event.getAuthor().getAvatarUrl());
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                                        "The **giveSkulls** command takes exactly **2** arguments, the second of which " +
                                                "**must** be a non-negative number.\n\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "giveSkulls [@player] " +
                                                "[skull amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Insufficient Permissions",
                                    "The **giveSkulls** command requires administrator permissions.",
                                    event.getAuthor().getName(), "I see you trying to cheat...",
                                    event.getAuthor().getAvatarUrl());
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **giveSkulls** command, you must be registered." +
                                        "\nTo do so, run the `" + DeathRollMain.getPrefix() + "register` command.",
                                event.getAuthor().getName(), "Come register, we have cookies!" ,
                                event.getAuthor().getAvatarUrl());
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
