package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Random;

public class RankedRollCommand extends ListenerAdapter
{
    private static final Random random = new Random();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "roll"))
            {
                if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                {
                    if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                    {
                        if(messageText.length != 1)
                        {
                            embedBuilder.setColor(0xe50b0e)
                                    .setTitle("Incorrect number of arguments!")
                                    .setDescription("The 'roll' command takes no arguments." +
                                            "\nUsage: " + DeathRollMain.getPrefix() + "roll");
                        }
                        else
                        {
                            int nextRoll = SQLiteConnection.getNextRoll(event.getAuthor().getId());

                            if (nextRoll < 2)
                            {
                                embedBuilder.setColor(0xe50b0e)
                                        .setTitle("Unexpected error!")
                                        .setDescription("Invalid next roll value." +
                                                "\nPlease contact a bot developer.");
                            }
                            else
                            {
                                int rand = random.nextInt(nextRoll) + 1;

                                if (rand > 1)
                                {
                                    embedBuilder.setColor(0x19ed0e)
                                            .setTitle("Value rolled:")
                                            .setDescription("User " + event.getAuthor().getAsMention()
                                                    + " just rolled a " + rand + "!");

                                    String duelPartner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

                                    SQLiteConnection.setNextRoll(event.getAuthor().getId(), rand);
                                    SQLiteConnection.setNextRoll(duelPartner, rand);
                                }
                                else
                                {
                                    embedBuilder.setColor(0x000000)
                                            .setTitle("DEATH ROLL:")
                                            .setDescription("User " + event.getAuthor().getAsMention()
                                                    + " just rolled a " + rand + "!");

                                    int currentBet = SQLiteConnection.getCurrentBet(event.getAuthor().getId());
                                    String duelPartner = SQLiteConnection.getDuelPartner(event.getAuthor().getId());

                                    int userScore = SQLiteConnection.getUserScore(event.getAuthor().getId());
                                    int opponentScore = SQLiteConnection.getUserScore(duelPartner);

                                    SQLiteConnection.setUserScore(event.getAuthor().getId(), userScore
                                            - currentBet);
                                    SQLiteConnection.setUserScore(duelPartner, opponentScore + currentBet);

                                    SQLiteConnection.setNextRoll(event.getAuthor().getId(), 0);
                                    SQLiteConnection.setNextRoll(duelPartner, 0);

                                    SQLiteConnection.updateUserDuelEnded(event.getAuthor().getId());
                                    SQLiteConnection.updateUserDuelEnded(duelPartner);
                                }
                            }
                        }
                    }
                    else
                    {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Not currently in a duel!")
                                .setDescription("User " + event.getAuthor().getAsMention() + " is not currently" +
                                        " in a duel.\nTo begin one, use the 'duel' command.");
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
