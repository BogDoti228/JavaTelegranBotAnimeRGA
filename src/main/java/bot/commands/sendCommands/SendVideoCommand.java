package bot.commands.sendCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.content.ContentType;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendVideoCommand extends SendCommand {

    @Override
    public CommandType getCommandType() {
        return CommandType.SEND_VIDEO;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.VIDEO;
    }
}
