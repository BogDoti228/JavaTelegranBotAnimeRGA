package bot.overseersModule;

import bot.BotConstants;

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
        return BotConstants.INSTANCE.getBOT_OWNERS_CHAT_ID().contains(this.chatId);
    }
}
