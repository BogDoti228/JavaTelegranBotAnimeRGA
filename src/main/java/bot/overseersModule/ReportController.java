package bot.overseersModule;

import bot.commands.ReportCommand;
import bot.content.GoogleContentFile;
import googleDrive.GoogleDriveClient;

import java.io.Serializable;
import java.util.*;

public class ReportController implements Serializable {
    private final HashMap<GoogleContentFile, ReportCommand> reports = new HashMap<>();

    public void deleteReportedFile(ReportCommand report) {
        var googleFileContent = report.getReportedFile();
        GoogleDriveClient.INSTANCE.deleteFile(googleFileContent.getId());
    }

    public void addNewReport(ReportCommand report) {
        var reportedFile = report.getReportedFile();
        if (!reports.containsKey(reportedFile)) {
            reports.put(reportedFile, report);
        } else {
            reports.replace(reportedFile, report);
        }
    }

    public ReportCommand getReport(){
        for (var e: this.reports.keySet()){
            var report = reports.get(e);
            this.reports.remove(e);
            return report;
        }
        return null;
    }
}
