package Commands;

import Database.SQLiteConnection;
import Main.DeathRollMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DuelCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot())
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String[] messageText = event.getMessage().getContentRaw().split("\\s+");

            if (messageText[0].equalsIgnoreCase(DeathRollMain.getPrefix() + "duel"))
            {
                if (SQLiteConnection.isUserRegistered(event.getAuthor().getId()))
                {
                    if (SQLiteConnection.isUserRequestingDuel(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Invalid Challenge!")
                                .setDescription("User " + event.getAuthor().getAsMention() + " currently has a " +
                                        "pending challenge!");
                    }
                    else if (SQLiteConnection.isUserInDuel(event.getAuthor().getId()))
                    {
                        embedBuilder.setColor(0xe50b0e)
                                .setTitle("Invalid Challenge!")
                                .setDescription("User " + event.getAuthor().getAsMention() + " is already in a duel!");
                    }
                    else
                    {
                        if (messageText.length != 3)
                        {
                            embedBuilder.setColor(0xe50b0e)
                                    .setTitle("Incorrect number of arguments!")
                                    .setDescription("The 'duel' command takes exactly 2 arguments."
                                            + "\nUsage: " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                        }
                        else
                        {
                            if (event.getMessage().getMentionedMembers().size() != 1)
                            {
                                embedBuilder.setColor(0xe50b0e)
                                        .setTitle("Incorrect arguments!")
                                        .setDescription("The 'duel' command takes exactly 2 arguments, the first of which " +
                                                "MUST be a player " + "mention" + "\nUsage: " + DeathRollMain.getPrefix() +
                                                "duel [@player] [bet amount]");
                            }
                            else
                            {
                                if (event.getMessage().getMentionedMembers().get(0).getUser().isBot())
                                {
                                    embedBuilder.setColor(0xe50b0e)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("You can not challenge a bot to a duel!");
                                }
                                else if (event.getMessage().getMentionedMembers().get(0).getId().equals(event.getAuthor().getId()))
                                {
                                    embedBuilder.setColor(0xe50b0e)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("You can not challenge yourself to a duel!");
                                }
                                else if (SQLiteConnection.isUserInDuel(event.getMessage().getMentionedMembers().get(0)
                                        .getId()))
                                {
                                    embedBuilder.setColor(0xe50b0e)
                                            .setTitle("Invalid Challenge:")
                                            .setDescription("User " + event.getMessage().getMentionedMembers().get(0)
                                                    .getId() + " is already in a duel!");
                                }
                                else
                                {
                                    if (SQLiteConnection.isUserInDuel(event.getMessage().getMentionedMembers().get(0).getId()))
                                    {
                                        embedBuilder.setColor(0xe50b0e)
                                                .setTitle("Challenged player already in a duel!")
                                                .setDescription("User " + event.getMessage().getMentionedMembers().get(0)
                                                        .getAsMention() + " is currently in a duel.");
                                    }
                                    else
                                    {
                                        try
                                        {
                                            int parsed = Integer.parseInt(messageText[2]);

                                            int userScore = SQLiteConnection.getUserScore(event.getAuthor().getId());

                                            if (parsed > userScore)
                                            {
                                                embedBuilder.setColor(0xe50b0e)
                                                        .setTitle("Not enough score to bet!")
                                                        .setDescription("User " + event.getAuthor().getAsMention()
                                                                + " currently has a score of " + userScore + ".");
                                            }
                                            else
                                            {
                                                embedBuilder.setColor(0x19ed0e)
                                                        .setTitle("Duel request:")
                                                        .setDescription("Hello " + event.getMessage().getMentionedMembers().get(0).getAsMention()
                                                                + ", you have been challenged to a duel by " + event.getAuthor().getAsMention() + "!"
                                                                + "\nBet amount: " + parsed + "\nDo you accept?");

                                                SQLiteConnection.setUserRequestingDuelState(event.getAuthor().getId(), 1);
                                            }
                                        }
                                        catch (NumberFormatException e)
                                        {
                                            embedBuilder.setColor(0xe50b0e)
                                                    .setTitle("Incorrect argument value!")
                                                    .setDescription("The 'duel' command takes exactly 2 arguments."
                                                            + "\nUsage: " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    embedBuilder.setColor(0xe50b0e)
                            .setTitle("User not registered!")
                            .setDescription("To use the 'duel' command, you must be registered." +
                                    "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
        else
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (event.getMessage().getEmbeds().size() != 1)
            {
                embedBuilder.setColor(0xe50b0e)
                        .setTitle("Unexpected Error!")
                        .setDescription("Target message either composed by multiple embeds or none." +
                                "\nPlease contact a bot developer.");
            }
            else
            {
                String title = event.getMessage().getEmbeds().get(0).getTitle();
                if (title != null && title.equals("Duel request:"))
                {
                    event.getMessage().addReaction("✅").queue();
                    event.getMessage().addReaction("❌").queue();
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event)
    {
        if (!event.getUser().isBot())
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            Message message = event.getReaction().getChannel().retrieveMessageById(event.getMessageId()).complete();

            if (message.getEmbeds().size() == 1)
            {
                MessageEmbed messageEmbed = message.getEmbeds().get(0);
                if (messageEmbed.getTitle() != null && messageEmbed.getTitle().equals("Duel request:"))
                {
                    if(messageEmbed.getDescription() != null)
                    {
                        String[] description = messageEmbed.getDescription().split(" ");

                        String challenged = description[1].replace("<@", "")
                                .replace(">,", "").replace("!", "");

                        String challenging = description[10].replace("<@", "")
                                .replace(">!\nBet", "").replace("!", "");

                        if (SQLiteConnection.isUserRegistered(event.getUserId()))
                        {
                            int parsedBet = -1;
                            try
                            {
                                parsedBet = Integer.parseInt(description[12].split("\n")[0].trim());
                            }
                            catch (NumberFormatException e)
                            {
                                embedBuilder.setColor(0xe50b0e)
                                        .setTitle("Unexpected Error!")
                                        .setDescription("Incorrect bet amount." +
                                                "\nPlease contact a bot developer.");

                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                            }

                            if (event.getUser().getId().equals(challenged) && parsedBet >= 0)
                            {
                                int userScore = SQLiteConnection.getUserScore(event.getUserId());
                                if (userScore < parsedBet)
                                {
                                    embedBuilder.setColor(0xe50b0e)
                                            .setTitle("Not enough score to accept duel!")
                                            .setDescription("User " + event.getUser().getAsMention()
                                                    + " currently has a score of " + userScore + ".");

                                    SQLiteConnection.setUserRequestingDuelState(challenging, 0);
                                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                                    message.delete().queue();
                                }
                                else
                                {
                                    if (SQLiteConnection.isUserInDuel(challenged))
                                    {
                                        embedBuilder.setColor(0xe50b0e)
                                                .setTitle("Could not accept challenge!")
                                                .setDescription("User <@" + challenged + "> is already in a duel!");

                                        SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                                    }
                                    else
                                    {
                                        if (event.getReactionEmote().getEmoji().equals("✅"))
                                        {
                                            embedBuilder.setColor(0x19ed0e)
                                                    .setTitle("Challenge accepted!")
                                                    .setDescription("User <@" + challenged + "> has accepted <@" + challenging +
                                                            ">'s duel challenge!");

                                            SQLiteConnection.updateUserDuelStarted(challenged, challenging, parsedBet);
                                            SQLiteConnection.updateUserDuelStarted(challenging, challenged, parsedBet);

                                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                                            message.delete().queue();
                                        }
                                        else if (event.getReactionEmote().getEmoji().equals("❌"))
                                        {
                                            embedBuilder.setColor(0xe50b0e)
                                                    .setTitle("Challenge declined!")
                                                    .setDescription("User <@" + challenged + "> has declined <@" + challenging +
                                                            ">'s duel challenge!");

                                            SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                                            message.delete().queue();
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            embedBuilder.setColor(0xe50b0e)
                                    .setTitle("User not registered!")
                                    .setDescription("To accept a duel, you must be registered." +
                                            "\nTo do so, run the " + DeathRollMain.getPrefix() + "register command.");

                            SQLiteConnection.setUserRequestingDuelState(challenging, 0);

                            event.getChannel().sendMessage(embedBuilder.build()).queue();
                            message.delete().queue();
                        }
                    }
                }
            }
        }
    }
}
