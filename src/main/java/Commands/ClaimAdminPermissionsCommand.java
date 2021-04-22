package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: ClaimAdminPermissions.
 * <ul>
 *     <li> Usable by: Specific users allowed by the developer.
 *     <li> Alias: ClaimAdminPermissions, cap.
 *     <li> Arguments: None.
 *     <li> Purpose: Claims the ability to use admin commands.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.3.0
 */
public class ClaimAdminPermissionsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the ClaimAdminPermission command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the user not being registered;
     *     <li> error, due to the user having already been granted admin permissions;
     *     <li> error, due to the user not being eligible to claim admin permissions;
     *     <li> success, where the user is granted admin permissions.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "claimAdminPermissions")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "cap"))
            {
                if (messageText.length != 1)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **claimAdminPermissions** command takes **no** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "claimAdminPermissions```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (event.getAuthor().getId().equals("175890397631873024")
                                || event.getAuthor().getId().equals("129458465969143808"))
                        {
                            if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                            {
                                embedBuilder = CommonEmbeds.errorEmbed("Failed Permission Granting",
                                        "User " + event.getAuthor().getAsMention() + " already has administrator " +
                                                "permissions.",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            }
                            else
                            {
                                SQLiteConnection.setUserAdminPerms(event.getAuthor().getId(), true);

                                embedBuilder = CommonEmbeds.successEmbed("Permissions Granted",
                                        "User " + event.getAuthor().getAsMention() + " has been granted administrator" +
                                                " permissions.",
                                        "All hail " + event.getAuthor().getName() + ", user of cheat codes!",
                                        event.getAuthor().getAvatarUrl());
                            }
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Failed Permission Granting",
                                    "User " + event.getAuthor().getAsMention() + " is not eligible to claim " +
                                            "administrator permissions.",
                                    event.getAuthor().getName(), "I see you trying to cheat!",
                                    event.getAuthor().getAvatarUrl());
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **claimAdminPermissions** command, you must be registered." +
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
