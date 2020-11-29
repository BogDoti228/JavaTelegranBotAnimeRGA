package bot.commands.moderatorCommands;

import bot.Bot;
import bot.commands.ReportCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CheckReportsCommand extends ModeratorCommand {

    private ReportCommand currentReport;

    public CheckReportsCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public void startExecute(Update update, Bot bot) {
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
            case "/delete":
                bot.getReportController().deleteReportedFile(currentReport);
                bot.sendTextMessage(chatId, "Удаление прошло успешно");
                break;
            case "/next":
                toTheNextReport(bot);
                break;
            default:
                bot.sendTextMessage(chatId, "Неизвестная команда, используйте /close, /delete или /next");
        }
    }

    private void toTheNextReport(Bot bot){
        currentReport = bot.getReportController().getReport();
        if (currentReport == null){
            bot.sendTextMessage(chatId, "На данный момент нет репортов");
        } else{
            var reportedFile = currentReport.getReportedFile();
            bot.sendContentObject(chatId,
                    reportedFile.getTelegramInputFile(),
                    reportedFile.getContentType());
            bot.sendTextMessage(chatId, "Причина - " + currentReport.getTextReport());
        }
    }

    private void sendOpeningCheckModeMessage(Bot bot){
        var message = new SendMessage(chatId, "Вы вошли в режим проверки репортов");
        bot.addKeyboardToMessage(message, new String[]{"/close", "/next", "/delete"});
        try {
            bot.execute(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendClosingCheckModeMessage(Bot bot){
        var message = new SendMessage(chatId, "Вы вышли в режим проверки репортов");
        try {
            bot.execute(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
