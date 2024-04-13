package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SammyCommand extends Command {
    public SammyCommand() {
        this.name = "sammy";
        this.help = "bro";
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        event.reply("https://media.discordapp.net/attachments/416683307686952972/1138167278320496710/IMG-20230807-WA0009.jpg?ex=66142e61&is=6601b961&hm=3fefa5739c0f25ed40e3f92aed4b14e1d0552ea498fa0263b43ec3eda4c60430&=&format=webp&width=376&height=670");
    }
}
