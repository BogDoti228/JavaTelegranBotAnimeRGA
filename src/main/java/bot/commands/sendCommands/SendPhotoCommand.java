package bot.commands.sendCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.content.ContentType;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendPhotoCommand extends SendCommand {

    @Override
    public CommandType getCommandType() {
        return CommandType.SEND_PHOTO;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.PHOTO;
    }
}
