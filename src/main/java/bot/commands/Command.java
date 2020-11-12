package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public interface Command extends Serializable {
    public void startExecute(Update update, Bot bot);

    public boolean shouldContinue(Bot bot);

    public void continueExecute(Update update, Bot bot);

    public CommandType getCommandType();
}
