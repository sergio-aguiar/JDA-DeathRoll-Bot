package Commands;

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
 *     <li> Purpose: Shut the application down.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.0
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
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "shutdown")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "sd"))
            {
                if (messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'shutdown' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "shutdown");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                        {
                            SQLiteConnection.cleanShutdown();

                            embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                    .setTitle("Successful shutdown.")
                                    .setDescription("The DeathRoll bot has been shutdown and commands can not be " +
                                            "used until restarted.");

                            System.exit(999);
                        }
                        else
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Insufficient permissions.")
                                    .setDescription("The 'shutdown' command requires administrator permissions.");
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'shutdown' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
