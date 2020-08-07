package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Random;

public class FreeRollCommand extends ListenerAdapter
{
    private static final Random random = new Random();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "froll"))
            {
                if (messageText.length != 2) {
                    embedBuilder.setColor(0xe50b0e)
                            .setTitle("Incorrect number of arguments!")
                            .setDescription("The 'froll' command takes exactly 1 argument." +
                                    "\nUsage: " + DeathRollMain.getPrefix() + "froll [maximum roll value]");
                } else {
                    int parsed = 0;

                    try {
                        parsed = Integer.parseInt(messageText[1]);
                    } catch (NumberFormatException e) {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Incorrect argument value!")
                                .setDescription("The 'froll' command takes exactly 1 argument." +
                                        "\nUsage: " + DeathRollMain.getPrefix() + "froll [maximum roll value]");
                    }

                    if (parsed < 2) {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Incorrect roll value!")
                                .setDescription("Argument [maximum roll value] must have a value of 2 or greater!");
                    } else {
                        int rand = random.nextInt(parsed) + 1;

                        if (rand > 1) {
                            embedBuilder.setColor(0x19ed0e)
                                    .setTitle("Value rolled:")
                                    .setDescription("User " + event.getAuthor().getAsMention()
                                            + " just rolled a " + rand + "!");
                        } else {
                            embedBuilder.setColor(0x000000)
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
