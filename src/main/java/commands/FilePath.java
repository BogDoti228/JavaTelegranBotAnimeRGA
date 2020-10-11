package commands;

import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class FilePath {

    public static String getDownloadUrl(String fileId, String botToken){
        var request = getRequestHttp(fileId, botToken);
        var data = executePost(request);
        assert data != null;
        var pathFile = getFilePath(data);
        return File.getFileUrl(botToken, pathFile);
    }

    private static String getFilePath(String data){
        assert data != null;
        var arrayInfo = data.split(":");
        var strangePath = arrayInfo[arrayInfo.length-1];
        var pathFile = new StringBuilder();
        for (var i = 1; i < strangePath.length() - 3; i++) {
            pathFile.append(strangePath.charAt(i));
        }
        return pathFile.toString();
    }

    private static String getRequestHttp(String fileId, String botToken){
        return "https://api.telegram.org/bot"+botToken+"/getFile?file_id=" + fileId;
    }

    private static String executePost(String targetURL) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
