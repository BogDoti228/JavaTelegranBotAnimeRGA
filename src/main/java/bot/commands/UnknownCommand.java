package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownCommand implements Command{
    @Override
    public void startExecute(Update update, Bot bot) {
        bot.sendTextMessage(update.getMessage().getChatId(),"Неизвестная команда");
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
        return CommandType.UNKNOWN;
    }
}
