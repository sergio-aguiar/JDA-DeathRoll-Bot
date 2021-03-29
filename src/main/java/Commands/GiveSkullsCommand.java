package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * DeathRoll Command: GiveSkulls.
 * <ul>
 *     <li> Usable by: Users with administrator permissions.
 *     <li> Alias: GiveSkulls, gs.
 *     <li> Arguments: A user mention (obligatory), a positive numeric skull amount (obligatory).
 *     <li> Purpose: Give a player a set amount of skulls.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.3.0
 * @since 1.3.0
 */
public class GiveSkullsCommand extends ListenerAdapter
{
    /**
     * Inherited from ListenerAdapter.
     *
     * This implementation handles the GiveSkulls command usage and can result in the following:
     * <ul>
     *     <li> error, due to incorrect number of arguments;
     *     <li> error, due to the calling user not being registered;
     *     <li> error, due to a valid player mention not having been provided;
     *     <li> error, due to the mentioned user being a bot;
     *     <li> error, due to the mentioned user not being registered;
     *     <li> error, due to a valid skull amount not having been provided;
     *     <li> error, due to the calling user not having administrator permissions;
     *     <li> success, where the mentioned user is given the specified skull amount.
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

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "giveskulls")
                    || messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "gs"))
            {
                if (messageText.length != 3)
                {
                    embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'giveSkulls' command takes exactly 2 arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "giveSkulls [@player] [skull amount]");
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
                                        .setDescription("The 'giveSkulls' command takes exactly 2 arguments, the " +
                                                "first of which MUST be a player mention.\nUsage: " +
                                                DeathRollMain.getPrefix() + "giveSkulls [@player] [skull amount]");
                            }
                            else if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Invalid skull giving.")
                                        .setDescription("You can not give skulls to a bot!");
                            }
                            else if (!SQLiteConnection.isUserRegistered(event.getMessage().getMentionedMembers().get(0)
                                    .getUser().getId())) {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Invalid skull giving.")
                                        .setDescription("You can not give skulls to a non-registered player!");
                            }
                            else if (SQLiteConnection.isUserAdmin(event.getAuthor().getId()))
                            {
                                int userSkulls = SQLiteConnection.getUserSkulls(event.getMessage().getMentionedMembers()
                                        .get(0).getUser().getId());

                                SQLiteConnection.setUserSkulls(event.getMessage().getMentionedMembers().get(0).getUser()
                                        .getId(), userSkulls + parsed);

                                embedBuilder.setColor(DeathRollMain.EMBED_SUCCESS)
                                        .setTitle("Skulls given.")
                                        .setDescription("Successfully given " + event.getMessage().getMentionedMembers()
                                                .get(0).getUser().getAsMention() + " " + parsed + " skulls.");
                            }
                            else
                            {
                                embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                        .setTitle("Insufficient permissions.")
                                        .setDescription("The 'giveSkulls' command requires administrator permissions.");
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                    .setTitle("Incorrect number of arguments!")
                                    .setDescription("The 'giveSkulls' command takes exactly 2 arguments." +
                                            "\nUsage: " + DeathRollMain.getPrefix()
                                            + "giveSkulls [@player] [skull amount]");
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(DeathRollMain.EMBED_FAILURE)
                                .setTitle("User not registered!")
                                .setDescription("To use the 'giveSkulls' command, you must be registered." +
                                        "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
