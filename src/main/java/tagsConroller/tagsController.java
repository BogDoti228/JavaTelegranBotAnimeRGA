package tagsConroller;

import java.util.ArrayList;

public class tagsController {
    public static String[] parseTags(String name) {
        var tagBuilder = new StringBuilder();
        var tags = new ArrayList<String>();
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
                }
                case '.':
                    break;
                default:
                    tagBuilder.append(name.charAt(i));
            }
        }
        if (tagBuilder.length() != 0)
            tags.add(tagBuilder.toString());
        return tags.stream().distinct().toArray(String[]::new);
    }
}
