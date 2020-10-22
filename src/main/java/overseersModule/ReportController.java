package overseersModule;

import commands.ContentType;
import commands.UrlsHandler;
import googleDrive.GoogleDriveClient;
import objects.GoogleFileContent;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;

public enum ReportController {
    REPORT_CONTROLLER;
    public void deleteReportedFile(Long chatId){
        var report = ReportBuilder.REPORT_BUILDER.getReportForDelete(chatId);
        var googleFileContent = report.getValue0();
        GoogleDriveClient.GOOGLE_DRIVE_CLIENT.deleteFile(googleFileContent.getId());
    }

    public Object initializeReport(Long chatId) {
        var report = ReportBuilder.REPORT_BUILDER.getReport(chatId);
        return initializeReport(report, chatId);
    }

    private Object initializeReport(Pair<GoogleFileContent, String> report, Long chatId){
        var googleFileContent = report.getValue0();
        var url = googleFileContent.getUrl();
        var type = googleFileContent.getContentType();
        var textReport = report.getValue1();
        InputFile inputFile = null;
        try {
            inputFile = UrlsHandler.URlS_HANDLER.getFileContent(url, type);
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
