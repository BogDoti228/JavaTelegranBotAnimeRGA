package commands;

import com.google.api.services.drive.model.File;
import googleDrive.GoogleDriveClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class Urls {
    public static List<File> urlsPhoto;
    public static List<File> urlsVideo;
    public static List<File> urlsGif;

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

    public static InputFile getUrlPhoto() throws IOException {
        var randomInt = new Random().nextInt(urlsPhoto.size());
        var url = urlsPhoto.get(randomInt).getWebContentLink();
        return getFileContent(url);
    }

    public static InputFile getUrlVideo() throws IOException {
        var randomInt = new Random().nextInt(urlsVideo.size());
        var url = urlsVideo.get(randomInt).getWebContentLink();
        return getFileContent(url);
    }
    public static InputFile getUrlGif() throws IOException {
        var randomInt = new Random().nextInt(urlsGif.size());
        var url = urlsGif.get(randomInt).getWebContentLink();
        return getFileContent(url);
    }

    public static void sendFileGoogleDisk(String FolderId, String url, String type, String  name)  {
        try{
            var stream = new URL(url).openStream();
            GoogleDriveClient.createGoogleFile(
                    FolderId,
                    type,
                    name,
                    stream
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendGifGoogleDisk(String FolderId, String url, String type, String  name)  {
        var bytes = GifConverter.gifConverter(url);
        try{
            GoogleDriveClient.createGoogleFile(
                    FolderId,
                    type,
                    name,
                    bytes
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputFile getFileContent(String url) throws IOException {
        var inputStream = new URL(url).openStream();
        var file = new InputFile();
        file.setMedia(inputStream, "KEKW");
        return file;
    }
}
