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
 *     <li> Usable by: Bot Administrators.
 *     <li> Alias: Shutdown.
 *     <li> Arguments: None.
 *     <li> Purpose: Shut the application down.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.0.0
 * @since 1.0.0
 */
public class ShutDownCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Shutdown command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> success, shutting down the application.
     * </ul>
     *
     * @param event The JDA event relative to a message having been read by the application in a server channel.
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot()) {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "shutdown"))
            {
                if (messageText.length != 1) {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'shutdown' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "shutdown");

                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
                else
                {
                    if (event.getAuthor().getId().equals("175890397631873024")
                            || event.getAuthor().getId().equals("129458465969143808"))
                    {
                        SQLiteConnection.cleanShutdown();

                        embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                .setTitle("Successful shutdown.")
                                .setDescription("The DeathRoll bot has been shutdown and commands can not be used " +
                                        "until restarted.");

                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                        System.exit(999);
                    }
                }
            }
        }
    }
}
