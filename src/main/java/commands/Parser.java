package commands;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import overseersModule.InfoController;

import java.util.HashMap;
import java.util.Map;

public enum  Parser {
    PARSER;

    private final Logger log = Logger.getLogger(Parser.class);
    private final Map<Command, String> commandsDict = new HashMap<>(){{
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
        put(Command.SUDO, "/sudo");
        put(Command.DESUDO, "/desudo");
    }};

    public Pair<Command, String> Parse(String input, Long chatId) {
        if (InfoController.INFO_CONTROLLER.isLastCommandReport(chatId)){
            return new Pair<>(Command.TEXT_REPORT, input);
        }
        for (var command: commandsDict.keySet()){
            if (input.startsWith(commandsDict.get(command))) {
                var parameters = "";
                if (input.length() != commandsDict.get(command).length())
                    parameters = input.substring(commandsDict.get(command).length() + 1);
                return new Pair<>(command, parameters);
            }
        }
        return new Pair<>(Command.UNKNOWN, input);
    }
}