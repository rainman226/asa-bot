package ro.music.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import ro.Bot;
import ro.music.audio.AudioHandler;
import ro.music.audio.RequestMetadata;
import ro.music.MusicCommand;
import ro.utils.FormatUtil;
public class SkipCmd extends MusicCommand
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.help = "votes to skip the current song";
        //this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        double skipRatio = bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio();
//        if(skipRatio == -1) {
//            skipRatio = bot.getConfig().getSkipRatio();
//        }
        if(event.getAuthor().getIdLong() == rm.getOwner() || skipRatio == 0)
        {
            event.reply(event.getClient().getSuccess()+" Skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**");
            handler.getPlayer().stopTrack();
        }
        else
        {
            int listeners = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if(handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning()+" You already voted to skip this song `[";
            else
            {
                msg = event.getClient().getSuccess()+" You voted to skip the song `[";
                handler.getVotes().add(event.getAuthor().getId());
            }
            int skippers = (int)event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int)Math.ceil(listeners * skipRatio);
            msg += skippers + " votes, " + required + "/" + listeners + " needed]`";
            if(skippers>=required)
            {
                msg += "\n" + event.getClient().getSuccess() + " Skipped **" + handler.getPlayer().getPlayingTrack().getInfo().title
                        + "** " + (rm.getOwner() == 0L ? "(autoplay)" : "(requested by **" + FormatUtil.formatUsername(rm.user) + "**)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }

}

