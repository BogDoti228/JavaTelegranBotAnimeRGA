package bot.commands;

import bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand extends Command {
    private static final String startText = "Привет всем пользующимся этим ботом=)\n" +
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
            " фото, видео, гиф можно кидать боту для пополнения базы данных," +
            " кидайте все что сочтете достойным для нас," +
            " всем очень благодарны за использование=)";

    public StartCommand(Long chatId) {
        super(chatId);
    }

    @Override
    public void startExecute(Update update, Bot bot) {
        var chatId = update.getMessage().getChatId();
        bot.sendTextMessage(chatId, startText);
    }

    @Override
    public boolean shouldContinue(Bot bot) {
        return false;
    }

    @Override
    public void continueExecute(Update update, Bot bot) {
        throw new UnsupportedOperationException();
    }
}
