package bot.commands;

import bot.commands.moderatorCommands.ownerCommands.CheckRequestsCommand;
import bot.commands.receiveCommands.ReceiveGifCommand;
import bot.commands.receiveCommands.ReceivePhotoCommand;
import bot.commands.receiveCommands.ReceiveVideoCommand;
import bot.commands.sendCommands.SendGifCommand;
import bot.commands.sendCommands.SendPhotoCommand;
import bot.commands.sendCommands.SendVideoCommand;
import bot.commands.moderatorCommands.CheckReportsCommand;
import bot.commands.moderatorCommands.ownerCommands.DesudoCommand;
import bot.commands.moderatorCommands.ownerCommands.SudoCommand;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public enum CommandParser {
    INSTANCE;

    private final Logger log = Logger.getLogger(CommandParser.class);
    private final Map<CommandType, String> commandsDict = new HashMap<>(){{
        put(CommandType.START, "/start");
        put(CommandType.HELP, "/help");
        put(CommandType.SEND_PHOTO, "/photo");
        put(CommandType.SEND_GIF, "/gif");
        put(CommandType.SEND_VIDEO, "/video");
        put(CommandType.REPORT, "/report");
        put(CommandType.CHECK_REPORTS, "/check_reports");
        put(CommandType.SUDO, "/sudo");
        put(CommandType.DESUDO, "/desudo");
        put(CommandType.REQUEST, "/ask_sudo");
        put(CommandType.CHECK_REQUESTS, "/check_requests");
    }};

    public Command getCommandByUpdate(Update update) {
        var message = update.getMessage();
        if (message.hasPhoto()){
            return new ReceivePhotoCommand();
        } else if (message.hasAnimation()){
            return new ReceiveGifCommand();
        } else  if (message.hasVideo()) {
            return new ReceiveVideoCommand();
        } else if (message.hasText()) {
            var inputText = message.getText();
            for (var command : commandsDict.keySet()) {
                if (inputText.startsWith(commandsDict.get(command))) {
                    return getCommandByType(command, message.getChatId());
                }
            }
        }
        return new UnknownCommand();
    }

    public String getCommandParameters(String input, CommandType commandType){
        if (input.length() != commandsDict.get(commandType).length())
            return input.substring(commandsDict.get(commandType).length() + 1);
        return "";
    }

    public Command getCommandByType(CommandType type, Long chatId){
        switch (type){
            case HELP:
                return new HelpCommand();
            case START:
                return new StartCommand();
            case SEND_PHOTO:
                return new SendPhotoCommand();
            case SEND_GIF:
                return new SendGifCommand();
            case SEND_VIDEO:
                return new SendVideoCommand();
            case REPORT:
                return new ReportCommand();
            case CHECK_REPORTS:
                return new CheckReportsCommand();
            case SUDO:
                return new SudoCommand();
            case DESUDO:
                return new DesudoCommand();
            case REQUEST:
                return new AskModeratorCommand();
            case CHECK_REQUESTS:
                return new CheckRequestsCommand();
            default:
                return null;
        }
    }
}