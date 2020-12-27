package bot.content;

import com.google.api.services.drive.model.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.io.Serializable;

public abstract class GoogleContentFile implements Serializable {
    protected ContentType contentType;

    protected String name;
    protected String id;
    protected String url;

    public GoogleContentFile(File googleFile){
        this.name = googleFile.getName();
        this.id = googleFile.getId();
        this.url = googleFile.getWebContentLink();
    }

    public String getId(){
        return this.id;
    }

    public String getUrl(){
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public abstract InputFile getTelegramInputFile();

    public ContentType getContentType(){
        return contentType;
    }

    public abstract InputStream openStream();

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        var objAsFile = (GoogleContentFile) obj;
        return this.url.equals(objAsFile.url);
    }
}
