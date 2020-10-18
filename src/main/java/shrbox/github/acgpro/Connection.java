package shrbox.github.acgpro;

import net.mamoe.mirai.console.plugins.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Connection {
    public static String getURL(String keyword, Boolean isr18) {
        Config config = Main.config;
        try {
            URL url = null;
            if (keyword.equals("")) {
                if (isr18) {
                    url = new URL("https://api.lolicon.app/setu/?apikey=" + config.getString("apikey") + "&num=10&r18=2");
                } else {
                    url = new URL("https://api.lolicon.app/setu/?apikey=" + config.getString("apikey") + "&num=10");
                }
            } else {
                if (isr18) {
                    url = new URL("https://api.lolicon.app/setu/?apikey=" + config.getString("apikey") + "&keyword=" + keyword + "&num=10&r18=2");
                } else {
                    url = new URL("https://api.lolicon.app/setu/?apikey=" + config.getString("apikey") + "&num=10&keyword=" + keyword);
                }
            }
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String reading, content = "";
            while((reading = bufferedReader.readLine()) != null) {
                content = content.concat(reading);
            }
            content = content.replaceFirst("\n|\r", "");
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
