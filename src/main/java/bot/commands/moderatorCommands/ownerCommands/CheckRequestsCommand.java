package bot.commands.moderatorCommands.ownerCommands;

import bot.Bot;
import bot.commands.AskModeratorCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public class CheckRequestsCommand extends OwnerCommand{
    //private enum Conditions implements Serializable {
    //    NOT_STARTED, CHECKING, FINISHED
   // }

  //  private Conditions condition = Conditions.NOT_STARTED;

    private AskModeratorCommand currentRequest;

    public CheckRequestsCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public void startExecute(Update update, Bot bot) {
     //   condition = Conditions.CHECKING;
        if (!isModerator(bot)){
            bot.sendTextMessage(chatId, "У вас нет таких прав");
        } else {
            bot.getModeratorController().openCheckMode(chatId);
            this.sendOpeningCheckModeMessage(bot);
            toTheNextReport(bot);
        }
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return isModerator(bot) && bot.getModeratorController().isModeratorInCheckMode(chatId);
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        if (!update.getMessage().hasText()){
            bot.sendTextMessage(chatId, "Вы не указали текст сообщения");
            return;
        }
        var text = update.getMessage().getText();
        switch (text) {
            case "/close":
                bot.getModeratorController().closeCheckMode(chatId);
                this.sendClosingCheckModeMessage(bot);
                break;
            case "/accept":
                var userId = currentRequest.getIdUser();
                var moderController = bot.getModeratorController();
                if (moderController.isUserModerator(userId)){
                    bot.sendTextMessage(chatId, "Этот пользователь в данный момент уже является модератором");
                }
                else {
                    moderController.addModerator(userId);
                    bot.sendTextMessage(chatId, "Теперь этот чел модер, ты ваще понял кого назначил?");
                }
                break;
            case "/next":
                toTheNextReport(bot);
                break;
            default:
                bot.sendTextMessage(chatId, "Неизвестная команда, используйте /close, /accept или /next");
        }
    }

    private void toTheNextReport(Bot bot){
        currentRequest = bot.getRequestController().getRequest();
        if (currentRequest == null){
            bot.sendTextMessage(chatId, "На данный момент нет запросов на модератора");
        } else{
            bot.sendTextMessage(chatId, "@" + currentRequest.getUserName());
            bot.sendTextMessage(chatId, currentRequest.getTextRequest());
        }
    }

    private void sendOpeningCheckModeMessage(Bot bot){
        var message = new SendMessage(chatId, "Вы вошли в режим проверки запросов на модератора");
        bot.addKeyboardToMessage(message, new String[]{"/close", "/next", "/accept"});
        try {
            bot.execute(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendClosingCheckModeMessage(Bot bot){
        var message = new SendMessage(chatId, "Вы вышли из режима проверки запросов на модератора");
        try {
            bot.execute(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
