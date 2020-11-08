package bot.commands.sendCommands;

import bot.commands.CommandType;
import bot.content.ContentType;

public class SendGifCommand extends SendCommand {

    @Override
    public CommandType getCommandType() {
        return CommandType.SEND_GIF;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.GIF;
    }
}
