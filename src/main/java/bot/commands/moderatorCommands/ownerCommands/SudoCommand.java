package bot.commands.moderatorCommands.ownerCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.overseersModule.ModeratorController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Objects;

public class SudoCommand extends OwnerCommand {
    private enum Condition implements Serializable {
        NOT_STARTED, WAITING_CONTACT, FINISHED, CANCELED
    }

    private Condition condition = Condition.NOT_STARTED;

    @Override
    public void startExecute(Update update, Bot bot) {
        this.moderatorId = update.getMessage().getChatId();
        if (!isOwner(bot)) {
            bot.sendTextMessage(this.moderatorId, "У вас нет таких прав");
        } else {
            bot.sendTextMessage(moderatorId, "Отправьте контакт, который должен стать модератором");
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
            bot.sendTextMessage(moderatorId, "Выполнение команды прервано");
        }
        if (contact == null){
            bot.sendTextMessage(moderatorId, "Вы не прислали контант. Если хотите отменить, то пропишите /cancel");
        } else {
            bot.getModeratorController().addModerator(contact.getUserID().longValue());
            bot.sendTextMessage(moderatorId, "Пользователь стал модератором");
            condition = Condition.FINISHED;
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SUDO;
    }
}
