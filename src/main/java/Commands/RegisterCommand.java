package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class RegisterCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "register"))
            {
                if (messageText.length != 1)
                {
                    embedBuilder.setColor(0xe50b0e)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'register' command takes arguments." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "register");
                }
                else
                {
                    if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Registration error!")
                                .setDescription("User " + event.getAuthor().getAsMention() + " is already registered!");
                    }
                    else if (SQLiteConnection.registerUser(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(0x19ed0e)
                                .setTitle("User registered.")
                                .setDescription("User " + event.getAuthor().getAsMention()
                                        + " was successfully registered!");
                    }
                    else
                    {
                        embedBuilder.setColor(0xe50b0e)
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
