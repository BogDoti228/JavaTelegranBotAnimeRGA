package bot.commands;

import bot.Bot;
import bot.commands.sendCommands.SendCommand;
import bot.content.GoogleContentFile;
import bot.overseersModule.InfoController;
import bot.overseersModule.ReportController;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ReportCommand implements Command{
    private enum Condition{
        NOT_STARRED, NO_REPORTED_FILE, WAITING_TEXT_REPORT, FINISHED
    }

    private Condition condition = Condition.NOT_STARRED;
    private SendCommand reportedFile;
    private String textReport;

    @Override
    public void startExecute(Update update, Bot bot) {
        var chatId = update.getMessage().getChatId();
        reportedFile = InfoController.INSTANCE.getLastSentContent(chatId);
        if (reportedFile == null){
            condition = Condition.NO_REPORTED_FILE;
            bot.sendTextMessage(chatId, "Контент для отправки репорта не был найден");
        } else {
            bot.sendTextMessage(chatId, "Укажите причину репорта");
            condition = Condition.WAITING_TEXT_REPORT;
        }
    }

    @Override
    public boolean shouldContinue() {
        return condition != Condition.FINISHED && condition != Condition.NO_REPORTED_FILE;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        if (condition != Condition.WAITING_TEXT_REPORT){
            throw new UnsupportedOperationException();
        }
        var chatId = update.getMessage().getChatId();
        if (!update.getMessage().hasText()){
            bot.sendTextMessage(chatId, "Вы не указали текст репорта");
        } else {
            textReport = update.getMessage().getText();
            condition = Condition.FINISHED;
            ReportController.INSTANCE.addNewReport(this);
            bot.sendTextMessage(chatId, "Спасибо за репорт, вкоре модераторы его проверят");
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.REPORT;
    }

    public String getTextReport(){
        return this.textReport;
    }

    public GoogleContentFile getReportedFile(){
        return reportedFile.getSentFile();
    }
}
