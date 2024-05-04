package com.kuroneko.misc;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class KuronekoEmbed extends EmbedBuilder {
    public KuronekoEmbed(){
        super();
        setColor(new Color(110, 0, 127));
        setThumbnail("https://safebooru.org//samples/1054/sample_d2f4a454783dd4817b78540495006764dea6066e.jpg");
    }

    public static void cos(){
        System.out.println("aaa");
    }
}
