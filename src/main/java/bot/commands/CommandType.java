package bot.commands;

public enum CommandType {
    START, HELP,
    REPORT, CHECK_REPORTS,
    RECEIVE_VIDEO, RECEIVE_GIF, RECEIVE_PHOTO,
    SEND_VIDEO, SEND_GIF, SEND_PHOTO,
   //DELETE, NEXT, CLOSE оказались не нужны, но мне не захотелось их отсюда удалять
    //это из-за того, что при входе в команду check_reports, она перехватывает управление
    //и команды delete, next и close вызываются изнутри check_reports
   //возможно стоит как-то по другому организовать
    SUDO, DESUDO,
    UNKNOWN
}
