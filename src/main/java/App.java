import commands.BotConstants;
import googleDrive.GoogleDriveClient;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

public class App {
    private static final Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        GoogleDriveClient.init();
        ApiContextInitializer.init();
        Bot bot = new Bot(BotConstants.NAME, BotConstants.TOKEN);
        bot.botConnect();
    }
}