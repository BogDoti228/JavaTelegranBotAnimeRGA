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
import overseersModule.ModeratorController;
import overseersModule.ReportBuilder;
import overseersModule.ReportController;


import java.text.ParseException;
import java.util.Comparator;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final Logger log = Logger.getLogger(Bot.class);
    private final String helpText =
            "/photo - запросить фото\n" +
            "/video  - запросить видео\n" +
            "/gif - запросить гифку \n" +
            "/report - пожаловаться \n" +
            "можете кидать любую фотку гиф или видео" +
            " - все сохраним, поплняйте нашу базу " +
            "- все будут круче и всего будет больше=)";

    private final String startText = "Привет всем ползьующимся этим ботом=)\n" +
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
    public void executeReport(Object inputObject){
        if (inputObject instanceof SendPhoto) {
            try {
                execute((SendPhoto)inputObject);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (inputObject instanceof SendVideo) {
            try {
                execute((SendVideo)inputObject);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (inputObject instanceof SendAnimation) {
            try {
                execute((SendAnimation)inputObject);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        System.out.println(update.getMessage().getChatId());
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        var inputPhoto = update.getMessage().getPhoto();
        var inputVideo = update.getMessage().getVideo();
        var inputGif = update.getMessage().getAnimation();
        var inputCaption = update.getMessage().getCaption();
        if (ModeratorController.MODERATOR_CONTROLLER.isUserModerator(chatId)
                && ModeratorController.MODERATOR_CONTROLLER.isModeratorInCheckMode(chatId)){
            if (inputPhoto != null || inputVideo != null || inputGif != null){
                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Зачем вы это кидаете в режиме проверки репортов?\n Используйте только /next, /delete, /close!!!");

                InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                var parsedInputText = Parser.PARSER.Parse(inputText, chatId);
                var command = parsedInputText.getValue0();

                var message = new SendMessage();
                message.setChatId(chatId);

                if (command == Command.DELETE){
                    if (ReportBuilder.REPORT_BUILDER.isNoReports()) {
                        message.setText("Нечего удалять, репорты уже закончены");
                    }
                    else {
                        if(ReportBuilder.REPORT_BUILDER.isReportAlreadyDeleted(chatId)){
                            message.setText("Фотка уже удалена");
                        }
                        else {
                            ReportController.REPORT_CONTROLLER.deleteReportedFile(chatId);
                            message.setText("Вы успешно удалили фотку!=)");
                        }
                    }
                }
                else if (command == Command.NEXT){
                    if (ReportBuilder.REPORT_BUILDER.isNoReports()) {
                        message.setText("Репортов больше нет, возвращайтесь потом");
                    }
                    else{
                        var inputObject = ReportController.REPORT_CONTROLLER.initializeReport(chatId);
                        message.setText("Ваш репорт");
                        executeReport(inputObject);
                    }
                }
                else if (command == Command.CLOSE){
                    ModeratorController.MODERATOR_CONTROLLER.closeCheckMode(chatId);
                    message.setText("Вы вышли из режима проверки репортов, спасибо за проделанную работу!=)");
                }
                else{
                    message.setText("Вы можете использовать только /next, /delete, /close!!!");
                }

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            if (inputPhoto != null && !inputPhoto.isEmpty()) {
                var fileId = inputPhoto.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null).getFileId();
                var url = FilePath.FILE_PATH.getDownloadUrl(fileId, botToken);
                UrlsHandler.URlS_HANDLER.sendFileGoogleDisk(BotConstants.BOT_CONSTANTS.getPHOTO_FOLDER_ID(), url, ContentType.PHOTO, inputCaption);

                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Спасибо за вашу картинку!=)<3");

                InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (inputVideo != null) {
                var fileId = inputVideo.getFileId();
                var url = FilePath.FILE_PATH.getDownloadUrl(fileId, botToken);
                UrlsHandler.URlS_HANDLER.sendFileGoogleDisk(BotConstants.BOT_CONSTANTS.getVIDEO_FOLDER_ID(), url, ContentType.VIDEO, inputCaption);

                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Спасибо за ваше видео!=)<3");

                InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (inputGif != null) {
                var fileId = inputGif.getFileId();
                var url = FilePath.FILE_PATH.getDownloadUrl(fileId, botToken);
                UrlsHandler.URlS_HANDLER.sendFileGoogleDisk(BotConstants.BOT_CONSTANTS.getGIF_FOLDER_ID(), url, ContentType.GIF, inputCaption);

                var message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Спасибо за вашу гифку!=)<3");

                InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                var parsedInputText = Parser.PARSER.Parse(inputText, chatId);
                var command = parsedInputText.getValue0();
                var commandParameters = parsedInputText.getValue1();

                var message = new SendMessage();
                message.setChatId(chatId);

                if (command == Command.START) {
                    message.setText(startText);

                    InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.START);
                } else if (command == Command.VIDEO) {
                    var sendTelegramVideo = new SendVideo();
                    sendTelegramVideo.setChatId(chatId);
                    try {
                        var inputFile = UrlsHandler.URlS_HANDLER.getFile(ContentType.VIDEO, commandParameters, chatId);
                        if (inputFile != null) {
                            sendTelegramVideo.setVideo(inputFile);
                            execute(sendTelegramVideo);
                            message.setText("Вот ваше видео, удачного дня");
                        } else {
                            message.setText("К сожалению, подобный контент не был найден");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (command == Command.GIF) {
                    var sendTelegramAnimation = new SendAnimation();
                    sendTelegramAnimation.setChatId(chatId);
                    try {
                        var inputFile = UrlsHandler.URlS_HANDLER.getFile(ContentType.GIF, commandParameters, chatId);
                        if (inputFile != null) {
                            sendTelegramAnimation.setAnimation(inputFile);
                            execute(sendTelegramAnimation);
                            message.setText("Вот ваша гифка, удачного дня");
                        } else {
                            message.setText("К сожалению, подобный контент не был найден");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (command == Command.PHOTO) {
                    var sendTelegramPhoto = new SendPhoto();
                    sendTelegramPhoto.setChatId(chatId);
                    try {
                        var inputFile = UrlsHandler.URlS_HANDLER.getFile(ContentType.PHOTO, commandParameters, chatId);
                        if (inputFile != null) {
                            sendTelegramPhoto.setPhoto(inputFile);
                            execute(sendTelegramPhoto);
                            message.setText("Вот ваша фотография, удачного дня");
                        } else {
                            message.setText("К сожалению, подобный контент не был найден");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (command == Command.HELP) {
                    message.setText(helpText);
                    InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.HELP);
                } else if (command == Command.REPORT) {
                    if (InfoController.INFO_CONTROLLER.isExistInputFile(chatId)) {
                        message.setText("Укажите причину репорта");
                        ReportBuilder.REPORT_BUILDER.addReportedInputFile(chatId, InfoController.INFO_CONTROLLER.getLastInputFile(chatId));
                        InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.REPORT);
                    } else {
                        message.setText("Для начала получите файл, чтобы была возможность кинуть репорт!");
                        InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                    }
                } else if (command == Command.CHECK_REPORTS) {
                    if (ModeratorController.MODERATOR_CONTROLLER.isUserModerator(chatId)) {
                        if (ReportBuilder.REPORT_BUILDER.isNoReports()) {
                            message.setText("Репортов пока что нет=(\n (вы не в режиме модерации)");
                            InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                        }
                        else {
                            message.setText("Вот репорты, пожалуйста, исполняйте свои обязанности нормально!");
                            var inputObject = ReportController.REPORT_CONTROLLER.initializeReport(chatId);
                            executeReport(inputObject);
                            ModeratorController.MODERATOR_CONTROLLER.openCheckMode(chatId);
                            InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.CHECK_REPORTS);
                        }
                    } else {
                        message.setText("Вы не имеете доступа к данной команде!");
                        InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                    }
                } else if (command == Command.TEXT_REPORT) {
                    message.setText("Спасибо за ваш репорт, модераторы скоро проверят его!=)");
                    ReportBuilder.REPORT_BUILDER.addReportText(chatId, inputText);
                    ReportBuilder.REPORT_BUILDER.createReport(chatId);

                    InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                } else if (command == Command.UNKNOWN) {
                    message.setText("Нет такой команды");
                    InfoController.INFO_CONTROLLER.addLastCommand(chatId, Command.UNKNOWN);
                } else if (command == Command.NEXT || command == Command.DELETE || command == Command.CLOSE ){
                    message.setText("Такая команда недоступна в обычном режиме");
                } else if (command == Command.SUDO){
                    if (ModeratorController.MODERATOR_CONTROLLER.isUserModerator(chatId)) {
                        message.setText("Вы уже являетесь модератором");
                    }
                    else if (ModeratorController.MODERATOR_CONTROLLER.passwordIsCorrect(commandParameters)){
                            ModeratorController.MODERATOR_CONTROLLER.addModerator(chatId);
                            message.setText("Поздравляем, теперь вы модератор");
                    }
                    else{
                        message.setText("Неправильный пароль");
                    }
                } else if (command == Command.DESUDO){
                    try{
                        Long moderatorId = Long.parseLong(commandParameters);
                        if (!ModeratorController.MODERATOR_CONTROLLER.isOwner(chatId)){
                            message.setText("У вас нет таких прав");
                        }
                        else if (!ModeratorController.MODERATOR_CONTROLLER.isUserModerator(moderatorId)) {
                            message.setText("Данный человек не модератор");
                        }
                        else{
                            ModeratorController.MODERATOR_CONTROLLER.demoteModerator(moderatorId);
                            message.setText("Модератор лишён своих привелегий");
                        }
                    }
                    catch (NumberFormatException e){
                        message.setText("id модератора должно быть числом");
                    }
                }

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

