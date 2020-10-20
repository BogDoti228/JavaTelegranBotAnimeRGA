package commands;

import com.google.api.services.drive.model.File;
import googleDrive.GoogleDriveClient;
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

public class Urls {
    private static List<File> urlsPhoto;
    private static List<File> urlsVideo;
    private static List<File> urlsGif;

    static {
        try {
            urlsPhoto = GoogleDriveClient.getAllFilesInFolder(BotConstants.PHOTO_FOLDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            urlsVideo = GoogleDriveClient.getAllFilesInFolder(BotConstants.VIDEO_FOLDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            urlsGif = GoogleDriveClient.getAllFilesInFolder(BotConstants.GIF_FOLDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InputFile getFile(ContentType type, String tagsQuery, Long chatId) throws Exception {
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
            necessaryTags = TagsParser.parseTagsFromInputQuery(tagsQuery);
        ArrayList<File> relevantFiles = new ArrayList<>();
        for (File file: files){
            var fileTags = TagsParser.parseTagsFromFileName(file.getName());
            if (Arrays.stream(necessaryTags)
                    .allMatch((x) -> Arrays.asList(fileTags).contains(x)))
                relevantFiles.add(file);
        }
        if (relevantFiles.size() == 0)
            return null;
        var randomInt = new Random().nextInt(relevantFiles.size());
        var randomFile = relevantFiles.get(randomInt);
        var url = randomFile.getWebContentLink();
        InfoController.addLastInputFile(chatId, url, type);
        return getFileContent(randomFile.getWebContentLink(), type);
    }


    public static void sendFileGoogleDisk(String FolderId, String url, ContentType type, String caption)  {
        try{
            var name = NameCreator.createNameWithTags(caption);
            InputStream stream;
            if (type == ContentType.GIF)
                stream = new ByteArrayInputStream(GifConverter.gifConverter(url));
            else
                stream = new URL(url).openStream();
            var file = GoogleDriveClient.createGoogleFile(
                                    FolderId,
                                    getGoogleDriveType(type),
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

    public static InputFile getFileContent(String url, ContentType type) throws IOException {
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

    private static String getGoogleDriveType(ContentType type) throws Exception {
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
