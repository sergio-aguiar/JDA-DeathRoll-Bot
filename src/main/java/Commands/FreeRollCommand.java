package Commands;

import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: FreeRoll.
 * <ul>
 *     <li> Usable by: Any user.
 *     <li> Alias: Froll, fr.
 *     <li> Arguments: A numeric value greater than 1 (obligatory).
 *     <li> Purpose: Roll a random number up to the value of the given argument.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.2
 * @since 1.0.0
 */
public class FreeRollCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the FreeRoll command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to a valid maximum roll value not having been provided;
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "froll")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "fr"))
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (messageText.length != 2) {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'froll' command takes exactly 1 argument." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "froll [maximum roll value]");
                }
                else
                {
                    int parsed = 0;

                    try
                    {
                        parsed = Integer.parseInt(messageText[1]);
                    }
                    catch (NumberFormatException e)
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("Incorrect argument value!")
                                .setDescription("The 'froll' command takes exactly 1 argument." +
                                        "\nUsage: " + DeathRollMain.getPrefix() + "froll [maximum roll value]");
                    }

                    if (parsed < 2)
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("Incorrect roll value!")
                                .setDescription("Argument [maximum roll value] must have a value of 2 or greater!");
                    }
                    else
                    {
                        int rand = DeathRollMain.getRandom().nextInt(parsed) + 1;

                        if (rand > 1)
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                    .setTitle("Value rolled:")
                                    .setDescription("User " + event.getAuthor().getAsMention()
                                            + " just rolled a " + rand + "!");
                        }
                        else
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_NEUTRAL)
                                    .setTitle("DEATH ROLL:")
                                    .setDescription("User " + event.getAuthor().getAsMention()
                                            + " just rolled a " + rand + "!");
                        }
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
