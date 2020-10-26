package overseersModule;

import commands.BotConstants;

import java.util.HashMap;
import java.util.Map;

public enum ModeratorController {
    MODERATOR_CONTROLLER;

    private final Map<Long, Moderator> moderators = new HashMap<>(){{
        put(BotConstants.BOT_CONSTANTS.getBOT_OWNER_CHAT_ID(),
                new Moderator(BotConstants.BOT_CONSTANTS.getBOT_OWNER_CHAT_ID()));
    }};

    public void addModerator(Long userId){
        if (isUserModerator(userId))
            return;
        moderators.put(userId, new Moderator(userId));
    }

    public boolean isOwner(Long userId){
        return isUserModerator(userId) && moderators.get(userId).isOwner();
    }

    public void demoteModerator(Long chatId) {
        if (isOwner(chatId)){
            return;
        }
        moderators.remove(chatId);
    }

    public Boolean isUserModerator(Long userId){
        return moderators.containsKey(userId);
    }

    public void openCheckMode(Long userId){
        moderators.get(userId).toCheckMode();
    }

    public void closeCheckMode(Long userId){
        moderators.get(userId).leaveCheckMode();
    }

    public Boolean isModeratorInCheckMode(Long userId){
        return moderators.get(userId).isInCheckMode();
    }
}
