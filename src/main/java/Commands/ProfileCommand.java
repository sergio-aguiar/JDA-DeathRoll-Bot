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
 * DeathRoll Command: Profile.
 * <ul>
 *     <li> Usable by: Registered users.
 *     <li> Alias: Profile, p.
 *     <li> Arguments: Either none or a player mention.
 *     <li> Purpose: Displays a user's match and skull information.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
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
                EmbedBuilder embedBuilder;

                if (messageText.length != 1 && messageText.length != 2)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **profile** command takes either **1** or **2** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "profile\n" +
                                    "• " + DeathRollMain.getPrefix() + "profile [@player]```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
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

                            embedBuilder = CommonEmbeds.profileEmbed(event.getAuthor().getName(),
                                    event.getAuthor().getAvatarUrl(), "**Skulls:** " + skulls, wins, losses);
                        }
                        else
                        {
                            if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                                        "The **profile** command takes either **1** or **2** arguments.\n\n" +
                                                "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "profile\n" +
                                                "• " + DeathRollMain.getPrefix() + "profile [@player]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                            else
                            {
                                if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Profile",
                                            "You cannot check bot user profiles.",
                                            event.getAuthor().getName(), "Don't you know bot users hide in the shadows?",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId()))
                                {
                                    embedBuilder = CommonEmbeds.errorEmbed("Invalid Profile",
                                            "You cannot check a non-registered user's profile.",
                                            event.getAuthor().getName(), "Go find someone else to stalk...",
                                            event.getAuthor().getAvatarUrl());
                                }
                                else
                                {
                                    int skulls = SQLiteConnection.getUserSkulls(event.getMessage().getMentionedMembers()
                                            .get(0).getUser().getId());
                                    UserStats wins = SQLiteConnection.getUserWins(event.getMessage()
                                            .getMentionedMembers().get(0).getUser().getId());
                                    UserStats losses = SQLiteConnection.getUserLosses(event.getMessage()
                                            .getMentionedMembers().get(0).getUser().getId());

                                    embedBuilder = CommonEmbeds.profileEmbed(event.getMessage().getMentionedMembers()
                                                    .get(0).getUser().getName() ,
                                            event.getMessage().getMentionedMembers().get(0).getUser().getAvatarUrl(),
                                            "**Skulls:** " + skulls, wins, losses);
                                }
                            }
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **profile** command, you must be registered." +
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
