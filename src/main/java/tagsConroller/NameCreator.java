package tagsConroller;

import java.util.*;
import java.util.regex.Pattern;

public class NameCreator {
    public static final String defaultName = "defaultName";

    //inputExamples
    //hello world -> hello_world
    //#hello,  world!... -> hello_world (ignore non letters)
    //he11o w0r1d -> he11o_w0r1d (not ignore numbers)
    //world hello -> hello_world (lexis order)
    //hello wor_ld -> hello_wor__ld (doubling underlines)
    //_hello_ -> error (not accept underlines at the beginning or at the end of tag)
    //hello hello world -> hello_world
    public static String createNameWithTags(String inputQuery) {
        if (inputQuery == null)
            return defaultName;
        var tags = TagsParser.parseTagsFromInputQuery(inputQuery);
        if (tags.length == 0)
            return defaultName;
        Arrays.sort(tags);
        if (Arrays
                .stream(tags)
                .anyMatch((x) -> x.charAt(0) == '_' || x.charAt(x.length() - 1) == '_'))
            return defaultName;
        var builder = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            for (var symbol : tags[i].toCharArray()) {
                if (symbol == '_')
                    builder.append("__");
                else
                    builder.append(symbol);
            }
            if (i + 1 < tags.length)
                builder.append("_");
        }
        return builder.toString();
    }
}
