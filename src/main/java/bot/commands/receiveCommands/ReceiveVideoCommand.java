package bot.commands.receiveCommands;

import bot.Bot;
import bot.content.ContentType;
import bot.commands.UrlsHandler;
import bot.content.GifFile;
import bot.tagsConroller.NameCreator;
import bot.BotConstants;
import googleDrive.GoogleDriveClient;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URL;

public class ReceiveVideoCommand extends ReceiveContentCommand {
    public ReceiveVideoCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.VIDEO;
    }

    @Override
    public void startExecute(Update update, Bot bot) {
        try {
            var inputVideo = update.getMessage().getVideo();
            var id = inputVideo.getFileId();
            var url = this.getDownloadUrl(id, bot.getBotToken());
            var name = NameCreator.INSTANCE.createNameWithTags(update.getMessage().getCaption());
            var googleFile = GoogleDriveClient.INSTANCE.createGoogleFile(
                    BotConstants.INSTANCE.getVIDEO_FOLDER_ID(),
                    "Video/mp4",
                    name + ".mp4",
                    new URL(url).openStream()
            );
            this.receivedFile = new GifFile(googleFile);
            UrlsHandler.INSTANCE.addNewFile(receivedFile);
            bot.sendTextMessage(update.getMessage().getChatId(), "Спасибо за ваше видео");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
