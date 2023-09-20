package Config;
import Music.MusicCommandManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;
import java.util.List;

public class Bot {
    static JDA jda;

    public Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.getToken())
                .enableIntents(Config.getIntents())
                .enableCache(Config.getFlags())
                .addEventListeners(new MusicCommandManager())
                .setActivity(Activity.listening("Ride on Time"))
                .build();
    }

}