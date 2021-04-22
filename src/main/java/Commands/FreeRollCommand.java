package Commands;

import Common.CommonEmbeds;
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
 *     <li> Purpose: Rolls a random number up to the value of the given argument.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
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
                EmbedBuilder embedBuilder;

                if (messageText.length != 2) {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **freeRoll** command takes exactly **1** argument.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "froll [maximum roll value]```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    int parsed = 0;
                    boolean wrongParse = false;

                    try
                    {
                        parsed = Integer.parseInt(messageText[1]);
                    }
                    catch (NumberFormatException e)
                    {
                        wrongParse = true;
                    }

                    if (wrongParse)
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Invalid Argument",
                                "The **freeRoll** command takes exactly **1** argument.\n\n" +
                                "**Usage:**\n" +
                                        "```• " + DeathRollMain.getPrefix() + "froll [maximum roll value]```",
                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    }
                    else if (parsed < 2)
                    {
                        embedBuilder = CommonEmbeds.errorEmbed("Invalid Argument",
                                "The **maximum roll value** argument must have a value of **2** or **greater**!",
                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    }
                    else
                    {
                        int rand = DeathRollMain.getRandom().nextInt(parsed) + 1;

                        if (rand > 1)
                        {
                            embedBuilder = CommonEmbeds.freeRollEmbed(false, "Value Rolled", "User " +
                                    event.getAuthor().getAsMention() + " just rolled a **" + rand + "**!",
                                    event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                        }
                        else
                        {
                            embedBuilder = CommonEmbeds.freeRollEmbed(true, "DEATHROLL", "User " +
                                    event.getAuthor().getAsMention() + " just rolled a **" + rand + "**!",
                                    event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                        }
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
