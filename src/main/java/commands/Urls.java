package commands;

import java.util.ArrayList;
import java.util.Random;

public class Urls {
    public static ArrayList<String> downloadUrls = new ArrayList<>();
    public static ArrayList<String> urls = new ArrayList<>(){{
        add("https://images.alphacoders.com/727/72743.jpg");
        add("https://images6.alphacoders.com/539/539942.jpg");
        add("https://images3.alphacoders.com/677/677688.png");
        add("https://images7.alphacoders.com/784/784805.jpg");
        add("https://images6.alphacoders.com/657/657194.jpg");
    }};

    public static String getUrl()
    {
        var randomInt = new Random().nextInt(urls.size());
        return urls.get(randomInt);
    }
}
