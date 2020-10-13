package commands;

import it.grabz.grabzit.GrabzItClient;

import java.io.UnsupportedEncodingException;

public class GifConverter {
    public static byte[] gifConverter(String url)
    {
        var converter = new GrabzItClient(BotConstants.APPLICATION_KEY, BotConstants.APPLICATION_SECRET);
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
