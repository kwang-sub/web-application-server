package util;

import exception.HttpConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {

    private static final Logger log = LoggerFactory.getLogger(HttpMessage.class);

    private final Map<String, String> headers = new HashMap<>();

    private Map<String, String> body;

    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String VERSION = "version";

    public HttpMessage(InputStream in) throws IOException {

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String requestLine = bufferedReader.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            String[] requestLines = requestLine.split(" ");
            headers.put(METHOD, requestLines[0]);
            headers.put(URL, requestLines[1]);
            headers.put(VERSION, requestLines[2]);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isBlank()) break;
                log.debug("HTTP 헤더 " + line);
                String[] strings = line.split(":");
                headers.put(strings[0].trim(), strings[1].trim());
            }

            if (getHeader(HttpMessage.METHOD).contains("POST")) {
                String body = bufferedReader.readLine();
                this.body = HttpRequestUtils.parseQueryString(body);
            }

        } catch (IOException | ArrayIndexOutOfBoundsException ex) {
            throw new HttpConvertException();
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
