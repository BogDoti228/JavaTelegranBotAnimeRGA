package tagsConroller;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class TagsParser {
    private static final Pattern inputParsePattern = Pattern.compile("[a-zA-Z0-9а-яА-ЯёЁ_]+");

    public static String[] parseTagsFromFileName(String name) {
        var tagBuilder = new StringBuilder();
        var tags = new ArrayList<String>();
        loop1 :for (var i = 0; i < name.length(); i++) {
            switch (name.charAt(i)) {
                case '_': {
                    if (name.charAt(i + 1) == '_') {
                        tagBuilder.append('_');
                        i++;
                    } else {
                        tags.add(tagBuilder.toString());
                        tagBuilder.setLength(0);
                    }
                }
                case '.':
                    break loop1;
                default:
                    tagBuilder.append(name.charAt(i));
            }
        }
        if (tagBuilder.length() != 0)
            tags.add(tagBuilder.toString());
        return tags.stream().distinct().toArray(String[]::new);
    }

    public static String[] parseTagsFromInputQuery(String input) {
        var result = new ArrayList<String>();
        var matcher = inputParsePattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result.toArray(String[]::new);
    }
}
