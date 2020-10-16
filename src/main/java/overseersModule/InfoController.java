package overseersModule;

import commands.Command;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashMap;
import java.util.Map;

public class InfoController {
    private static Map<Long, InputFile> mapDataIdInputFiles = new HashMap<>();
    private static Map<Long, Command> mapDataIdCommands = new HashMap<>();

    public static void addLastInputFile(Long chatId, InputFile inputFile)
    {
        mapDataIdInputFiles.put(chatId, inputFile);
        mapDataIdCommands.remove(chatId);
    }

    public static void addLastCommand(Long chatId, Command command)
    {
        mapDataIdCommands.put(chatId, command);
        mapDataIdInputFiles.remove(chatId);
    }

    public static Boolean isExistInputFile(Long chatId){
        return mapDataIdInputFiles.containsKey(chatId);
    }

    public static Boolean isLastCommandReport(Long chatId){
        return mapDataIdCommands.get(chatId) == Command.REPORT;
    }

    public static InputFile getLastInputFile(Long chatId){
        return mapDataIdInputFiles.get(chatId);
    }
}
