package commands;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import overseersModule.InfoController;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);
    private static final Map<Command, String> dict = new HashMap<>(){{
        put(Command.START, "/start");
        put(Command.HELP, "/help");
        put(Command.PHOTO, "/photo");
        put(Command.GIF, "/gif");
        put(Command.VIDEO, "/video");
        put(Command.REPORT, "/report");
    }};

    public static Pair<Command, String> Parse(String input, Long chatId) {
        Pair<Command, String> result = new Pair<>(null, null);
        if (InfoController.isLastCommandReport(chatId)){
            return new Pair<>(Command.TEXT_REPORT, "");
        }
        for (var command: dict.keySet()){
            if (input.startsWith(dict.get(command))) {
                var parameters = input.substring(dict.get(command).length());
                return new Pair<>(command, parameters);
            }
        }
        return new Pair<>(Command.UNKNOWN, input);
    }
}