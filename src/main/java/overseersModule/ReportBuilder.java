package overseersModule;

import commands.Command;
import commands.ContentType;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportBuilder {
    private static Map<Long, Pair<String, ContentType>> mapReportedInputFile = new HashMap<>();
    private static Map<Long, String> mapReportText = new HashMap<>();
    private static Map<Long, Integer> mapProgressionReports = new HashMap<>();
    private static ArrayList<Pair<Pair<String, ContentType>, String>> listReports = new ArrayList<>();

    public static void addReportedInputFile(Long chatId, Pair<String, ContentType> inputFileType){
        mapReportedInputFile.put(chatId, inputFileType);
    }

    public static void addReportText(Long chatId, String reportText){
        mapReportText.put(chatId, reportText);
    }

    public static void createReport(Long chatId){
        listReports.add(Pair.with(mapReportedInputFile.get(chatId), mapReportText.get(chatId)));
    }

    public static Pair<Pair<String, ContentType>, String> getProgressionRelativeReport(Long chatId){
        if (!mapProgressionReports.containsKey(chatId))
            mapProgressionReports.put(chatId, 0);

        return listReports.get(mapProgressionReports.get(chatId));
    }
    public static Pair<Pair<String, ContentType>, String> getNextReport(Long chatId){
        mapProgressionReports.put(chatId, mapProgressionReports.get(chatId) + 1);
        return listReports.get(mapProgressionReports.get(chatId));
    }

}
