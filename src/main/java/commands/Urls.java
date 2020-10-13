package commands;

import com.google.api.services.drive.model.File;
import googleDrive.GoogleDriveClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Urls {
    public static ArrayList<String> downloadUrls = new ArrayList<>();
    public static List<File> urlsPhoto;
    public static List<File> urlsVideo;
    public static List<File> urlsGif;

    static {
        try {
            urlsPhoto = GoogleDriveClient.getAllFilesInFolder(BotConstants.GOOGLE_DRIVE_FOLDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUrlPhoto() {
        var randomInt = new Random().nextInt(urlsPhoto.size());
        return urlsPhoto.get(randomInt).getWebContentLink() + "?usp=sharing";
    }

    public static String getUrlVideo() {
        var randomInt = new Random().nextInt(urlsVideo.size());
        return urlsVideo.get(randomInt).getWebContentLink() + "?usp=sharing";
    }
    public static String getUrlGif() {
        var randomInt = new Random().nextInt(urlsGif.size());
        return urlsGif.get(randomInt).getWebContentLink() + "?usp=sharing";
    }

    public static void sendPhotoGoogleDisk(InputStream uploadData, String type) throws IOException {
        GoogleDriveClient.createGoogleFile(
                BotConstants.GOOGLE_DRIVE_FOLDER_ID,
                "Image/" + type,
                "123." + type,
                uploadData
        );
    }
}
