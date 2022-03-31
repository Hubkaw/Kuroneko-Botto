package Config;
import Music.MusicCommandManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;

public class Bot {
    static JDA jda;

    public Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.getToken())
                .enableCache(CacheFlag.VOICE_STATE)
                .enableCache(CacheFlag.EMOTE)
                .addEventListeners(new MusicCommandManager())
                .setActivity(Activity.listening("Ride on Time - Tatsuro Yamashita"))
                .build();
    }

}
