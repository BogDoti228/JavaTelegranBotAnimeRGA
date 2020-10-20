package commands;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import overseersModule.InfoController;

import java.util.HashMap;
import java.util.Map;

public class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);
    private static final Map<Command, String> commandsDict = new HashMap<>(){{
        put(Command.START, "/start");
        put(Command.HELP, "/help");
        put(Command.PHOTO, "/photo");
        put(Command.GIF, "/gif");
        put(Command.VIDEO, "/video");
        put(Command.REPORT, "/report");
        put(Command.CHECK_REPORTS, "/check_reports");
        put(Command.CLOSE, "/close");
        put(Command.NEXT, "/next");
        put(Command.DELETE, "/delete");
    }};

    public static Pair<Command, String> Parse(String input, Long chatId) {
        if (InfoController.isLastCommandReport(chatId)){
            return new Pair<>(Command.TEXT_REPORT, input);
        }
        for (var command: commandsDict.keySet()){
            if (input.startsWith(commandsDict.get(command))) {
                var parameters = input.substring(commandsDict.get(command).length());
                return new Pair<>(command, parameters);
            }
        }
        return new Pair<>(Command.UNKNOWN, input);
    }
}
