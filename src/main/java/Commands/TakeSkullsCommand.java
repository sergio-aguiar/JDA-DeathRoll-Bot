package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: TakeSkulls.
 * <ul>
 *     <li> Usable by: Users with administrator permissions.
 *     <li> Alias: TakeSkulls, ts, tsk.
 *     <li> Arguments: A user mention (obligatory), a positive numeric skull amount (obligatory).
 *     <li> Purpose: Take a set amount of skulls from a player.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.1
 * @since 1.3.1
 */
public class TakeSkullsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the TakeSkulls command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the mentioned user being a bot;
     *     <li> error, due to the mentioned user not being registered;
     *     <li> error, due to a valid skull amount not having been provided;
     *     <li> error, due to the calling user not having administrator permissions;
     *     <li> success, where the mentioned user has the specified skull amount taken away.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "takeskulls")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "ts")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "tsk"))
            {
                if (messageText.length != 3)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'takeSkulls' command takes exactly 2 arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "takeSkulls [@player] [skull amount]");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        try
                        {
                            int parsed = Integer.parseInt(messageText[2]);

                            if (parsed <= 0)
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Incorrect argument value!")
                                        .setDescription("The 'skull amount' argument must be a positive " +
                                                "value.");
                            }
                            else if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Incorrect arguments!")
                                        .setDescription("The 'takeSkulls' command takes exactly 2 arguments, the " +
                                                "first of which MUST be a player mention.\nUsage: " +
                                                DeathRollMain.getPrefix() + "takeSkulls [@player] [skull amount]");
                            }
                            else if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Invalid skull taking.")
                                        .setDescription("You can not take skulls from a bot!");
                            }
                            else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers().get(0)
                                    .getUser().getId())) {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Invalid skull taking.")
                                        .setDescription("You can not take skulls from a non-registered player!");
                            }
                            else if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                            {
                                int userSkulls = SQLiteConnection.getUserSkulls(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId());

                                SQLiteConnection.setUserSkulls(event.getMessage().getMentionedMembers().get(0).getUser()
                                        .getId(), Math.max(userSkulls - parsed, 0));

                                embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                        .setTitle("Skulls taken.")
                                        .setDescription("Successfully taken " + parsed + " skulls from " +
                                                        event.getMessage().getMentionedMembers().get(0).getUser()
                                                                .getAsMention() + ".");
                            }
                            else
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Insufficient permissions.")
                                        .setDescription("The 'takeSkulls' command requires administrator permissions.");
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Incorrect number of arguments!")
                                    .setDescription("The 'takeSkulls' command takes exactly 2 arguments." +
                                            "\nUsage: " + DeathRollMain.getPrefix()
                                            + "takeSkulls [@player] [skull amount]");
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'takeSkulls' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
