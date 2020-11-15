package shrbox.github.acgpro;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Connection {
    public static String getURL(String keyword, Boolean isr18) {
        try {
            URL url;
            String address = "https://api.lolicon.app/setu/?num=10&apikey=" + Main.config.getString("apikey");
            if (isr18) {
                url = new URL(address + "&r18=2&" + "keyword=" + keyword);
            } else {
                url = new URL(address + "&keyword=" + keyword);
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
            while ((reading = bufferedReader.readLine()) != null) {
                content = content.concat(reading);
            }
            content = content.replaceFirst("\n", "");
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
