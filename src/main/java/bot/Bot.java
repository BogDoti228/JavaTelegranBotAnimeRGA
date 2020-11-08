package bot;

import bot.commands.*;
import bot.commands.sendCommands.SendCommand;
import bot.content.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import bot.overseersModule.InfoController;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final Logger log = Logger.getLogger(Bot.class);

    final int RECONNECT_PAUSE = 10000;

    @Setter
    @Getter
    private String botName;

    @Setter
    private String botToken;

    public Bot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        Long chatId = update.getMessage().getChatId();
        var lastCommand = InfoController.INSTANCE.getLastCommand(chatId);
        if (lastCommand != null && lastCommand.shouldContinue()) {
            lastCommand.continueExecute(update, this);
        } else {
            handleNewCommand(update);
        }
    }

    @Override
    public String getBotUsername() {
        log.debug("Bot.Bot name: " + botName);
        return botName;
    }

    @Override
    public String getBotToken() {
        log.debug("Bot.Bot token: " + botToken);
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Bot.Bot connected and waiting for messages");
        } catch (TelegramApiRequestException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    private void handleNewCommand(Update update) {
        var chatId = update.getMessage().getChatId();
        Command command = CommandParser.INSTANCE.getCommandByUpdate(update);
        command.startExecute(update, this);
        InfoController.INSTANCE.addLastCommand(chatId, command);
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
}

