package commands;

import org.apache.log4j.Logger;

public class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);

    public Command Parse(String input)
    {
        switch (input){
            case "/start":
                return Command.START;
            case "/help":
                return Command.HELP;
            case "/sendPhoto":
                return Command.SEND_PHOTO;
            default:
                return Command.UNKNOWN;
        }
    }
}
