package bot.commands.moderatorCommands;

import bot.commands.Command;
import bot.overseersModule.ModeratorController;

public abstract class ModeratorCommand implements Command {
    protected Long moderatorId;

    protected boolean isModerator() {
        return ModeratorController.INSTANCE.isUserModerator(moderatorId);
    }
}
