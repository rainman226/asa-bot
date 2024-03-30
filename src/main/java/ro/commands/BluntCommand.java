package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class BluntCommand extends Command {
    public BluntCommand() {
        this.name = "blunt";
        this.help = "M-am prajit m-am sparta!";
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        event.reply("M-am prajit m-am sparta!");
    }
}

