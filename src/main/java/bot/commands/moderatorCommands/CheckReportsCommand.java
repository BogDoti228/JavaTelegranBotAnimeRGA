package bot.commands.moderatorCommands;

import bot.Bot;
import bot.commands.CommandType;
import bot.commands.ReportCommand;
import bot.overseersModule.ModeratorController;
import bot.overseersModule.ReportController;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CheckReportsCommand extends ModeratorCommand {

    private ReportCommand currentReport;

    @Override
    public void startExecute(Update update, Bot bot) {
        this.moderatorId = update.getMessage().getChatId();
        if (!isModerator(bot)){
            bot.sendTextMessage(this.moderatorId, "У вас нет таких прав");
        } else {
            bot.getModeratorController().openCheckMode(this.moderatorId);
            bot.sendTextMessage(this.moderatorId, "Вы вошли в режим модератора");
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
                bot.sendTextMessage(moderatorId, "Вы вышли из режима модератора");
                break;
            case "/delete":
                bot.getReportController().deleteReportedFile(currentReport);
                bot.sendTextMessage(moderatorId, "Удаление прошло успешно");
                break;
            case "/next":
                toTheNextReport(bot);
                break;
            default:
                bot.sendTextMessage(moderatorId, "Неизвестная команда, используйте /close, /delete или /next");
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.CHECK_REPORTS;
    }

    private void toTheNextReport(Bot bot){
        currentReport = bot.getReportController().getReport();
        if (currentReport == null){
            bot.sendTextMessage(moderatorId, "На данный момент нет репортов");
        } else{
            var reportedFile = currentReport.getReportedFile();
            bot.sendContentObject(moderatorId,
                    reportedFile.getTelegramInputFile(),
                    reportedFile.getContentType());
            bot.sendTextMessage(moderatorId, "Причина - " + currentReport.getTextReport());
        }
    }
}
