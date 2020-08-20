package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: Register.
 * <ul>
 *     <li> Usable by: Non-registered users.
 *     <li> Alias: Register, reg, r.
 *     <li> Arguments: None.
 *     <li> Purpose: Registers the user to the database and grants permission to the usage of various commands.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.1.2
 * @since 1.0.0
 */
public class RegisterCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Register command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user already being registered;
     *     <li> error, due to an unexpected database issue (please contact the developer);
     *     <li> success, where the database is updated.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "register")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "reg")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "r"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 1)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'register' command takes no arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "register");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("Registration error!")
                                .setDescription("User " + event.getAuthor().getAsMention() + " is already registered!");
                    }
                    else if (SQLiteConnection.registerUser(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                .setTitle("User registered.")
                                .setDescription("User " + event.getAuthor().getAsMention()
                                        + " was successfully registered!");
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("Unexpected error!")
                                .setDescription("User could not be registered." +
                                        "\nPlease contact a bot developer.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
