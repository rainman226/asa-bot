package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import ro.services.OriconService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OriconCmd extends Command {
    private final OriconService service = new OriconService();

    public OriconCmd() {
        this.name = "oricon";
        this.help = "Get the Oricon charts for the current week";
        //this.arguments = "<category>";
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        List<OriconService.Entry> entries = null;

        try {
            entries = service.getWeeklySinglesRankings(year, month);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Weekly Singles Rankings");

        Button button1 = Button.primary("weekly_singles", "Weekly Singles");
        Button button2 = Button.primary("weekly_albums", "Weekly Albums");
        Button button3 = Button.primary("daily_singles", "Daily Singles");

        ActionRow actionRow = ActionRow.of(button1, button2, button3);

        for (int i = 0; i < entries.size(); i++) {
            OriconService.Entry entry = entries.get(i);
            String emoji = getEmoji(entry.getStatus());
            String rank = String.valueOf(i + 1);
            String titleWithLink = "[" + entry.getArtist() + " - " + entry.getTitle() + "](" + entry.getLinkUrl() + ")";
            embedBuilder.appendDescription(emoji + " " + rank + ". " + titleWithLink + "\n");
        }

        MessageCreateData messageData = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addComponents(actionRow)
                .build();

        event.getChannel().sendMessage(messageData).queue(message -> {
            ButtonListener buttonListener = new ButtonListener();
            event.getJDA().addEventListener(buttonListener);
        });
    }

    private static String getEmoji(String status) {
        return switch (status) {
            case "NEW" -> ":blue_circle:";
            case "DOWN" -> ":red_circle:";
            case "UP" -> ":green_circle:";
            case "STAY" -> ":yellow_circle:";
            default -> "";
        };
    }

    public static class ButtonListener extends ListenerAdapter {

        OriconService service = new OriconService();
        public ButtonListener() {}

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            if (event.getComponentId().equals("weekly_singles")) {
                try {
                    List<OriconService.Entry> entries = service.getWeeklySinglesRankings(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
                    event.deferEdit().queue();
                    editMessage(event.getMessage(), "Weekly Singles Rankings", entries);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (event.getComponentId().equals("weekly_albums")) {
                try {
                    List<OriconService.Entry> entries = service.getWeeklyAlbumsRankings(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
                    event.deferEdit().queue();
                    editMessage(event.getMessage(), "Weekly Albums Rankings", entries);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (event.getComponentId().equals("daily_singles")) {
                try {
                    List<OriconService.Entry> entries = service.getDailySinglesRankings();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    event.deferEdit().queue();
                    editMessage(event.getMessage(), "Daily Singles Rankings (" + LocalDate.now().format(formatter) + ")", entries);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void editMessage(Message message, String title, List<OriconService.Entry> list) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(title);

            for (int i = 0; i < list.size(); i++) {
                OriconService.Entry entry = list.get(i);
                String emoji = getEmoji(entry.getStatus());
                String rank = String.valueOf(i + 1);
                String titleWithLink = "[" + entry.getArtist() + " - " + entry.getTitle() + "](" + entry.getLinkUrl() + ")";
                embedBuilder.appendDescription(emoji + " " + rank + ". " + titleWithLink + "\n");
            }

            MessageEditData editData = new MessageEditBuilder()
                    .setEmbeds(embedBuilder.build())
                    .build();

            message.editMessage(editData).queue();
        }
    }
}
