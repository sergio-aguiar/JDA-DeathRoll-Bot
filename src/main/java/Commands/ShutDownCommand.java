package Commands;

import Common.CommonEmbeds;
import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: Shutdown.
 * <ul>
 *     <li> Usable by: Users with administrator permissions.
 *     <li> Alias: Shutdown, sd.
 *     <li> Arguments: None.
 *     <li> Purpose: Shuts the application down.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
 * @since 1.0.0
 */
public class ShutDownCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the ShutDown command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to the calling user not having administrator permissions;
     *     <li> success, shutting down the application.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "shutdown")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "sd"))
            {
                if (messageText.length != 1)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **shutdown** command takes **no** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "shutdown```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                        {
                            embedBuilder = CommonEmbeds.successEmbed("Shutting Down",
                                    "This instance of the DeathRoll bot is shutting down.\nCommands will not work " +
                                            "until restarted.",
                                    "Time for a very, very, long nap...",
                                    event.getAuthor().getAvatarUrl());

                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                            SQLiteConnection.cleanShutdown();
                            System.exit(999);
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.errorEmbed("Insufficient Permissions",
                                    "The **shutdown** command requires administrator permissions.",
                                    event.getAuthor().getName(), "I see you trying to cheat...",
                                    event.getAuthor().getAvatarUrl());
                        }
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Non-Registered User",
                                "To use the **shutdown** command, you must be registered." +
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
