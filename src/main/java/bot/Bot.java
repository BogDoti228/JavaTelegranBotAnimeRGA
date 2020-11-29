package bot;

import bot.commands.*;
import bot.content.ContentType;
import bot.overseersModule.ModeratorController;
import bot.overseersModule.ReportController;
import bot.overseersModule.RequestController;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import bot.overseersModule.InfoController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.StampedLock;


public class Bot extends TelegramLongPollingBot implements Serializable {
    private static class BotInfo implements Serializable {
        private ModeratorController moderatorController = new ModeratorController();

        private ReportController reportController = new ReportController();

        private InfoController infoController = new InfoController();

        private RequestController requestController = new RequestController();
    }

    final int RECONNECT_PAUSE = 10000;

    @Setter
    @Getter
    private String botName;

    private final String[] commands = new String[]{
            "/photo", "/video", "/gif", "/report", "/check_reports", "/ask_sudo",
            "/check_requests", "/sudo", "/desudo"
    };

    private BotInfo info;

    @Setter
    private String botToken;
    
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    private HashMap<Long, StampedLock> locks = new HashMap<>();

    public Bot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
        this.info = new BotInfo();
    }

    @Override
    public void onUpdateReceived(Update update) {
        var chatId = update.getMessage().getChatId();
        if (!locks.containsKey(chatId)){
            locks.put(chatId, new StampedLock());
        }
        executor.execute(() -> handlePlayerRequest(update));
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

    private void handlePlayerRequest(Update update){
        Long chatId = update.getMessage().getChatId();
        var lock = locks.get(chatId);
        var stamp = lock.writeLock();
        System.out.println("Начал" + chatId);
        var lastCommand = this.info.infoController.getLastCommand(chatId);
        if (lastCommand != null && lastCommand.shouldContinue(this)) {
            lastCommand.continueExecute(update, this);
        } else {
            handleNewCommand(update);
        }
        lastCommand = this.info.infoController.getLastCommand(chatId);
        if (!lastCommand.shouldContinue(this)){
            this.showKeyboardWithCommands(chatId);
        }
        System.out.println("Закончил" + chatId);
        lock.unlockWrite(stamp);
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

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
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

    public void addKeyboardToMessage(Object message, String[] buttons){
        var markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(this.getKeyboardRaws(buttons));
        try {
            var cls = message.getClass();
            cls.getMethod("setReplyMarkup", ReplyKeyboard.class).invoke(message, markup);
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

    private void showKeyboardWithCommands(Long chatId){
        var commands = this.commands;
        var messageText = "Доступные команды:" + String.join(" ", commands);
        var message = new SendMessage(chatId, messageText);
        this.addKeyboardToMessage(message, commands);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleNewCommand(Update update) {
        var chatId = update.getMessage().getChatId();
        Command command = CommandParser.INSTANCE.getCommandByUpdate(update);
        command.startExecute(update, this);
        this.info.infoController.addLastCommand(chatId, command);
    }

    private List<KeyboardRow> getKeyboardRaws(String[] buttons){
        var raws = new ArrayList<KeyboardRow>();
        var currentRaw = new KeyboardRow();
        var maxElementsInRaw = 5;
        for (var i = 0; i < buttons.length; i++){
            currentRaw.add(buttons[i]);
            if (currentRaw.size() == maxElementsInRaw || i == buttons.length - 1){
                raws.add(currentRaw);
                currentRaw = new KeyboardRow();
            }
        }
        return raws;
    }
}
