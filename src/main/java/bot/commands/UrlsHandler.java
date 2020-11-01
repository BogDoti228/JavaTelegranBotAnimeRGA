package bot.commands;

import bot.BotConstants;
import bot.content.*;
import googleDrive.GoogleDriveClient;
import bot.tagsConroller.TagsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum UrlsHandler {
    INSTANCE;

    private List<PhotoFile> urlsPhoto;
    private List<VideoFile> urlsVideo;
    private List<GifFile> urlsGif;

    UrlsHandler()
    {
        try {
            var photos = GoogleDriveClient.INSTANCE.getAllFilesInFolder(
                    BotConstants.INSTANCE.getPHOTO_FOLDER_ID()
            );
            var videos = GoogleDriveClient.INSTANCE.getAllFilesInFolder(
                    BotConstants.INSTANCE.getVIDEO_FOLDER_ID()
            );
            var gifs = GoogleDriveClient.INSTANCE.getAllFilesInFolder(
                    BotConstants.INSTANCE.getGIF_FOLDER_ID()
            );
            urlsPhoto = photos.stream().map(PhotoFile::new).collect(Collectors.toList());
            urlsVideo = videos.stream().map(VideoFile::new).collect(Collectors.toList());
            urlsGif = gifs.stream().map(GifFile::new).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GoogleContentFile getFile(ContentType type, String tagsQuery) {
        List<GoogleContentFile> files;
        switch (type) {
            case GIF:
                files = new ArrayList<>(urlsGif);
                break;
            case PHOTO:
                files = new ArrayList<>(urlsPhoto);
                break;
            case VIDEO:
                files = new ArrayList<>(urlsVideo);
                break;
            default:
                files = new ArrayList<>();
        }
        String[] necessaryTags;
        if (tagsQuery == null)
            necessaryTags = new String[0];
        else
            necessaryTags = TagsParser.INSTANCE.parseTagsFromInputQuery(tagsQuery);
        ArrayList<GoogleContentFile> relevantFiles = new ArrayList<>();
        for (GoogleContentFile file: files){
            var fileTags = TagsParser.INSTANCE.parseTagsFromFileName(file.getName());
            if (Arrays.stream(necessaryTags).allMatch((x) -> Arrays.asList(fileTags).contains(x))) {
                relevantFiles.add(file);
            }
        }
        if (relevantFiles.size() == 0)
            return null;
        var randomInt = new Random().nextInt(relevantFiles.size());
        return relevantFiles.get(randomInt);
    }

    public void addNewFile(GoogleContentFile file){
        //Вообще без понятия как это пофиксить
        switch (file.getContentType()){
            case GIF:
                urlsGif.add((GifFile) file);
                break;
            case PHOTO:
                urlsPhoto.add((PhotoFile) file);
                break;
            case VIDEO:
                urlsVideo.add((VideoFile) file);
                break;
        }
    }
}
