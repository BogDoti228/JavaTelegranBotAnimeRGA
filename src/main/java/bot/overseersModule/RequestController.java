package bot.overseersModule;

import bot.commands.AskModeratorCommand;
import bot.commands.ReportCommand;
import googleDrive.GoogleDriveClient;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;

public class RequestController implements Serializable {
    private final Queue<AskModeratorCommand> requests = new ArrayDeque<>();

    public void addNewRequest(AskModeratorCommand request){
        requests.add(request);
    }

    public AskModeratorCommand getRequest(){
        return requests.poll();
    }
}
