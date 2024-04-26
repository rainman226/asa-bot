package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.entities.impl.SafebooruImage;

import java.util.Random;

public class FutabaCmd extends Command {
    private final ImageBoard<SafebooruImage> imgBoard = DefaultImageBoards.SAFEBOORU;

    private final int MAX_LIMIT = 110;

    public FutabaCmd() {
        this.name = "futaba";
        this.help = "Get pictures of Rio Futaba";
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        ImageBoard.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
        try {
            imgBoard.search(MAX_LIMIT, "futaba_rio ").async(images -> {
                String imgURL = images.get(new Random().nextInt(images.size())).getURL();
                System.out.println(imgURL);
                event.reply(imgURL);
            });
        } catch (Exception e) {
            e.printStackTrace();
            event.reply("An error occurred while fetching the image");
        }
    }
}
