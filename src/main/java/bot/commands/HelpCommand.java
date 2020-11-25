package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpCommand implements Command {
    private final String helpText =
            " напиши /photo чтобы получить фото\n " +
                    " напиши /photo tag1 tag2 ... чтобы получить фото с тегами tag1 и tag2 ...\n " +
                    " напиши /video чтобы получить видео\n " +
                    " напиши /video tag1 tag2 ... чтобы получить фото с тегами tag1 и tag2 ...\n " +
                    " напиши /gif чтобы получить гифку\n " +
                    " напиши /gif tag1 tag2 ... чтобы получить фото с тегами tag1 и tag2 ...\n " +
                    " напиши /report чтобы пожаловаться на файл\n " +
                    " напиши /ask_sudo, чтобы запросить права на модератора\n" +
                    " Для модераторов: \n" +
                    " /check_reports - проверить репорты\n" +
                    " /close, закрыть \n" +
                    " /next, следующий репорт\n" +
                    " /delete, удалить контент \n" +
                    " Для админов \n" +
                    " Все также как у модераторов, но также\n" +
                    " /check_requests, проверить запросы на модератора\n" +
                    " /sudo + кинуть контакт, сделать модером кого то\n" +
                    " /desudo + кинуть контакт, убрать модера \n" +
                    " /next, следующий запрос на модератора\n" +
                    " /close, закрыть режим проверки запросов модератора\n" +
                    " /accept, подтвердить запрос на модератора\n" +
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