package ro.music.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.User;
import ro.music.queue.Queueable;
import ro.utils.FormatUtil;

public class QueuedTrack implements Queueable
{
    private final AudioTrack track;

    public QueuedTrack(AudioTrack track, User owner)
    {
        this(track, new RequestMetadata(owner));
    }

    public QueuedTrack(AudioTrack track, RequestMetadata rm)
    {
        this.track = track;
        this.track.setUserData(rm == null ? RequestMetadata.EMPTY : rm);
    }

    @Override
    public long getIdentifier()
    {
        return track.getUserData() == null ? 0L : track.getUserData(RequestMetadata.class).getOwner();
    }

    public AudioTrack getTrack()
    {
        return track;
    }

    @Override
    public String toString()
    {
        String entry = "`[" + FormatUtil.formatTime(track.getDuration()) + "]` ";
        AudioTrackInfo trackInfo = track.getInfo();
        entry = entry + (trackInfo.uri.startsWith("http") ? "[**" + trackInfo.title + "**]("+trackInfo.uri+")" : "**" + trackInfo.title + "**");
        return entry + " - <@" + track.getUserData(RequestMetadata.class).getOwner() + ">";
    }
}
