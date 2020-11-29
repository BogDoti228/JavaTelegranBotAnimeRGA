package bot.commands.moderatorCommands.ownerCommands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Objects;

public class SudoCommand extends OwnerCommand {
    public SudoCommand(Long chatId) {
        super(chatId);
    }

    private enum Condition implements Serializable {
        NOT_STARTED, WAITING_CONTACT, FINISHED, CANCELED
    }

    private Condition condition = Condition.NOT_STARTED;

    @Override
    public void startExecute(Update update, Bot bot) {
        if (!isOwner(bot)) {
            bot.sendTextMessage(chatId, "У вас нет таких прав");
        } else {
            bot.sendTextMessage(chatId, "Отправьте контакт, который должен стать модератором");
            condition = Condition.WAITING_CONTACT;
        }
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return isOwner(bot) && condition != Condition.FINISHED && condition != Condition.CANCELED ;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        var inputText = update.getMessage().getText();
        var contact = update.getMessage().getContact();
        if (Objects.equals(inputText, "/cancel")){
            condition = Condition.CANCELED;
            bot.sendTextMessage(chatId, "Выполнение команды прервано");
        }
        if (contact == null){
            bot.sendTextMessage(chatId, "Вы не прислали контант. Если хотите отменить, то пропишите /cancel");
        } else {
            bot.getModeratorController().addModerator(contact.getUserID().longValue());
            bot.sendTextMessage(chatId, "Пользователь стал модератором");
            condition = Condition.FINISHED;
        }
    }
}
