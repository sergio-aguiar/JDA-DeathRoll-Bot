package Main;

import Commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DeathRollMain
{
    private static JDA jda;
    private static String prefix = "+";

    public static void main(String[] args)
    {
        try
        {
            jda = JDABuilder.createDefault("NzMxODE5NjkxNDc5MjY5NDI2.XwrnWw.imO83KXv7_N6J26yBTjIdE2sVzM").build();
            jda.addEventListener(new DuelCommand());
            jda.addEventListener(new FreeRollCommand());
            jda.addEventListener(new RankedRollCommand());
            jda.addEventListener(new RegisterCommand());
            jda.addEventListener(new ScoreCommand());
            jda.awaitReady();
        }
        catch (LoginException e)
        {
            e.printStackTrace();
            System.exit(201);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            System.exit(202);
        }
    }

    public static String getPrefix()
    {
        return prefix;
    }
}
