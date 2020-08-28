package shrbox.github.acg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Connection {
    public static String getURL() {
        try {
            URL url = new URL("https://api.ixiaowai.cn/api/api.php?return=json");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String re = bufferedReader.readLine();
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
