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
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public enum CommandParser {
    INSTANCE;

    private final Map<Class, String> commandsDict = new HashMap<>(){{
        put(StartCommand.class, "/start");
        put(HelpCommand.class, "/help");
        put(SendPhotoCommand.class, "/photo");
        put(SendGifCommand.class, "/gif");
        put(SendVideoCommand.class, "/video");
        put(ReportCommand.class, "/report");
        put(CheckReportsCommand.class, "/check_reports");
        put(SudoCommand.class, "/sudo");
        put(DesudoCommand.class, "/desudo");
        put(AskModeratorCommand.class, "/ask_sudo");
        put(CheckRequestsCommand.class, "/check_requests");
    }};

    public Command getCommandByUpdate(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        if (message.hasPhoto()){
            return new ReceivePhotoCommand(chatId);
        } else if (message.hasAnimation()){
            return new ReceiveGifCommand(chatId);
        } else  if (message.hasVideo()) {
            return new ReceiveVideoCommand(chatId);
        } else if (message.hasText()) {
            var inputText = message.getText();
            for (var command : commandsDict.keySet()) {
                if (inputText.startsWith(commandsDict.get(command))) {
                    try {
                        return (Command) command.getConstructor(Long.class).newInstance(chatId);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return new UnknownCommand(chatId);
    }

    public String getCommandParameters(String input, Command command){
        if (input.length() != commandsDict.get(command.getClass()).length())
            return input.substring(commandsDict.get(command.getClass()).length() + 1);
        return "";
    }
}