package bot.commands.moderatorCommands.ownerCommands;

import bot.commands.moderatorCommands.ModeratorCommand;
import bot.overseersModule.ModeratorController;

public abstract class OwnerCommand extends ModeratorCommand {
    protected boolean isOwner(){
        return ModeratorController.INSTANCE.isOwner(this.moderatorId);
    }
}
