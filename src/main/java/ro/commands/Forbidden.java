package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import ro.Bot;
import ro.utils.JCM;

import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ro.utils.JCM.getImages;
import static ro.utils.JCM.search;

public class Forbidden extends Command {

    private final Bot bot;

    public Forbidden(Bot bot) {
        this.bot = bot;
        this.name = "forbidden";
        //this.help = "You are not allowed to use this command!";
        this.arguments = "<person>";
        this.guildOnly = false;
        this.nsfwOnly = true;
        this.hidden = true;
    }

    protected void execute(CommandEvent event) {
        try {
            List<JCM.Person> searchResults = search(URLEncoder.encode(event.getArgs(), "UTF-8"));
            List<String> images = getImages(searchResults.get(0));

            if (!images.isEmpty()) {
                String initialImageUrl = images.get(0);
                sendInitialMessage(event.getChannel(), initialImageUrl, images);
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.reply("Wrong name mate! Retry!");
        }
    }

    private void sendInitialMessage(MessageChannel channel, String imageUrl, List<String> images) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Initial Image");
        embedBuilder.setImage(imageUrl);

        Button button = Button.primary("changeImage", "Reroll");
        ActionRow actionRow = ActionRow.of(button);

        MessageCreateData messageData = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addComponents(actionRow)
                .build();

        channel.sendMessage(messageData).queue(message -> {
            ButtonListener buttonListener = new ButtonListener(images);
            bot.getJDA().addEventListener(buttonListener);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> {
                message.editMessageComponents(actionRow.asDisabled()).queue(); // Remove the button
                bot.getJDA().removeEventListener(buttonListener);
                scheduler.shutdown(); // Shutdown the scheduler after the task is done
            }, 30, TimeUnit.SECONDS);
        });

    }

    public static class ButtonListener extends ListenerAdapter {
        private final List<String> images;

        public ButtonListener(List<String> images) {
            this.images = images;
        }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            if (event.getComponentId().equals("changeImage")) {
                try {
                    if (!images.isEmpty()) {
                        String newImageUrl = images.get(new Random().nextInt(images.size()));
                        event.deferEdit().queue();
                        editMessage(event.getMessage(), newImageUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //event.reply("Wrong name mate! Retry!").queue();
                }
            }
        }

        private void editMessage(Message message, String newImageUrl) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("New Image");
            embedBuilder.setImage(newImageUrl);

            MessageEditData editData = new MessageEditBuilder()
                    .setEmbeds(embedBuilder.build())
                    .build();

            message.editMessage(editData).queue();
        }
    }
}