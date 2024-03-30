package ro;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;

public class Bot {
    private final EventWaiter waiter;

    private JDA jda;

    public Bot(EventWaiter waiter) {
        this.waiter = waiter;
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

    public void setJDA(JDA jda) {
        this.jda = jda;
    }
}