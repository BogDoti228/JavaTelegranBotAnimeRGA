package bot.overseersModule;

import constants.BotConstants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ModeratorController implements Serializable {
    private Map<Long, Moderator> moderators = new HashMap<>(){{
        for (var chatId : BotConstants.INSTANCE.getBOT_OWNERS_CHAT_ID()){
            put(chatId, new Moderator(chatId));
        }
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
        if (isUserModerator(chatId)) {
            moderators.get(chatId).leaveCheckMode();
            moderators.remove(chatId);
        }
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