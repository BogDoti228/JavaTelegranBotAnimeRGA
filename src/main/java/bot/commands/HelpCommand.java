package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpCommand implements Command {
    private final String helpText =
            "/photo - запросить фото\n" +
                    "/video  - запросить видео\n" +
                    "/gif - запросить гифку \n" +
                    "/report - пожаловаться \n" +
                    "можете кидать любую фотку гиф или видео" +
                    " - все сохраним, поплняйте нашу базу " +
                    "- все будут круче и всего будет больше=)";

    @Override
    public void startExecute(Update update, Bot bot) {
        var chatId = update.getMessage().getChatId();
        bot.sendTextMessage(chatId, helpText);
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return false;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HELP;
    }
}