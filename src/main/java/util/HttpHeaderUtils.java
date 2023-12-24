package util;

import exception.HttpConvertException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaderUtils {

    private final Map<String, String> headers = new HashMap<>();

    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String VERSION = "version";

    public HttpHeaderUtils(String httpMessageHeader) {

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(httpMessageHeader))) {
            String[] firstLine = bufferedReader.readLine().split(" ");
            headers.put(METHOD, firstLine[0]);
            headers.put(URL, firstLine[1]);
            headers.put(VERSION, firstLine[2]);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(":");
                headers.put(strings[0].trim(), strings[1].trim());
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            throw new HttpConvertException();
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
