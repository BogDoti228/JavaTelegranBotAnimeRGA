package bot.commands.receiveCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.content.ContentType;
import bot.commands.UrlsHandler;
import bot.content.GifFile;
import bot.tagsConroller.NameCreator;
import bot.BotConstants;
import googleDrive.GoogleDriveClient;
import it.grabz.grabzit.GrabzItClient;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;

public class ReceiveGifCommand extends ReceiveContentCommand {

    @Override
    public void startExecute(Update update, Bot bot) {
        try {
            var inputGif = update.getMessage().getAnimation();
            var id = inputGif.getFileId();
            var url = this.getDownloadUrl(id, bot.getBotToken());
            var name = NameCreator.INSTANCE.createNameWithTags(update.getMessage().getCaption());
            var converter = new GrabzItClient(
                    BotConstants.INSTANCE.getAPPLICATION_KEY(),
                    BotConstants.INSTANCE.getAPPLICATION_SECRET());
            converter.URLToAnimation(url);
            var stream = new ByteArrayInputStream(converter.SaveTo().getBytes());
            var googleFile = GoogleDriveClient.INSTANCE.createGoogleFile(
                    BotConstants.INSTANCE.getGIF_FOLDER_ID(),
                    "Image/gif",
                    name + ".gif",
                    stream
            );
            this.receivedFile = new GifFile(googleFile);
            UrlsHandler.INSTANCE.addNewFile(receivedFile);
            bot.sendTextMessage(update.getMessage().getChatId(), "Спасибо за вашу гифку");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.RECEIVE_GIF;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.GIF;
    }
}
