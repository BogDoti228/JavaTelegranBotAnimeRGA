package bot.content;

import com.google.api.services.drive.model.File;
import org.apache.http.impl.io.EmptyInputStream;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class GifFile extends GoogleContentFile {
    public GifFile(File googleFile) {
        super(googleFile);
        this.contentType = ContentType.GIF;
    }

    @Override
    public InputFile getTelegramInputFile() {
        var file = new InputFile();
        var name = "гифка.gif";
        file.setMedia(openStream(), name);
        return file;
    }

    @Override
    public InputStream openStream() {
        try {
            return new URL(getUrl()).openStream();
        } catch (Exception e){
            e.printStackTrace();
            return EmptyInputStream.INSTANCE;
        }
    }
}
