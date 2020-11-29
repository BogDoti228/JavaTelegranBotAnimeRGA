package bot.commands.moderatorCommands;

import bot.Bot;
import bot.commands.Command;
import bot.overseersModule.ModeratorController;

public abstract class ModeratorCommand extends Command {
    public ModeratorCommand(Long chatId) {
        super(chatId);
    }

    protected boolean isModerator(Bot bot) {
        return bot.getModeratorController().isUserModerator(chatId);
    }
}
