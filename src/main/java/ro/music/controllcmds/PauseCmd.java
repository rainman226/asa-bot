package ro.music.controllcmds;

import com.jagrosh.jdautilities.command.CommandEvent;
import ro.Bot;
import ro.music.DJCommand;
import ro.music.audio.AudioHandler;

public class PauseCmd extends DJCommand
{
    public PauseCmd(Bot bot)
    {
        super(bot);
        this.name = "pause";
        this.help = "pauses the current song";
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getPlayer().isPaused())
        {
            event.replyWarning("The player is already paused! Use `"+event.getClient().getPrefix()+"play` to unpause!");
            return;
        }
        handler.getPlayer().setPaused(true);
        event.replySuccess("Paused **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**. Type `"+event.getClient().getPrefix()+"play` to unpause!");
    }
}
