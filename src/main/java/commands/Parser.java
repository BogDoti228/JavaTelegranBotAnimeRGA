package commands;

import org.apache.log4j.Logger;
import overseersModule.InfoController;

public class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);

    public Command Parse(String input , Long chatId)
    {
        if (InfoController.isLastCommandReport(chatId))
            return Command.TEXT_REPORT;
        switch (input){
            case "/start":
                return Command.START;
            case "/help":
                return Command.HELP;
            case "/photo":
                return Command.PHOTO;
            case "/gif":
                return Command.GIF;
            case "/video":
                return Command.VIDEO;
            case "/report":
                return Command.REPORT;
            default:
                return Command.UNKNOWN;
        }
    }
}
