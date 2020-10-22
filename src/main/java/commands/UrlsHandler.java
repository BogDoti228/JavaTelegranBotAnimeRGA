package commands;

import com.google.api.services.drive.model.File;
import googleDrive.GoogleDriveClient;
import objects.GoogleFileContent;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import overseersModule.InfoController;
import tagsConroller.NameCreator;
import tagsConroller.TagsParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum UrlsHandler {
    URlS_HANDLER;

    private List<File> urlsPhoto;
    private List<File> urlsVideo;
    private List<File> urlsGif;

    UrlsHandler()
    {
        try {
            urlsPhoto = GoogleDriveClient.GOOGLE_DRIVE_CLIENT.getAllFilesInFolder(BotConstants.BOT_CONSTANTS.getPHOTO_FOLDER_ID());
            urlsVideo = GoogleDriveClient.GOOGLE_DRIVE_CLIENT.getAllFilesInFolder(BotConstants.BOT_CONSTANTS.getVIDEO_FOLDER_ID());
            urlsGif = GoogleDriveClient.GOOGLE_DRIVE_CLIENT.getAllFilesInFolder(BotConstants.BOT_CONSTANTS.getGIF_FOLDER_ID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputFile getFile(ContentType type, String tagsQuery, Long chatId) throws Exception {
        List<File> files;
        switch (type) {
            case GIF:
                files = urlsGif;
                break;
            case PHOTO:
                files = urlsPhoto;
                break;
            case VIDEO:
                files = urlsVideo;
                break;
            default:
                files = new ArrayList<>();
        }
        String[] necessaryTags;
        if (tagsQuery == null)
            necessaryTags = new String[0];
        else
            necessaryTags = TagsParser.TAGS_PARSER.parseTagsFromInputQuery(tagsQuery);
        ArrayList<File> relevantFiles = new ArrayList<>();
        for (File file: files){
            var fileTags = TagsParser.TAGS_PARSER.parseTagsFromFileName(file.getName());
            if (Arrays.stream(necessaryTags)
                    .allMatch((x) -> Arrays.asList(fileTags).contains(x)))
                relevantFiles.add(file);
        }
        if (relevantFiles.size() == 0)
            return null;
        var randomInt = new Random().nextInt(relevantFiles.size());
        var randomFile = relevantFiles.get(randomInt);
        var googleFileContent = new GoogleFileContent(randomFile, type);
        InfoController.INFO_CONTROLLER.addLastInputFile(chatId, googleFileContent);
        return URlS_HANDLER.getFileContent(googleFileContent.getUrl(), type);
    }

    public void sendFileGoogleDisk(String FolderId, String url, ContentType type, String caption)  {
        try{
            var name = NameCreator.NAME_CREATOR.createNameWithTags(caption);
            InputStream stream;
            if (type == ContentType.GIF)
                stream = new ByteArrayInputStream(GifConverter.GIF_CONVERTER.gifConverter(url));
            else
                stream = new URL(url).openStream();
            var file = GoogleDriveClient.GOOGLE_DRIVE_CLIENT.createGoogleFile(
                                    FolderId,
                                    URlS_HANDLER.getGoogleDriveType(type),
                                    name,
                                    stream
                            );
            switch (type){
                case PHOTO:
                    urlsPhoto.add(file);
                    break;
                case GIF:
                    urlsGif.add(file);
                    break;
                case VIDEO:
                    urlsVideo.add(file);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputFile getFileContent(String url, ContentType type) throws IOException {
        var inputStream = new URL(url).openStream();
        String format = null;
        switch (type){
            case GIF:
                format = "gif";
            case VIDEO:
                format = "mp4";
            case PHOTO:
                format = "jpg";
        }
        var file = new InputFile();
        file.setMedia(inputStream, "KEKW." + format);
        return file;
    }

    private String getGoogleDriveType(ContentType type) throws Exception {
        switch (type){
            case PHOTO:
                return "image/jpg";
            case GIF:
                return "image/gif";
            case VIDEO:
                return "video/mp4";
            default:
                throw new Exception("unknown type");
        }
    }
}
