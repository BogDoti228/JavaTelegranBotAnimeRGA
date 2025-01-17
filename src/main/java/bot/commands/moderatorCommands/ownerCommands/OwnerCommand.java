package bot.commands.moderatorCommands.ownerCommands;

import bot.Bot;
import bot.commands.moderatorCommands.ModeratorCommand;
import bot.overseersModule.ModeratorController;

public abstract class OwnerCommand extends ModeratorCommand {
    public OwnerCommand(Long chatId) {
        super(chatId);
    }

    protected boolean isOwner(Bot bot){
        return bot.getModeratorController().isOwner(chatId);
    }
}
