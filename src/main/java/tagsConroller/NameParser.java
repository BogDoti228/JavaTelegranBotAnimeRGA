package tagsConroller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NameParser {
    public static String[] parseTags(String name) {
        var tagBuilder = new StringBuilder();
        var tags = new ArrayList<String>();
        loop: for (var i = 0; i < name.length(); i++) {
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
                    break loop;
                default:
                    tagBuilder.append(name.charAt(i));
                    break;
            }
        }
        if (tagBuilder.length() != 0)
            tags.add(tagBuilder.toString());
        return tags.stream().distinct().toArray(String[]::new);
    }
}
