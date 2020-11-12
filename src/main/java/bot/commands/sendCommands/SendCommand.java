package bot.commands.sendCommands;

import bot.Bot;
import bot.commands.*;
import bot.content.ContentType;
import bot.content.GoogleContentFile;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class SendCommand implements Command {
    private GoogleContentFile sentFile;

    @Override
    public void startExecute(Update update, Bot bot) {
        var tagsQuery = CommandParser.INSTANCE.getCommandParameters(
                update.getMessage().getText(), getCommandType());
        sentFile = UrlsHandler.INSTANCE.getFile(
                getContentType(),
                tagsQuery
        );
        var chatId = update.getMessage().getChatId();
        if (sentFile == null){
            bot.sendTextMessage(chatId, "Подобный контент не найден");
        }
        bot.sendContentObject(chatId, sentFile.getTelegramInputFile(), sentFile.getContentType());
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return false;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        throw new UnsupportedOperationException();
    }

    public GoogleContentFile getSentFile(){
        return sentFile;
    };

    public abstract ContentType getContentType();
}
