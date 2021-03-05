package Commands;

import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: Help.
 * <ul>
 *     <li> Usable by: Any user.
 *     <li> Alias: Help, h.
 *     <li> Arguments: A command name or one of their aliases.
 *     <li> Purpose: Display basic information regarding the usage of command a command.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.2.0
 * @since 1.2.0
 */
public class HelpCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the Help command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to a valid command name not having been provided;
     *     <li> success, where an embed with the given command's information is displayed.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "help")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "h"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 2)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'help' command takes exactly 1 argument." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "help [command]");
                }
                else
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS);
                    switch(messageText[1].toLowerCase())
                    {
                        case "duel":
                        case "d":
                            embedBuilder.setTitle("Duel Command:");
                            embedBuilder.setDescription("Challenge another user to a ranked duel.\n\n"
                                    + "Aliases: Duel, d.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                            break;
                        case "forfeit":
                        case "df":
                        case "f":
                            embedBuilder.setTitle("Forfeit Command:");
                            embedBuilder.setDescription("Force the currently ongoing duel to end in a loss for the "
                                    + "command user.\n\n"
                                    + "Aliases: Forfeit, df, f.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "forfeit");
                            break;
                        case "froll":
                        case "fr":
                            embedBuilder.setTitle("FreeRoll Command:");
                            embedBuilder.setDescription("Roll a random number up to the value of the given argument.\n\n"
                                    + "Aliases: Froll, fr.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "froll [maximum roll value]");
                            break;
                        case "help":
                        case "h":
                            embedBuilder.setTitle("Help Command:");
                            embedBuilder.setDescription("Display basic information regarding the usage of command a "
                                    + "command.\n\n"
                                    + "Aliases: Help, h.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "help [command]");
                            break;
                        case "roll":
                        case "rr":
                            embedBuilder.setTitle("RankedRoll Command:");
                            embedBuilder.setDescription("Roll a random number up to the previously rolled value (or to "
                                    + "10x the bid value if the first roll).\n\n"
                                    + "Aliases: Roll, rr.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "roll");
                            break;
                        case "register":
                        case "reg":
                        case "r":
                            embedBuilder.setTitle("Register Command:");
                            embedBuilder.setDescription("Registers the user to the database and grants permission to "
                                    + "the usage of various commands.\n\n"
                                    + "Aliases: Register, reg, r.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "register");
                            break;
                        case "skullsboard":
                        case "leaderboard":
                        case "skb":
                        case "ldb":
                            embedBuilder.setTitle("SkullsBoard Command:");
                            embedBuilder.setDescription("Displays the top 10 users by (descending) skull value.\n\n"
                                    + "Aliases: SkullsBoard, leaderboard, skb, ldb.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "skullsboard");
                            break;
                        case "skulls":
                        case "sk":
                            embedBuilder.setTitle("Skulls Command:");
                            embedBuilder.setDescription("Returns the skull value for the user who used the command.\n\n"
                                    + "Aliases: Skulls, sk.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "skulls");
                            break;
                        case "shutdown":
                        case "sd":
                            embedBuilder.setTitle("ShutDown Command:");
                            embedBuilder.setDescription("Shut the application down.\n\n"
                                    + "Aliases: Shutdown, sd.\n"
                                    + "Usage: " + DeathRollMain.getPrefix() + "shutdown");
                            break;
                        default:
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE);
                            embedBuilder.setTitle("Invalid Command Name!");
                            embedBuilder.setDescription("Please pass a valid command name or alias as an argument.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
