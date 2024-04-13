package ro.music.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import ro.Bot;
import ro.music.MusicCommand;
import ro.music.audio.AudioHandler;
import ro.music.audio.QueuedTrack;
import ro.utils.FormatUtil;

import java.util.concurrent.TimeUnit;

public class SearchCmd extends MusicCommand
{
    protected String searchPrefix = "ytsearch:";
    private final OrderedMenu.Builder builder;
    private final String searchingEmoji;

    public SearchCmd(Bot bot)
    {
        super(bot);
        this.searchingEmoji = "\uD83D\uDD0D";
        this.name = "search";
        this.arguments = "<query>";
        this.help = "searches Youtube for a provided query";
        this.beListening = true;
        this.bePlaying = false;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        builder = new OrderedMenu.Builder()
                .allowTextInput(true)
                .useNumbers()
                .useCancelButton(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }
    @Override
    public void doCommand(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Please include a query.");
            return;
        }
        event.reply(searchingEmoji+" Searching... `["+event.getArgs()+"]`",
                m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), searchPrefix + event.getArgs(), new ResultHandler(m,event)));
    }

    private class ResultHandler implements AudioLoadResultHandler
    {
        private final Message m;
        private final CommandEvent event;

        private ResultHandler(Message m, CommandEvent event)
        {
            this.m = m;
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track)
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" Added **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0 ? "to begin playing"
                    : " to the queue at position "+pos))).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            builder.setColor(event.getSelfMember().getColor())
                    .setText(FormatUtil.filter(event.getClient().getSuccess()+" Search results for `"+event.getArgs()+"`:"))
                    .setChoices(new String[0])
                    .setSelection((msg,i) ->
                    {
                        AudioTrack track = playlist.getTracks().get(i-1);
                        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                        int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
                        event.replySuccess("Added **" + FormatUtil.filter(track.getInfo().title)
                                + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos==0 ? "to begin playing"
                                : " to the queue at position "+pos));
                    })
                    .setCancel((msg) -> {})
                    .setUsers(event.getAuthor())
            ;
            for(int i=0; i<4 && i<playlist.getTracks().size(); i++)
            {
                AudioTrack track = playlist.getTracks().get(i);
                builder.addChoices("`["+FormatUtil.formatTime(track.getDuration())+"]` [**"+track.getInfo().title+"**]("+track.getInfo().uri+")");
            }
            builder.build().display(m);
        }

        @Override
        public void noMatches()
        {
            m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`.")).queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                m.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError()+" Error loading track.").queue();
        }
    }
}
