package ro;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import ro.music.audio.AloneInVoiceHandler;
import ro.music.audio.NowPlayingHandler;
import ro.music.audio.PlayerManager;
import ro.music.playlist.PlaylistLoader;
import ro.settings.SettingsManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final SettingsManager settings;
    private final PlayerManager players;
    private final PlaylistLoader playlists;
    private final NowPlayingHandler nowplaying;
    private final AloneInVoiceHandler aloneInVoiceHandler;

    private JDA jda;

    public Bot(EventWaiter waiter, SettingsManager settings) {
        this.waiter = waiter;
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
        this.settings = settings;
        this.players = new PlayerManager(this);
        this.players.init();
        this.playlists = new PlaylistLoader();
        this.nowplaying = new NowPlayingHandler(this);
        this.nowplaying.init();
        this.aloneInVoiceHandler = new AloneInVoiceHandler(this);
        this.aloneInVoiceHandler.init();
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

//    public void shutdown()
//    {
//        if(shuttingDown)
//            return;
//        shuttingDown = true;
//        threadpool.shutdownNow();
//        if(jda.getStatus()!=JDA.Status.SHUTTING_DOWN)
//        {
//            jda.getGuilds().stream().forEach(g ->
//            {
//                g.getAudioManager().closeAudioConnection();
//                AudioHandler ah = (AudioHandler)g.getAudioManager().getSendingHandler();
//                if(ah!=null)
//                {
//                    ah.stopAndClear();
//                    ah.getPlayer().destroy();
//                }
//            });
//            jda.shutdown();
//        }
//        if(gui!=null)
//            gui.dispose();
//        System.exit(0);
//    }

    public SettingsManager getSettingsManager() {
        return settings;
    }

    public PlaylistLoader getPlaylistLoader() {
        return playlists;
    }

    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }

    public NowPlayingHandler getNowplayingHandler()
    {
        return nowplaying;
    }

    public JDA getJDA()
    {
        return jda;
    }

    public PlayerManager getPlayerManager()
    {
        return players;
    }

    public void closeAudioConnection(long guildId)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild!=null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }
}