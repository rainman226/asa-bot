package ro.music.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import ro.Bot;
import ro.music.MusicCommand;
import ro.music.audio.AudioHandler;
import ro.music.audio.QueuedTrack;
import ro.settings.Settings;


public class RemoveCmd extends MusicCommand
{
    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "removes a song from the queue";
        this.arguments = "<position|ALL>";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getQueue().isEmpty())
        {
            event.replyError("There is nothing in the queue!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all"))
        {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
            if(count==0)
                event.replyWarning("You don't have any songs in the queue!");
            else
                event.replySuccess("Successfully removed your "+count+" entries.");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch(NumberFormatException e) {
            pos = 0;
        }
        if(pos<1 || pos>handler.getQueue().size())
        {
            event.replyError("Position must be a valid integer between 1 and "+handler.getQueue().size()+"!");
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if(!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
        QueuedTrack qt = handler.getQueue().get(pos-1);
        if(qt.getIdentifier()==event.getAuthor().getIdLong())
        {
            handler.getQueue().remove(pos-1);
            event.replySuccess("Removed **"+qt.getTrack().getInfo().title+"** from the queue");
        }
        else if(isDJ)
        {
            handler.getQueue().remove(pos-1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch(Exception e) {
                u = null;
            }
            event.replySuccess("Removed **"+qt.getTrack().getInfo().title
                    +"** from the queue (requested by "+(u==null ? "someone" : "**"+u.getName()+"**")+")");
        }
        else
        {
            event.replyError("You cannot remove **"+qt.getTrack().getInfo().title+"** because you didn't add it!");
        }
    }
}