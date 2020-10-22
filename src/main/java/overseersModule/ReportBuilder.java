package overseersModule;

import commands.Command;
import commands.ContentType;
import objects.GoogleFileContent;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

public enum ReportBuilder {
    REPORT_BUILDER;
    private Map<Long, GoogleFileContent> mapReportedInputFile = new HashMap<>();
    private Map<Long, String> mapReportText = new HashMap<>();
    private Map<Long, Pair<GoogleFileContent, String>> mapLastReport= new HashMap<>();
    private Queue<Pair<GoogleFileContent, String>> listReports = new LinkedList<>();

    public void addReportedInputFile(Long chatId, GoogleFileContent googleFileContent){
        mapReportedInputFile.put(chatId, googleFileContent);
    }

    public void addReportText(Long chatId, String reportText){
        mapReportText.put(chatId, reportText);
    }

    public void createReport(Long chatId){
        listReports.offer(Pair.with(mapReportedInputFile.get(chatId), mapReportText.get(chatId)));
    }

    public Pair<GoogleFileContent, String> getReport(Long chatId){
        var report = listReports.poll();
        mapLastReport.put(chatId, report);
        return report;
    }

    public Pair<GoogleFileContent, String> getReportForDelete(Long chatId){
        var report = mapLastReport.get(chatId);
        mapLastReport.remove(chatId);
        return report;
    }

    public Boolean isReportAlreadyDeleted(Long chatId){
        return !mapLastReport.containsKey(chatId);
    }

    public Boolean isNoReports(){
        return listReports.size() == 0;
    }
}
