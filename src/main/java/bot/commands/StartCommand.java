package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command {
    private static final String startText = "Привет всем ползьующимся этим ботом=)\n" +
            " напиши /photo чтобы получить фото\n " +
            " напиши /photo tag1 tag2 чтобы получить фото с тегами tag1 и tag2\n " +
            " напиши /photo чтобы получить фото\n " +
            " напиши /video чтобы получить видео\n " +
            " напиши /gif чтобы получить гифку\n " +
            " напиши /report чтобы пожаловаться на файл\n " +
            " фото, видео, гиф можно кидать боту для пополнения базы данных," +
            " кидайте все что сочтете достойным для нас," +
            " всем очень благодарны за использование=)";

    @Override
    public void startExecute(Update update, Bot bot) {
        var chatId = update.getMessage().getChatId();
        bot.sendTextMessage(chatId, startText);
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }
}
