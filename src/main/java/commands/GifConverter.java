package commands;

import it.grabz.grabzit.GrabzItClient;

import java.io.UnsupportedEncodingException;

public enum GifConverter {
    GIF_CONVERTER;
    public byte[] gifConverter(String url)
    {
        var converter = new GrabzItClient(BotConstants.BOT_CONSTANTS.getAPPLICATION_KEY(), BotConstants.BOT_CONSTANTS.getAPPLICATION_SECRET());
        try {
            converter.URLToAnimation(url);
            try {
                var object = converter.SaveTo();
                return object.getBytes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
