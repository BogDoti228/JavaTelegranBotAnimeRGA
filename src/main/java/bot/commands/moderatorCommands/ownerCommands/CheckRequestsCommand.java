package bot.commands.moderatorCommands.ownerCommands;

import bot.Bot;
import bot.commands.AskModeratorCommand;
import bot.commands.CommandType;
import bot.commands.ReportCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CheckRequestsCommand extends OwnerCommand{

    private AskModeratorCommand currentRequest;

    @Override
    public void startExecute(Update update, Bot bot) {
        this.moderatorId = update.getMessage().getChatId();
        if (!isModerator(bot)){
            bot.sendTextMessage(this.moderatorId, "У вас нет таких прав");
        } else {
            bot.getModeratorController().openCheckMode(this.moderatorId);
            bot.sendTextMessage(this.moderatorId, "Вы вошли в режим проверки запросов на модератора");
            toTheNextReport(bot);
        }
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return isModerator(bot) && bot.getModeratorController().isModeratorInCheckMode(this.moderatorId);
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        if (!update.getMessage().hasText()){
            bot.sendTextMessage(moderatorId, "Вы не указали текст сообщения");
            return;
        }
        var text = update.getMessage().getText();
        switch (text) {
            case "/close":
                bot.getModeratorController().closeCheckMode(this.moderatorId);
                bot.sendTextMessage(moderatorId, "Вы вышли из режима проверки запросов на модератора");
                break;
            case "/accept":
                var userId = currentRequest.getIdUser();
                var moderController = bot.getModeratorController();
                if (moderController.isUserModerator(userId)){
                    bot.sendTextMessage(moderatorId, "Этот пользователь в данный момент уже является модератором");
                }
                else {
                    moderController.addModerator(userId);
                    bot.sendTextMessage(moderatorId, "Теперь этот чел модер, ты ваще понял кого назначил?");
                }
                break;
            case "/next":
                toTheNextReport(bot);
                break;
            default:
                bot.sendTextMessage(moderatorId, "Неизвестная команда, используйте /close, /accept или /next");
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.CHECK_REQUESTS;
    }

    private void toTheNextReport(Bot bot){
        currentRequest = bot.getRequestController().getRequest();
        if (currentRequest == null){
            bot.sendTextMessage(moderatorId, "На данный момент нет запросов на модератора");
        } else{
            bot.sendTextMessage(moderatorId, "@" + currentRequest.getUserName());
            bot.sendTextMessage(moderatorId, currentRequest.getTextRequest());
        }
    }
}
