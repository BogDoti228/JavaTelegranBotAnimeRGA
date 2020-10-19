import commands.BotConstants;
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
import overseersModule.ReportBuilder;


import java.io.*;
import java.util.Comparator;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);
    private static final String helpText =
            "/photo - запросить фото\n" +
            "/video  - запросить видео\n" +
            "/gif - запросить гифку \n" +
            "/report - пожаловаться \n" +
            "можете кидать любую фотку гиф или видео" +
            " - все сохраним, поплняйте нашу базу " +
            "- все будут круче и всего будет больше=)";

    private static final String startText = "Привет всем ползьующимся этим ботом=)\n" +
            " напиши /photo чтобы получить фото\n " +
            " напиши /photo tag1 tag2 чтобы получить фото с тегами tag1 и tag2\n " +
            " напиши /photo чтобы получить фото\n " +
            " напиши /video чтобы получить видео\n " +
            " напиши /gif чтобы получить гифку\n " +
            " напиши /report чтобы пожаловаться на файл\n " +
            " фото, видео, гиф можно кидать боту для пополнения базы данных," +
            " кидайте все что сочтете достойным для нас," +
            " всем очень благодарны за использование=)";

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
        System.out.println("Я въезжаю в стройку");
        log.debug("Receive new Update. updateID: " + update.getUpdateId());

        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        var inputPhoto = update.getMessage().getPhoto();
        var inputVideo = update.getMessage().getVideo();
        var inputGif = update.getMessage().getAnimation();
        var inputCaption = update.getMessage().getCaption();

        if (inputPhoto != null && !inputPhoto.isEmpty())
        {
            var fileId = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null).getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendFileGoogleDisk(BotConstants.PHOTO_FOLDER_ID, url, ContentType.PHOTO, inputCaption);

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
            Urls.sendFileGoogleDisk(BotConstants.VIDEO_FOLDER_ID, url, ContentType.VIDEO, inputCaption);

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
        else if (inputGif != null){
            var fileId = inputGif.getFileId();
            var url = FilePath.getDownloadUrl(fileId, botToken);
            Urls.sendFileGoogleDisk(BotConstants.GIF_FOLDER_ID, url, ContentType.GIF, inputCaption);

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
            var parsedInputText = Parser.Parse(inputText, chatId);
            var command = parsedInputText.getValue0();
            var commandParameters = parsedInputText.getValue1();

            var message = new SendMessage();
            message.setChatId(chatId);

            if (command == Command.START) {
                message.setText(startText);

                InfoController.addLastCommand(chatId, Command.START);
            }
            else if (command == Command.VIDEO) {
                var sendTelegramVideo = new SendVideo();
                sendTelegramVideo.setChatId(chatId);
                try {
                    var inputFile = Urls.getFile(ContentType.VIDEO, commandParameters);
                    if (inputFile != null) {
                        InfoController.addLastInputFile(chatId, inputFile);
                        sendTelegramVideo.setVideo(inputFile);
                        execute(sendTelegramVideo);
                        message.setText("Вот ваше видео, удачного дня");
                    } else {
                        message.setText("К сожалению, подобный контент не был найден");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.GIF) {
                var sendTelegramAnimation = new SendAnimation();
                sendTelegramAnimation.setChatId(chatId);
                try {
                    var inputFile = Urls.getFile(ContentType.GIF, commandParameters);
                    if (inputFile != null) {
                        InfoController.addLastInputFile(chatId, inputFile);
                        sendTelegramAnimation.setAnimation(inputFile);
                        execute(sendTelegramAnimation);
                        message.setText("Вот ваша гифка, удачного дня");
                    } else {
                        message.setText("К сожалению, подобный контент не был найден");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.PHOTO) {
                var sendTelegramPhoto = new SendPhoto();
                sendTelegramPhoto.setChatId(chatId);
                try {
                    var inputFile = Urls.getFile(ContentType.PHOTO, commandParameters);
                    if (inputFile != null) {
                        InfoController.addLastInputFile(chatId, inputFile);
                        sendTelegramPhoto.setPhoto(inputFile);
                        execute(sendTelegramPhoto);
                        message.setText("Вот ваша фотография, удачного дня");
                    } else {
                        message.setText("К сожалению, подобный контент не был найден");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (command == Command.HELP) {
                message.setText(helpText);
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

