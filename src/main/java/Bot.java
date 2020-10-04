import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);
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
        String inputText = update.getMessage().getText();
        var inputPhoto = update.getMessage().getPhoto();
        if (inputPhoto != null && !inputPhoto.isEmpty())
        {
            var f_id = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            //Тут ТИПА НУЖНО СКАЧАТЬ ФАЙЛ НА КОМП КАК ТО Я ТОЛЬКО СМОГ ДОБЫТЬ ФАЙЛ АЙДИ НО НЕ ПОНИМАЮ ЧО С НИМ ДЕЛАТЬ
            var getFile = new GetFile().setFileId(f_id);


        }
        else
        {
            if (inputText.startsWith("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Привет напиши /photo чтобы получить фото, " +
                        "или напишите /sendPhoto чтобы отправить нам картинку");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if (inputText.startsWith("/photo")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вот ваша фотография, удачного дня");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                var sendTelegramPhoto = new SendPhoto();
                sendTelegramPhoto.setChatId(chatId);
                sendTelegramPhoto.setPhoto("https://images.alphacoders.com/727/72743.jpg");
                try {
                    sendPhoto(sendTelegramPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if (inputText.startsWith("/sendPhoto")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Пожалуйста ниже отправте фотографию");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                /*var getTelegramPhoto = new SendPhoto();
                getTelegramPhoto.setChatId(chatId);
                var photo = getTelegramPhoto.getPhoto();
                try {
                    sendPhoto(getTelegramPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                message.setText(photo);*/
            }
        }
    }

    @Override
    public String getBotUsername() {
        log.debug("Bot name: " + botName);
        return botName;
    }

    @Override
    public String getBotToken() {
        log.debug("Bot token: " + botToken);
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Bot connected and waiting for messages");
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
}

