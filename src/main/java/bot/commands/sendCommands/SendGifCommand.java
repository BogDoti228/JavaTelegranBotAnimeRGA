package bot.commands.sendCommands;

import bot.content.ContentType;

public class SendGifCommand extends SendCommand {

    public SendGifCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.GIF;
    }
}
