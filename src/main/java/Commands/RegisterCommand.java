package Commands;

import Common.CommonEmbeds;
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
 * @version 1.4.0
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
                EmbedBuilder embedBuilder;

                if (messageText.length != 1)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **register** command takes **no** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "register```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Registration Failed",
                                "User " + event.getAuthor().getAsMention() + " is already registered.",
                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    }
                    else if (SQLiteConnection.registerUser(event.getAuthor().getId()))
                    {
                        embedBuilder = CommonEmbeds.successEmbed("User Registered",
                                "User " + event.getAuthor().getAsMention() + " was successfully registered!",
                                "Welcome, " + event.getAuthor().getName() + ", to the last place you'll ever enter.",
                                event.getAuthor().getAvatarUrl());
                    }
                    else
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Unexpected Error",
                                "User could not be registered.\nPlease contact a bot developer.",
                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
