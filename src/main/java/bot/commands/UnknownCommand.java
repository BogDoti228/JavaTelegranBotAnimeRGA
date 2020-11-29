package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownCommand extends Command{
    public UnknownCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public void startExecute(Update update, Bot bot) {
        bot.sendTextMessage(update.getMessage().getChatId(),"Неизвестная команда");
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return false;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        throw new UnsupportedOperationException();
    }
}
