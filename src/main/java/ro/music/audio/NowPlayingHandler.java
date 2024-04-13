package ro.music.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ro.Bot;
import ro.music.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NowPlayingHandler
{
    private final Bot bot;
    private final HashMap<Long,Pair<Long,Long>> lastNP; // guild -> channel,message

    public NowPlayingHandler(Bot bot)
    {
        this.bot = bot;
        this.lastNP = new HashMap<>();
    }

    public void init()
    {

            bot.getThreadpool().scheduleWithFixedDelay(() -> updateAll(), 0, 5, TimeUnit.SECONDS);
    }

    public void setLastNPMessage(Message m)
    {
        lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getChannel().getIdLong(), m.getIdLong()));
    }

    public void clearLastNPMessage(Guild guild)
    {
        lastNP.remove(guild.getIdLong());
    }

    private void updateAll()
    {
        Set<Long> toRemove = new HashSet<>();
        for(long guildId: lastNP.keySet())
        {
            Guild guild = bot.getJDA().getGuildById(guildId);
            if(guild==null)
            {
                toRemove.add(guildId);
                continue;
            }
            Pair<Long,Long> pair = lastNP.get(guildId);
            TextChannel tc = guild.getTextChannelById(pair.getKey());
            if(tc==null)
            {
                toRemove.add(guildId);
                continue;
            }
            AudioHandler handler = (AudioHandler)guild.getAudioManager().getSendingHandler();
            MessageCreateData msg = handler.getNowPlaying(bot.getJDA());
            if(msg==null)
            {
                msg = (MessageCreateData) handler.getNoMusicPlaying(bot.getJDA());
                toRemove.add(guildId);
            }
//            try
//            {
//                tc.editMessageById(pair.getValue(), msg).queue(m->{}, t -> lastNP.remove(guildId));
//            }
//            catch(Exception e)
//            {
//                toRemove.add(guildId);
//            }
        }
        toRemove.forEach(id -> lastNP.remove(id));
    }

    // "event"-based methods

    public void onMessageDelete(Guild guild, long messageId)
    {
        Pair<Long,Long> pair = lastNP.get(guild.getIdLong());
        if(pair==null)
            return;
        if(pair.getValue() == messageId)
            lastNP.remove(guild.getIdLong());
    }
}
