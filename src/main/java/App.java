import bot.Bot;
import bot.BotConstants;
import googleDrive.GoogleDriveClient;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

public class App {
    private static final Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        GoogleDriveClient.INSTANCE.init();
        ApiContextInitializer.init();
        Bot bot = new Bot(BotConstants.INSTANCE.getNAME(), BotConstants.INSTANCE.getTOKEN());
        bot.botConnect();
    }
}