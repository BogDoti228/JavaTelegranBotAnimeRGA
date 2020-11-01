package bot.tagsConroller;

import java.util.ArrayList;
import java.util.regex.Pattern;

public enum TagsParser {
    INSTANCE;
    private final Pattern inputParsePattern = Pattern.compile("[a-zA-Z0-9а-яА-ЯёЁ_]+");

    public String[] parseTagsFromFileName(String name) {
        name = name.toLowerCase();
        var tagBuilder = new StringBuilder();
        var tags = new ArrayList<String>();
        loop1:
        for (var i = 0; i < name.length(); i++) {
            switch (name.charAt(i)) {
                case '_': {
                    if (name.charAt(i + 1) == '_') {
                        tagBuilder.append('_');
                        i++;
                    } else {
                        tags.add(tagBuilder.toString());
                        tagBuilder.setLength(0);
                    }
                    break;
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

    public String[] parseTagsFromInputQuery(String input) {
        var result = new ArrayList<String>();
        var matcher = inputParsePattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group().toLowerCase());
        }
        return result.toArray(String[]::new);
    }
}
