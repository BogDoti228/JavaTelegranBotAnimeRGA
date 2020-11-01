package bot.overseersModule;

import bot.commands.Command;
import bot.commands.sendCommands.SendCommand;

import java.util.HashMap;
import java.util.Map;

public enum InfoController {
    INSTANCE;

    private Map<Long, Command> mapDataIdCommands = new HashMap<>();
    private Map<Long, SendCommand> lastSentContent = new HashMap<>();

    public Command getLastCommand(Long chatId) {
        try {
            return mapDataIdCommands.get(chatId);
        } catch (Exception e) {
            return null;
        }
    }

    public void addLastCommand(Long chatId, Command command)
    {
        mapDataIdCommands.put(chatId, command);
        if (command instanceof SendCommand){
            lastSentContent.put(chatId, (SendCommand) command);
        }
    }

    public SendCommand getLastSentContent(Long chatId){
        if (lastSentContent.containsKey(chatId)){
            return lastSentContent.get(chatId);
        }
        return null;
    }
}
