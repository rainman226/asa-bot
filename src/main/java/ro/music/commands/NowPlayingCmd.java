package ro.music.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ro.Bot;
import ro.music.audio.AudioHandler;
import ro.music.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class NowPlayingCmd extends MusicCommand
{
    public NowPlayingCmd(Bot bot)
    {
        super(bot);
        this.name = "nowplaying";
        this.help = "shows the song that is currently playing";
        //this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        MessageCreateData m = handler.getNowPlaying(event.getJDA());
        if(m==null)
        {
            event.reply(handler.getNoMusicPlaying(event.getJDA()));
            bot.getNowplayingHandler().clearLastNPMessage(event.getGuild());
        }
        else
        {
            event.reply(m, msg -> bot.getNowplayingHandler().setLastNPMessage(msg));
        }
    }
}