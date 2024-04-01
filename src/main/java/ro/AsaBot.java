package ro;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ro.commands.BluntCommand;
import ro.commands.SammyCommand;
import ro.music.commands.*;
import ro.music.dj.VolumeCmd;
import ro.settings.SettingsManager;

import java.util.Arrays;

public class AsaBot {
    public final static GatewayIntent[] INTENTS = {GatewayIntent.MESSAGE_CONTENT,GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};

    public static void main(String[] args)
    {
        startBot(args[0]);
    }

    private static void startBot(String token) {
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, settings);
        CommandClient client = createCommandClient(bot, settings);

        try {
            JDA jda = JDABuilder.create(token, Arrays.asList(INTENTS))
                    .setActivity(Activity.playing("ba flo"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(client, waiter, new MyListener())
                    .build();

            bot.setJDA(jda);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandClient createCommandClient(Bot bot, SettingsManager settings) {
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix("!")
                .setOwnerId("240159759670575104")
                .setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26")
                .setHelpWord("help")
                .setGuildSettingsManager(settings)
                .addCommands(
                        new BluntCommand(),
                        new SammyCommand(),

                        new PlayCmd(bot),
                        new SkipCmd(bot),
                        new StopCmd(bot),
                        new QueueCmd(bot),
                        new ShuffleCmd(bot),
                        new NowPlayingCmd(bot),
                        new SearchCmd(bot),
                        new RemoveCmd(bot),

                        new VolumeCmd(bot)
                );

        return cb.build();
    }
}

