package edu.utep.cs.cs4330.mypricewatcher.networking;

public class NetworkUtil {
    public static String getTagContent(String html, String openTag, String endTag) {
        int startIndex = html.lastIndexOf(openTag);
        int endIndex = html.indexOf(endTag, startIndex);

        int contentStart = startIndex + openTag.length();
        String content = html.substring(contentStart, endIndex);
        return content;
    }
}
