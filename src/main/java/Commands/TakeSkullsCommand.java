package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: TakeSkulls.
 * <ul>
 *     <li> Usable by: Users with administrator permissions.
 *     <li> Alias: TakeSkulls, ts, tsk.
 *     <li> Arguments: A user mention (obligatory), a positive numeric skull amount (obligatory).
 *     <li> Purpose: Takes a set amount of skulls from a player.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.3.1
 */
public class TakeSkullsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the TakeSkulls command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the mentioned user being a bot;
     *     <li> error, due to the mentioned user not being registered;
     *     <li> error, due to a valid skull amount not having been provided;
     *     <li> error, due to the calling user not having administrator permissions;
     *     <li> success, where the mentioned user has the specified skull amount taken away.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "takeskulls")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "ts")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "tsk"))
            {
                if (messageText.length != 3)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **takeSkulls** command takes exactly **2** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "takeSkulls [@player] [skull amount]```",
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
                                            "The **takeSkulls** command takes exactly **2** arguments, the first of " +
                                                    "which **must** be a player mention.\n\n" +
                                                    "**Usage:**\n" +
                                                    "```• " + DeathRollMain.getPrefix() + "takeSkulls [@player] " +
                                                    "[skull amount]```",
                                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                }
                                else if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Skull Taking",
                                            "You cannot take skulls from a bot user.",
                                            event.getAuthor().getName(), "Stop trying to steal from your overlords.",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId()))
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Skull Taking",
                                            "You cannot take skulls from a non-registered user.",
                                            event.getAuthor().getName(), "Not even registered and you're " +
                                                    "already trying to bully them... Good.",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else
                                {
                                    int userSkulls = SQLiteConnection.getUserSkulls(event.getMessage()
                                            .getMentionedMembers().get(0).getUser().getId());

                                    SQLiteConnection.setUserSkulls(event.getMessage().getMentionedMembers().get(0)
                                            .getUser().getId(), Math.max(userSkulls - parsed, 0));

                                    embedBuilder = CommonEmbeds.successEmbed("Skulls Taken",
                                            "Successfully taken " + parsed + " skulls from " + event.getMessage()
                                                    .getMentionedMembers().get(0).getUser().getAsMention() + ".",
                                            "Why do you even want these anyway?...",
                                            event.getAuthor().getAvatarUrl());
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                                        "The **takeSkulls** command takes exactly **2** arguments, the second of " +
                                                "which **must** be a non-negative number.\n\n" +
                                                "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "takeSkulls [@player] " +
                                                "[skull amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Insufficient Permissions",
                                    "The **takeSkulls** command requires administrator permissions.",
                                    event.getAuthor().getName(), "I see you trying to cheat...",
                                    event.getAuthor().getAvatarUrl());
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **takeSkulls** command, you must be registered." +
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
