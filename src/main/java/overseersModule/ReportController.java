package overseersModule;

import commands.ContentType;
import commands.Urls;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

public class ReportController {
    public static Object initializeProgressionRelativeReport(Long chatId){
        var report = ReportBuilder.getProgressionRelativeReport(chatId);
        return initializeReport(report, chatId);
    }
    public static Object initializeNextReport(Long chatId){
        var report = ReportBuilder.getNextReport(chatId);
        return initializeReport(report, chatId);
    }

    private static Object initializeReport(Pair<Pair<String, ContentType>, String> report, Long chatId){
        var url = report.getValue0().getValue0();
        var type = report.getValue0().getValue1();
        var textReport = report.getValue1();
        InputFile inputFile = null;
        try {
            inputFile = Urls.getFileContent(url, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type == ContentType.PHOTO){
            var sendTelegramPhoto = new SendPhoto();
            sendTelegramPhoto.setChatId(chatId);
            try {
                sendTelegramPhoto.setPhoto(inputFile);
                sendTelegramPhoto.setCaption(textReport);
                return sendTelegramPhoto;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(type == ContentType.GIF){
            var sendTelegramGif = new SendAnimation();
            sendTelegramGif.setChatId(chatId);
            try {
                sendTelegramGif.setAnimation(inputFile);
                sendTelegramGif.setCaption(textReport);
                return sendTelegramGif;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(type == ContentType.VIDEO){
            var sendTelegramVideo = new SendVideo();
            sendTelegramVideo.setChatId(chatId);
            try {
                sendTelegramVideo.setVideo(inputFile);
                sendTelegramVideo.setCaption(textReport);
                return sendTelegramVideo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
