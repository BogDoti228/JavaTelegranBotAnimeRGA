package tagsConroller;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class InputParser {
    private static final Pattern parsePattern = Pattern.compile("[a-zA-Z0-9а-яА-ЯёЁ_]+");

    public static String[] parseTags(String input){
        var result = new ArrayList<String>();
        var matcher = parsePattern.matcher(input);
        while (matcher.find()){
            result.add(matcher.group());
        }
        return result.toArray(String[]::new);
    }
}
