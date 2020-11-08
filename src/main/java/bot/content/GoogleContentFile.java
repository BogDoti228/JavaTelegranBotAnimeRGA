package bot.content;

import com.google.api.services.drive.model.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

public abstract class GoogleContentFile {
    protected final File googleFile;
    protected ContentType contentType;

    public GoogleContentFile(File googleFile){
        this.googleFile = googleFile;
    }

    public String getId(){
        return googleFile.getId();
    }

    public String getUrl(){
        return googleFile.getWebContentLink();
    }

    public String getName() {
        return googleFile.getName();
    }

    public abstract InputFile getTelegramInputFile();

    public ContentType getContentType(){
        return contentType;
    }

    public abstract InputStream openStream();
}
