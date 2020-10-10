package googleDrive;

public class QueryCreator {
    public static String createQuery(String name, String mimeType, String parrentId)
    {
        StringBuilder builder = new StringBuilder();
        if (name != null)
        {
            builder.append("name = '").append(name).append("' ");
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
        var query = builder.toString();
        System.out.println(query);
        return query;
    }

    private static boolean shouldAddAnd(StringBuilder current)
    {
        return current.length() != 0;
    }
}
