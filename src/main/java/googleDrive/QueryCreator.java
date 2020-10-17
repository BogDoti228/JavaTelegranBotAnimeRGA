package googleDrive;

public class QueryCreator {
    public static String createQuery(String name, String mimeType, String parrentId)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("trashed=false ");
        if (name != null)
        {
            builder.append("and name = '").append(name).append("' ");
        }
        if (mimeType != null)
        {
            if (shouldAddAnd(builder))
                builder.append("and ");
            builder.append("mimeType = '").append(mimeType).append("' ");
        }
        if (parrentId != null)
        {
            if (shouldAddAnd(builder))
                builder.append("and ");
            builder.append('\'').append(parrentId).append("' in parents");
        }
        else
        {
            if (shouldAddAnd(builder))
                builder.append("and ");
            builder.append('\'').append("root").append("' in parents");
        }
        return builder.toString();
    }

    private static boolean shouldAddAnd(StringBuilder current)
    {
        return current.length() != 0;
    }
}
