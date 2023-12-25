package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpMessage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpMessage httpMessage = new HttpMessage(in);
            if (httpMessage.getHeaders().isEmpty()) return;
            String url = httpMessage.getHeader(HttpMessage.URL);

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;
            Pattern userCreatePattern = Pattern.compile("^/user/create?.*");
            if (url.equals("/index.html")) {
                body = getHtmlFileToString("index.html").getBytes();
            } else if (url.equals("/user/form.html")) {
                body = getHtmlFileToString("user/form.html").getBytes();
            } else if (userCreatePattern.matcher(url).find()) {
                Map<String, String> requestBody = httpMessage.getBody();
                User user = new User(
                        requestBody.get("userId"),
                        requestBody.get("password"),
                        requestBody.get("name"),
                        ""
                );
                body = user.toString().getBytes(StandardCharsets.UTF_8);
            } else {
                body = "Hello Kwang".getBytes();
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static String getHtmlFileToString(String fileName) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String path = rootPath + File.separator + "webapp" + File.separator + fileName;
        FileReader fileInputStream = new FileReader(path);

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(fileInputStream);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
