package Commands;

import Common.CommonEmbeds;
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
 *     <li> Arguments: Either none or a command name/alias.
 *     <li> Purpose: Displays basic information regarding the usage of a command.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.4.0
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

                if (messageText.length != 1 && messageText.length != 2)
                {
                    embedBuilder = CommonEmbeds.errorEmbed("Incorrect Argument Number",
                            "The **help** command takes either **0** or **1** arguments.\n\n" +
                            "**Usage:**\n" +
                                    "```• " + DeathRollMain.getPrefix() + "help\n" +
                                    "• " + DeathRollMain.getPrefix() + "help [command]```",
                            event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                }
                else
                {
                    if (messageText.length == 1)
                    {
                        embedBuilder = CommonEmbeds.mainHelpEmbed(event.getJDA().getSelfUser().getAvatarUrl(),
                                event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    }
                    else
                    {
                        embedBuilder.setColor(CommonEmbeds.EMBED_NEUTRAL);
                        switch(messageText[1].toLowerCase())
                        {
                            case "claimadminpermissions":
                            case "cap":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("ClaimAdminPermissions",
                                        "Claims the ability to use admin commands.\n\n" +
                                        "**Aliases:** ClaimAdminPermissions, cap.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "claimAdminPermissions```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "duel":
                            case "d":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Duel",
                                        "Challenges another user to a ranked duel.\n\n" +
                                        "**Aliases:** Duel, d.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "duel [@player] [bet amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "forfeit":
                            case "df":
                            case "f":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Forfeit",
                                        "Forces the currently ongoing duel to end in a loss for the command user.\n\n" +
                                        "**Aliases:** Forfeit, df, f.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "forfeit```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "froll":
                            case "fr":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("FreeRoll",
                                        "Rolls a random number up to the value of the given argument.\n\n" +
                                        "**Aliases:** Froll, fr.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "froll [maximum roll value]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "giveskulls":
                            case "gs":
                            case "gsk":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("GiveSkulls",
                                        "Gives a player a set amount of skulls.\n\n" +
                                        "**Aliases:** GiveSkulls, gs, gsk.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "giveSkulls [@player] " +
                                                "[skull amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "help":
                            case "h":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Help",
                                        "Displays basic information regarding the usage of a command.\n\n" +
                                        "**Aliases:** Help, h.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "help\n" +
                                                "• " + DeathRollMain.getPrefix() + "help [command]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "profile":
                            case "p":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Profile",
                                        "Displays a user's match and skull information.\n\n" +
                                        "**Aliases:** Profile, p.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "profile\n" +
                                                "• " + DeathRollMain.getPrefix() + "profile [@player]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "roll":
                            case "rr":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("RankedRoll",
                                        "Rolls a random number up to the previously rolled value (or to a 10x the bid " +
                                                "value if the first roll).\n\n" +
                                        "**Aliases:** Roll, rr.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "roll```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "register":
                            case "reg":
                            case "r":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Register",
                                        "Registers the user to the database and grants permission to the usage of " +
                                                "various commands.\n\n" +
                                        "**Aliases:** Register, reg, r.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "register```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "skullsboard":
                            case "leaderboard":
                            case "skb":
                            case "ldb":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("SkullsBoard",
                                        "Displays the top 10 users by (descending) skull value.\n\n" +
                                        "**Aliases:** SkullsBoard, leaderboard, skb, ldb.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "skullsBoard```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "skulls":
                            case "sk":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("Skulls",
                                        "Displays the skull value for the user who used the command.\n\n" +
                                        "**Aliases:** Skulls, sk.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "skulls```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "shutdown":
                            case "sd":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("ShutDown",
                                        "Shuts the application down.\n\n" +
                                        "**Aliases:** Shutdown, sd.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "shutdown```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            case "takeskulls":
                            case "ts":
                            case "tsk":
                                embedBuilder = CommonEmbeds.specificHelpEmbed("TakeSkulls",
                                        "Takes a set amount of skulls from a player.\n\n" +
                                        "**Aliases:** TakeSkulls, ts, tsk.\n" +
                                        "**Usage:**\n" +
                                                "```• " + DeathRollMain.getPrefix() + "takeSkulls [@player] "
                                                + "[skull amount]```",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                                break;
                            default:
                                embedBuilder = CommonEmbeds.errorEmbed("Invalid Command Name",
                                        "Please provide a valid command name or alias as an argument.",
                                        event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                        }
                    }
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
