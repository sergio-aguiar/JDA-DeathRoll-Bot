package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: Skulls.
 * <ul>
 *     <li> Usable by: Registered users.
 *     <li> Alias: Skulls, sk.
 *     <li> Arguments: None.
 *     <li> Purpose: Returns the skulls value for the user who used the command.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.1
 * @since 1.0.0
 */
public class SkullsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Skulls command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> success, where the resulting value is displayed.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "skulls")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "sk"))
            {
                if(messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'skulls' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "skulls");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        int userSkulls = SQLiteConnection.getUserSkulls(event.getAuthor().getId());

                        embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                .setTitle("Current Skulls:")
                                .setDescription("User " + event.getAuthor().getAsMention() + " has " + userSkulls +
                                        " skulls.");
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'skulls' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
