package bot.commands.receiveCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.content.ContentType;
import bot.commands.UrlsHandler;
import bot.content.PhotoFile;
import bot.tagsConroller.NameCreator;
import bot.BotConstants;
import googleDrive.GoogleDriveClient;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URL;
import java.util.Comparator;

public class ReceivePhotoCommand extends ReceiveContentCommand {
    @Override
    public ContentType getContentType() {
        return ContentType.PHOTO;
    }

    @Override
    public void startExecute(Update update, Bot bot) {
        try {
            var inputPhoto = update.getMessage().getPhoto();
            var fileId = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            var url = getDownloadUrl(fileId, bot.getBotToken());
            var name = NameCreator.INSTANCE.createNameWithTags(update.getMessage().getCaption());
            var googleFile = GoogleDriveClient.INSTANCE.createGoogleFile(
                    BotConstants.INSTANCE.getPHOTO_FOLDER_ID(),
                    "Image/jpg",
                    name + ".jpg",
                    new URL(url).openStream()
            );
            this.receivedFile = new PhotoFile(googleFile);
            UrlsHandler.INSTANCE.addNewFile(receivedFile);
            bot.sendTextMessage(update.getMessage().getChatId(), "Спасибо за вашу фотографию");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.RECEIVE_PHOTO;
    }
}
