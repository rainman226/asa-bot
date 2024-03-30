package ro.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import java.util.Collection;
import java.util.Collections;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
public class Settings implements GuildSettingsProvider
{
    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    private int volume;
    private String defaultPlaylist;
    private RepeatMode repeatMode;
    private QueueType queueType;
    private String prefix;
    private double skipRatio;

    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, QueueType queueType)
    {
        this.manager = manager;
        try
        {
            this.textId = Long.parseLong(textId);
        }
        catch(NumberFormatException e)
        {
            this.textId = 0;
        }
        try
        {
            this.voiceId = Long.parseLong(voiceId);
        }
        catch(NumberFormatException e)
        {
            this.voiceId = 0;
        }
        try
        {
            this.roleId = Long.parseLong(roleId);
        }
        catch(NumberFormatException e)
        {
            this.roleId = 0;
        }
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
        this.queueType = queueType;
    }

    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, QueueType queueType)
    {
        this.manager = manager;
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
        this.queueType = queueType;
    }

    // Getters
    public TextChannel getTextChannel(Guild guild)
    {
        return guild == null ? null : guild.getTextChannelById(textId);
    }

    public VoiceChannel getVoiceChannel(Guild guild)
    {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }

    public Role getRole(Guild guild)
    {
        return guild == null ? null : guild.getRoleById(roleId);
    }

    public int getVolume()
    {
        return volume;
    }

    public String getDefaultPlaylist()
    {
        return defaultPlaylist;
    }

    public RepeatMode getRepeatMode()
    {
        return repeatMode;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public double getSkipRatio()
    {
        return skipRatio;
    }

    public QueueType getQueueType()
    {
        return queueType;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.emptySet() : Collections.singleton(prefix);
    }

    // Setters
    public void setTextChannel(TextChannel tc)
    {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }

    public void setVoiceChannel(VoiceChannel vc)
    {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }

    public void setDJRole(Role role)
    {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }

    public void setVolume(int volume)
    {
        this.volume = volume;
        this.manager.writeSettings();
    }

    public void setDefaultPlaylist(String defaultPlaylist)
    {
        this.defaultPlaylist = defaultPlaylist;
        this.manager.writeSettings();
    }

    public void setRepeatMode(RepeatMode mode)
    {
        this.repeatMode = mode;
        this.manager.writeSettings();
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
        this.manager.writeSettings();
    }

    public void setSkipRatio(double skipRatio)
    {
        this.skipRatio = skipRatio;
        this.manager.writeSettings();
    }

    public void setQueueType(QueueType queueType)
    {
        this.queueType = queueType;
        this.manager.writeSettings();
    }
}

