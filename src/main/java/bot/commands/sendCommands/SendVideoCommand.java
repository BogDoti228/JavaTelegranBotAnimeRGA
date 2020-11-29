package bot.commands.sendCommands;

import bot.Bot;
import bot.content.ContentType;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendVideoCommand extends SendCommand {

    public SendVideoCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.VIDEO;
    }
}
