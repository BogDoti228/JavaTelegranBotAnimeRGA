package bot;

import bot.commands.*;
import bot.commands.sendCommands.SendCommand;
import bot.content.ContentType;
import bot.overseersModule.ModeratorController;
import bot.overseersModule.ReportController;
import bot.overseersModule.RequestController;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.http.impl.io.EmptyInputStream;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import bot.overseersModule.InfoController;

import java.io.*;
import java.util.ArrayList;


public class Bot extends TelegramLongPollingBot implements Serializable {
    private static class BotInfo implements Serializable {
        private ModeratorController moderatorController = new ModeratorController();

        private ReportController reportController = new ReportController();

        private InfoController infoController = new InfoController();

        private RequestController requestController = new RequestController();
    }

    private Logger log = Logger.getLogger(Bot.class);

    final int RECONNECT_PAUSE = 10000;

    @Setter
    @Getter
    private String botName;

    private BotInfo info;

    @Setter
    private String botToken;

    public Bot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
        this.info = new BotInfo();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        var lastCommand = this.info.infoController.getLastCommand(chatId);
        if (lastCommand != null && lastCommand.shouldContinue(this)) {
            lastCommand.continueExecute(update, this);
        } else {
            handleNewCommand(update);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    public ModeratorController getModeratorController(){
        return this.info.moderatorController;
    }

    public InfoController getInfoController(){
        return this.info.infoController;
    }

    public ReportController getReportController(){
        return this.info.reportController;
    }

    public RequestController getRequestController(){
        return this.info.requestController;
    }

    private void handleNewCommand(Update update) {
        var chatId = update.getMessage().getChatId();
        Command command = CommandParser.INSTANCE.getCommandByUpdate(update);
        command.startExecute(update, this);
        this.info.infoController.addLastCommand(chatId, command);
    }

    public void sendTextMessage(Long chatId, String text){
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendContentObject(Long chatId ,InputFile file, ContentType type) {
        try {
            switch (type) {
                case PHOTO:
                    var sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(file);
                    sendPhoto.setChatId(chatId);
                    execute(sendPhoto);
                    break;
                case VIDEO:
                    var sendVideo = new SendVideo();
                    sendVideo.setVideo(file);
                    sendVideo.setChatId(chatId);
                    execute(sendVideo);
                    break;
                case GIF:
                    var sendAnimation = new SendAnimation();
                    sendAnimation.setAnimation(file);
                    sendAnimation.setChatId(chatId);
                    execute(sendAnimation);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(OutputStream stream){
        try {
            var outputStream =  new ObjectOutputStream(stream);
            outputStream.writeObject(this.info);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void load(InputStream stream){
        try{
            var object = new ObjectInputStream(stream).readObject();
            this.info = (BotInfo) object;
            System.out.println("Я загрузил");
        } catch (Exception e){
            System.out.println("Ошибка с загрузкой состояния");
        }
    }
}