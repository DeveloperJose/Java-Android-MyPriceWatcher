package edu.utep.cs.cs4330.mypricewatcher.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Network {
    public static String clean(String html) {
        int bodyStart = html.indexOf("<body");
        int bodyEnd = html.indexOf("</body");

        String body = html.substring(bodyStart + 4, bodyEnd);
        int scriptIDX = body.indexOf("<script");
        while (scriptIDX >= 0) {
            int afterIDX = body.indexOf("</script>", scriptIDX);
            String bodyBefore = body.substring(0, scriptIDX);
            String bodyAfter = body.substring(afterIDX);
            body = bodyBefore + bodyAfter;

            scriptIDX = body.indexOf("<script");
        }
        return body;
    }

    public static String getHTML(String itemURL) throws MalformedURLException {
        URL url = new URL(itemURL);

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            return (buffer.toString());
        } catch (IOException ex) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
