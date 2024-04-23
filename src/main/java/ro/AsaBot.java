package ro;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ro.commands.*;
import ro.music.commands.*;
import ro.music.controllcmds.*;
import ro.settings.SettingsManager;

import java.awt.*;
import java.util.Arrays;

public class AsaBot {
    public final static GatewayIntent[] INTENTS = {GatewayIntent.MESSAGE_CONTENT,GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};

    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
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

        AboutCommand aboutCommand = new AboutCommand(Color.BLUE, "AsaBot, a music and fun activities bot developed" +
                " by the lodge", new String[]{"High quality music player", "Pictures of Asa Mitaka", "A lot of other fun stuff"},
                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);

        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix("!")
                .setOwnerId("240159759670575104")
                .setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26")
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("ba flo"))
                .setHelpWord("help")
                .setGuildSettingsManager(settings)
                .addCommands(
                        new BluntCommand(),
                        new SammyCommand(),
                        new AsaCmd(),
                        new MakimaCmd(),
                        new PowerCmd(),
                        new Forbidden(bot),

                        new PlayCmd(bot),
                        new SkipCmd(bot),
                        new StopCmd(bot),
                        new QueueCmd(bot),
                        new ShuffleCmd(bot),
                        new NowPlayingCmd(bot),
                        new SearchCmd(bot),
                        new RemoveCmd(bot),

                        new VolumeCmd(bot),
                        new PauseCmd(bot),
                        new PlayNextCmd(bot),
                        new RepeatCmd(bot),
                        new MoveTrackCmd(bot)
                );

        return cb.build();
    }
}

