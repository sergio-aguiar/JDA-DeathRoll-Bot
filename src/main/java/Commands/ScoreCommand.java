package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ScoreCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "score"))
            {
                if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                {
                    if(messageText.length != 1)
                    {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Incorrect number of arguments!")
                                .setDescription("The 'score' command takes no argument." +
                                        "\nUsage: " + DeathRollMain.getPrefix() + "score");
                    }
                    else
                    {
                        int userScore = SQLiteConnection.getUserScore(event.getAuthor().getId());

                        embedBuilder.setColor(0x19ed0e)
                                .setTitle("Current Score:")
                                .setDescription("User " + event.getAuthor().getAsMention() + " has a current score" +
                                        " of " + userScore + ".");
                    }
                }
                else
                {
                    embedBuilder.setColor(0xe50b0e)
                            .setTitle("User not registered!")
                            .setDescription("To use the 'roll' command, you must be registered." +
                                    "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
