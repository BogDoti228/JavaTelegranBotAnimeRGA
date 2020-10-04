import commands.Urls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static String executePost(String targetURL) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());

        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();
        var inputPhoto = update.getMessage().getPhoto();
        if (inputPhoto != null && !inputPhoto.isEmpty())
        {
            var fileId = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            var request = "https://api.telegram.org/bot"+botToken+"/getFile?file_id=" + fileId;
            var something = executePost(request);
            assert something != null;
            var arrayInfo = something.split(":");
            var strangePath = arrayInfo[arrayInfo.length-1];
            var pathFile = new StringBuilder();
            for (var i = 1; i < strangePath.length() - 3; i++) {
                pathFile.append(strangePath.charAt(i));
            }
            var url = File.getFileUrl(botToken, pathFile.toString());
            //при добавлении этой ссылки у нас будет ошибка потому что она сразу скачивает файл и там просто пустая страница
            //но я думаю это хорошо, просто устал думать
            Urls.urls.add(url);
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
                sendTelegramPhoto.setPhoto(Urls.getUrl());
                try {
                    execute(sendTelegramPhoto);
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

