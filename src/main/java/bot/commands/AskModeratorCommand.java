package bot.commands;

import bot.Bot;
import bot.commands.sendCommands.SendCommand;
import bot.content.GoogleContentFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public class AskModeratorCommand implements Command{
    private enum Condition implements Serializable {
        NOT_STARRED, WAITING_TEXT_REQUEST, FINISHED
    }

    private Condition condition = Condition.NOT_STARRED;
    private String textRequest;
    private Long userId;
    private String userName;

    @Override
    public void startExecute(Update update, Bot bot) {
        userId = update.getMessage().getChatId();
        userName = update.getMessage().getFrom().getUserName();
        bot.sendTextMessage(userId, "Укажите причину того зачем вам становится модератором\n"
        + "кто вы такой");
        condition = Condition.WAITING_TEXT_REQUEST;
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return condition != Condition.FINISHED;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        if (condition != Condition.WAITING_TEXT_REQUEST){
            throw new UnsupportedOperationException();
        }
        if (!update.getMessage().hasText()){
            bot.sendTextMessage(userId, "Вы ничего не указали");
        } else {
            textRequest = update.getMessage().getText();
            condition = Condition.FINISHED;
            bot.getRequestController().addNewRequest(this);
            bot.sendTextMessage(userId, "Шанс того что вы станете модератором крайне мал, все проплачено, функция фо фанчик");
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.REQUEST;
    }

    public String getTextRequest(){
        return this.textRequest;
    }

    public Long getIdUser(){
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }
}
