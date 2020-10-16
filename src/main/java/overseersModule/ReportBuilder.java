package overseersModule;

import commands.Command;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashMap;
import java.util.Map;

public class ReportBuilder {
    private static Map<Long, InputFile> mapReportedInputFile = new HashMap<>();
    private static Map<Long, String> mapReportText = new HashMap<>();
    private static Map<Long, Map<InputFile, String>> mapReports = new HashMap<>();

    public static void addReportedInputFile(Long chatId, InputFile inputFile){
        mapReportedInputFile.put(chatId, inputFile);
    }

    public static void addReportText(Long chatId, String reportText){
        mapReportText.put(chatId, reportText);
    }

    public static void createReport(Long chatId){
        if (!mapReports.containsKey(chatId))
            mapReports.put(chatId, new HashMap<>());
        mapReports.get(chatId).put(mapReportedInputFile.get(chatId), mapReportText.get(chatId));
    }

}
