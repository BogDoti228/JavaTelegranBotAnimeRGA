import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

<<<<<<< HEAD
=======

>>>>>>> origin/master
public class App {
    private static final Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        ApiContextInitializer.init();
<<<<<<< HEAD
        Bot animeRGABot = new Bot("AnimeRGABot", "heheheh");
        animeRGABot.botConnect();
=======
        Bot bot = new Bot(BotConstants.NAME, BotConstants.TOKEN);
        bot.botConnect();
>>>>>>> origin/master
    }
}