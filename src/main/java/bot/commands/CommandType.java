package bot.commands;

public enum CommandType {
    START, HELP,
    REPORT, CHECK_REPORTS,
    RECEIVE_VIDEO, RECEIVE_GIF, RECEIVE_PHOTO,
    SEND_VIDEO, SEND_GIF, SEND_PHOTO,
    SUDO, DESUDO,
    UNKNOWN
}
