package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.List;

public abstract class Command implements Serializable {
    protected Long chatId;

    public Command(Long chatId){
        this.chatId = chatId;
    }

    public abstract void startExecute(Update update, Bot bot);

    public abstract boolean shouldContinue(Bot bot);

    public abstract void continueExecute(Update update, Bot bot);
}
