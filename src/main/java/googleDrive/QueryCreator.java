package googleDrive;

import java.io.Serializable;
import java.util.Objects;

public enum QueryCreator implements Serializable {
    QUERY_CREATOR;

    public String createQuery(String name, String mimeType, String parrentId)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("trashed=false ");
        if (name != null)
        {
            builder.append("and name = '").append(name).append("' ");
        }
        if (mimeType != null)
        {
            builder.append("and mimeType = '").append(mimeType).append("' ");
        }
        builder.append("and '").append(Objects.requireNonNullElse(parrentId, "root")).append("' in parents");
        return builder.toString();
    }
}
