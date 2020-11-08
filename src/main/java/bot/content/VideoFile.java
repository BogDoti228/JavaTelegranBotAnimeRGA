package bot.content;

import com.google.api.services.drive.model.File;
import org.apache.http.impl.io.EmptyInputStream;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.net.URL;

public class VideoFile extends GoogleContentFile {
    public VideoFile(File googleFile) {
        super(googleFile);
        this.contentType = ContentType.VIDEO;
    }

    @Override
    public InputFile getTelegramInputFile() {
        var name = "видео.mp4";
        var file = new InputFile();
        file.setMedia(openStream(), name);
        return file;
    }

    @Override
    public InputStream openStream() {
        try{
            return new URL(getUrl()).openStream();
        } catch (Exception e){
            e.printStackTrace();
            return EmptyInputStream.INSTANCE;
        }
    }
}
