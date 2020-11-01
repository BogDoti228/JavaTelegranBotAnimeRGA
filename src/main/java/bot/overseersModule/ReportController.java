package bot.overseersModule;

import bot.commands.ReportCommand;
import googleDrive.GoogleDriveClient;

import java.util.ArrayDeque;
import java.util.Queue;

public enum ReportController {
    INSTANCE;

    private Queue<ReportCommand> reports = new ArrayDeque<>();

    public void deleteReportedFile(ReportCommand report) {
        var googleFileContent = report.getReportedFile();
        GoogleDriveClient.INSTANCE.deleteFile(googleFileContent.getId());
    }

    public void addNewReport(ReportCommand report){
        reports.add(report);
    }

    public ReportCommand getReport(){
        return reports.poll();
    }
}
