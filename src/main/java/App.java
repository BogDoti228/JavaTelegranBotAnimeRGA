import bot.Bot;
import constants.BotConstants;
import com.google.api.services.drive.model.File;
import googleDrive.GoogleDriveClient;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class App implements Serializable{
    private static final Logger log = Logger.getLogger(App.class);
    private static final boolean loadLastCondition = true;
    private static final boolean saveConditions = true;

    public static void main(String[] args) throws Exception {
        GoogleDriveClient.INSTANCE.init();

        ApiContextInitializer.init();
        Bot bot = new Bot(BotConstants.INSTANCE.getNAME(), BotConstants.INSTANCE.getTOKEN());
        if (loadLastCondition){
            loadLastCondition(bot);
        }
        bot.botConnect();
        if (saveConditions) {
            var thread = new Thread(() -> saveBotCondition(bot));
            thread.start();
        }
    }

    private static  void loadLastCondition(Bot bot){
        try {
            var lastBotConditions = GoogleDriveClient.INSTANCE.getAllFilesInFolder(
                    BotConstants.INSTANCE.getBOT_INFO_FOLDER_ID()
            );
            File lastCondition = null;
            for (var condition : lastBotConditions) {
                if (lastCondition == null) {
                    lastCondition = condition;
                } else if (lastCondition.getCreatedTime().getValue() < condition.getCreatedTime().getValue()) {
                    lastCondition = condition;
                }
            }
            if (lastCondition != null) {
                bot.load(new URL(lastCondition.getWebContentLink()).openStream());
            }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void saveBotCondition(Bot bot) {
        try {
            while (true) {
                var periodInSeconds = 10;
                Thread.sleep(periodInSeconds * 1000);
                var stream = new ByteArrayOutputStream();
                bot.save(stream);
                stream.close();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss: dd:MM:yyyy");
                GoogleDriveClient.INSTANCE.createGoogleFile(
                        BotConstants.INSTANCE.getBOT_INFO_FOLDER_ID(),
                        "*/*",
                        "save" + sdf.format(cal.getTime()),
                        stream.toByteArray()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
