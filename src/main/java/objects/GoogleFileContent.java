package objects;

import com.google.api.services.drive.model.File;
import commands.ContentType;

public class GoogleFileContent {
    private final File googleFile;
    private final ContentType contentType;

    public GoogleFileContent(File googleFile, ContentType contentType){
        this.googleFile = googleFile;
        this.contentType = contentType;
    }

    public String getId(){
        return googleFile.getId();
    }

    public String getUrl(){
        return googleFile.getWebContentLink();
    }

    public ContentType getContentType(){
        return contentType;
    }
}
