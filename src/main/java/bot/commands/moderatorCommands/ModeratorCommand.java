package bot.commands.moderatorCommands;

import bot.Bot;
import bot.commands.Command;
import bot.overseersModule.ModeratorController;

public abstract class ModeratorCommand implements Command {
    protected Long moderatorId;

    protected boolean isModerator(Bot bot) {
        return bot.getModeratorController().isUserModerator(moderatorId);
    }
}
