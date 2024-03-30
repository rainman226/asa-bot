package ro;


import com.jagrosh.jdautilities.examples.command.AboutCommand;

import java.awt.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                "a music bot that is [easy to host yourself!](https://github.com/jagrosh/MusicBot) (v)",
                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"});
    }
}