package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ro.services.OriconService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OriconCmd extends Command {
    private OriconService service = new OriconService();

    public OriconCmd() {
        this.name = "oricon";
        this.help = "Get the Oricon chart for a specific category (albums, daily, leave blank for weekly)";
        this.arguments = "<category>";
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        //System.out.println("Daily Singles Rankings:\n" + service.getDailySinglesRankings(year, month));
        List<OriconService.Entry> entries = null;
        System.out.println(event.getArgs());
        try {
            if (event.getArgs().equals("albums")) {
                entries = service.getWeeklyAlbumsRankings(year, month);
            }
            else if(event.getArgs().equals("daily")) {
                entries = service.getDailySinglesRankings();
            }
            else {
                entries = service.getWeeklySinglesRankings(year, month);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        switch (event.getArgs()) {
            case "albums":
                embedBuilder.setTitle("Weekly Albums Rankings");
                break;
            case "daily":
                embedBuilder.setTitle("Daily Singles Rankings (" + currentDate.format(formatter) + ")");
                break;
            default:
                embedBuilder.setTitle("Weekly Singles Rankings");
                break;
        }

        for (int i = 0; i < entries.size(); i++) {
            OriconService.Entry entry = entries.get(i);
            String emoji = getEmoji(entry.getStatus());
            String rank = String.valueOf(i + 1);
            String titleWithLink = "[" + entry.getArtist() + " - " + entry.getTitle() + "](" + entry.getLinkUrl() + ")";
            embedBuilder.appendDescription(emoji + " " + rank + ". " + titleWithLink + "\n");
        }

        MessageEmbed messageEmbed = embedBuilder.build();
        event.reply(messageEmbed);
    }

    private String getEmoji(String status) {
        switch (status) {
            case "NEW":
                return ":blue_circle:";
            case "DOWN":
                return ":red_circle:";
            case "UP":
                return ":green_circle:";
            case "STAY":
                return ":yellow_circle:";
            default:
                return "";
        }
    }
}
