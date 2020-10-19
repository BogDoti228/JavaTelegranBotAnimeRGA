package overseersModule;

import commands.Command;
import commands.ContentType;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashMap;
import java.util.Map;

public class InfoController {
    private static Map<Long, Pair<String, ContentType>> mapDataIdInputFiles = new HashMap<>();
    private static Map<Long, Command> mapDataIdCommands = new HashMap<>();

    public static void addLastInputFile(Long chatId, String inputFile, ContentType type)
    {
        mapDataIdInputFiles.put(chatId, Pair.with(inputFile, type));
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

    public static Pair<String, ContentType> getLastInputFile(Long chatId){
        return mapDataIdInputFiles.get(chatId);
    }
}
