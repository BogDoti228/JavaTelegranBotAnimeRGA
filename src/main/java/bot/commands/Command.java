package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    public void startExecute(Update update, Bot bot);

    public boolean shouldContinue();

    public void continueExecute(Update update, Bot bot);

    public CommandType getCommandType();
}
