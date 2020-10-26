package overseersModule;

import commands.BotConstants;

public class Moderator {

    public final Long chatId;

    private boolean inCheckMode;

    public Moderator(Long chatId){
        this.chatId = chatId;
    }

    public boolean isInCheckMode(){
        return this.inCheckMode;
    }

    public void toCheckMode(){
        this.inCheckMode = true;
    }

    public void leaveCheckMode(){
        this.inCheckMode = false;
    }

    public boolean isOwner(){
        return this.chatId.equals(BotConstants.BOT_CONSTANTS.getBOT_OWNER_CHAT_ID());
    }
}
