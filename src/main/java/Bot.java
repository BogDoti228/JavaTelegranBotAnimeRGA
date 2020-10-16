import commands.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import overseersModule.InfoController;
import overseersModule.ModeratorController;
import overseersModule.ReportBuilder;


import java.io.*;
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

            var message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Спасибо за вашу картинку!=)<3");

            InfoController.addLastCommand(chatId, Command.UNKNOWN);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (inputVideo != null){
            var fileId = inputVideo.getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendFileGoogleDisk(BotConstants.VIDEO_FOLDER_ID, url, "video/mp4", "1488.mp4");

            var message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Спасибо за ваше видео!=)<3");

            InfoController.addLastCommand(chatId, Command.UNKNOWN);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (inputGif != null)
        {
            var fileId = inputGif.getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendGifGoogleDisk(BotConstants.GIF_FOLDER_ID, url, "Image/gif", "1337.gif");

            var message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Спасибо за вашу гифку!=)<3");

            InfoController.addLastCommand(chatId, Command.UNKNOWN);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else
        {
            var command = parser.Parse(inputText, chatId);

            var message = new SendMessage();
            message.setChatId(chatId);

            if (command == Command.START) {
                message.setText("Привет всем ползьующимся этим ботом=)\n" +
                        " напиши /photo чтобы получить фото\n " +
                        " напиши /video чтобы получить видео\n " +
                        " напиши /gif чтобы получить гифку\n " +
                        " напиши /report чтобы пожаловаться на файл\n " +
                        " фото, видео, гиф можно кидать боту для пополнения базы данных," +
                        " кидайте все что сочтете достойным для нас," +
                        " всем очень благодарны за использование=)");

                InfoController.addLastCommand(chatId, Command.START);
            }
            else if (command == Command.VIDEO) {
                message.setText("Вот ваше видео, удачного дня");

                var sendTelegramVideo = new SendVideo();
                sendTelegramVideo.setChatId(chatId);
                try {
                    var inputFile = Urls.getInputVideo();
                    InfoController.addLastInputFile(chatId, inputFile);
                    sendTelegramVideo.setVideo(inputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(sendTelegramVideo);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.GIF) {
                message.setText("Вот ваша гифка, удачного дня");


                var sendTelegramAnimation = new SendAnimation();
                sendTelegramAnimation.setChatId(chatId);
                try {
                    var inputFile = Urls.getInputGif();
                    InfoController.addLastInputFile(chatId, inputFile);
                    sendTelegramAnimation.setAnimation(inputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(sendTelegramAnimation);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.PHOTO) {
                message.setText("Вот ваша фотография, удачного дня");

                var sendTelegramPhoto = new SendPhoto();
                sendTelegramPhoto.setChatId(chatId);
                try {
                    var inputFile = Urls.getInputPhoto();
                    InfoController.addLastInputFile(chatId, inputFile);
                    sendTelegramPhoto.setPhoto(inputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    execute(sendTelegramPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.HELP) {
                message.setText("/photo - запросить фото\n " +
                        "/video - запросить видео\n" +
                        " /gif - запросить гифку \n " +
                        " /report - пожаловаться \n " +
                        "можете кидать любую фотку гиф или видео" +
                        " - все сохраним, поплняйте нашу базу " +
                        "- все будут круче и всего будет больше=)");

                InfoController.addLastCommand(chatId, Command.HELP);
            }
            else if (command == Command.REPORT)
            {
                if (InfoController.isExistInputFile(chatId)){
                    message.setText("Укажите причину репорта");
                    ReportBuilder.addReportedInputFile(chatId, InfoController.getLastInputFile(chatId));
                    InfoController.addLastCommand(chatId, Command.REPORT);
                }
                else {
                    message.setText("Для начала получите файл, чтобы была возможность кинуть репорт!");
                    InfoController.addLastCommand(chatId, Command.UNKNOWN);
                }
            }
            else if (command == Command.TEXT_REPORT) {
                message.setText("Спасибо за ваш репорт, модераторы скоро проверят его!=)");
                ReportBuilder.addReportText(chatId, inputText);
                ReportBuilder.createReport(chatId);
                InfoController.addLastCommand(chatId, Command.UNKNOWN);
            }
            else if (command == Command.UNKNOWN)
            {
                message.setText("Нет такой команды");

                InfoController.addLastCommand(chatId, Command.UNKNOWN);
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
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

