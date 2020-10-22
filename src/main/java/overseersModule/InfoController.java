package overseersModule;

import commands.Command;
import commands.ContentType;
import objects.GoogleFileContent;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashMap;
import java.util.Map;

public enum InfoController {
    INFO_CONTROLLER;
    private Map<Long, GoogleFileContent> mapDataIdInputFiles = new HashMap<>();
    private Map<Long, Command> mapDataIdCommands = new HashMap<>();

    public void addLastInputFile(Long chatId, GoogleFileContent googleFileContent)
    {
        mapDataIdInputFiles.put(chatId, googleFileContent);
        mapDataIdCommands.remove(chatId);
    }

    public void addLastCommand(Long chatId, Command command)
    {
        mapDataIdCommands.put(chatId, command);
        mapDataIdInputFiles.remove(chatId);
    }

    public Boolean isExistInputFile(Long chatId){
        return mapDataIdInputFiles.containsKey(chatId);
    }

    public Boolean isLastCommandReport(Long chatId){
        return mapDataIdCommands.get(chatId) == Command.REPORT;
    }

    public GoogleFileContent getLastInputFile(Long chatId){
        return mapDataIdInputFiles.get(chatId);
    }
}
