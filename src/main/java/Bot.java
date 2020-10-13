import commands.*;
import googleDrive.GoogleDriveClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import it.grabz.grabzit.*;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Comparator;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);
    private static  Parser parser = new Parser();
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
        var inputVideo = update.getMessage().getVideo();
        var inputGif = update.getMessage().getAnimation();

        if (inputPhoto != null && !inputPhoto.isEmpty())
        {
            var fileId = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendFileGoogleDisk(BotConstants.PHOTO_FOLDER_ID, url, "Image/jpg", "228.jpg");
        }
        else if (inputVideo != null){
            var fileId = inputVideo.getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendFileGoogleDisk(BotConstants.VIDEO_FOLDER_ID, url, "video/mp4", "1488.mp4");
        }
        else if (inputGif != null)
        {
            var fileId = inputGif.getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendGifGoogleDisk(BotConstants.GIF_FOLDER_ID, url, "video/gif", "1337.gif");
        }
        else
        {
            var command = parser.Parse(inputText);

            if (command == Command.START) {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Привет всем ползьующимся этим ботом=)\n+" +
                        " напиши /photo чтобы получить фото\n " +
                        " напиши /video чтобы получить видео\n " +
                        " напиши /gif чтобы получить гифку\n " +
                        " фото, видео, гиф можно кидать боту для пополнения базы данных," +
                        " кидайте все что сочтете достойным для нас," +
                        " всем очень благодарны за использование=)");

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.VIDEO) {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вот ваше видео, удачного дня");

                var sendTelegramVideo = new SendVideo();
                sendTelegramVideo.setChatId(chatId);
                try {
                    sendTelegramVideo.setVideo(Urls.getUrlVideo());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(message);
                    execute(sendTelegramVideo);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.GIF) {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вот ваша гифка, удачного дня");


                var sendTelegramAnimation = new SendAnimation();
                sendTelegramAnimation.setChatId(chatId);
                try {
                    sendTelegramAnimation.setAnimation(Urls.getUrlGif());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(message);
                    execute(sendTelegramAnimation);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.PHOTO) {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вот ваша фотография, удачного дня");

                var sendTelegramPhoto = new SendPhoto();
                sendTelegramPhoto.setChatId(chatId);
                try {
                    sendTelegramPhoto.setPhoto(Urls.getUrlPhoto());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(message);
                    execute(sendTelegramPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.HELP) {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("/photo - запросить фото\n /video - запросить видео\n" +
                        " /gif - запросить гифку \n " +
                        "можете кидать любую фотку гиф или видео" +
                        " - все сохраним, поплняйте нашу базу " +
                        "- все будут круче и всего будет больше=)");

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.UNKNOWN)
            {
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Нет такой команды");

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

