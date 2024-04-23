package ro.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.entities.impl.DanbooruImage;
import net.kodehawa.lib.imageboards.entities.impl.SafebooruImage;

import java.util.Random;

public class PowerCmd extends Command {

    private final ImageBoard<DanbooruImage> imgBoard = DefaultImageBoards.DANBOORU;

    private final ImageBoard<SafebooruImage> safeImgBoard = DefaultImageBoards.SAFEBOORU;

    private final int MAX_LIMIT = 2105;

    public PowerCmd() {
        this.name = "power";
        this.arguments = "<tag>";
        this.help = "Get pictures of Power";
        this.guildOnly = false;
    }

    //power_(chainsaw_man)
    protected void execute(CommandEvent event) {
        ImageBoard.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
        try {
            if(event.getArgs().isEmpty()) {
                safeImgBoard.search(MAX_LIMIT, "power_(chainsaw_man) " + event.getArgs()).async(images -> {
                    event.reply(images.get(new Random().nextInt(images.size())).getURL());
                });
            } else {
                imgBoard.search(MAX_LIMIT, "power_(chainsaw_man) " + event.getArgs()).async(images -> {
                    event.reply(images.get(new Random().nextInt(images.size())).getURL());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.reply("An error occurred while fetching the image");
        }
    }
}
