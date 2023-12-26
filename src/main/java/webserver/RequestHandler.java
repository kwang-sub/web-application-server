package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpMessage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
            log.debug("request url {}", url);

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;
            String statusCode = "200";
            if (url.equals("/index.html")) {
                body = getHtmlFileToString("index.html").getBytes();
            } else if (url.equals("/user/form.html")) {
                body = getHtmlFileToString("user/form.html").getBytes();
            } else if (url.equals("/user/create")) {
                Map<String, String> requestBody = httpMessage.getBody();
                User user = new User(
                        requestBody.get("userId"),
                        requestBody.get("password"),
                        requestBody.get("name"),
                        ""
                );
                log.debug("create user {}", user);
                body = user.toString().getBytes(StandardCharsets.UTF_8);
                statusCode = "302";
                response302Header(dos, "/index.html");
            } else {
                body = "Hello Kwang".getBytes();
            }
            if (statusCode.equals("200")) {
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
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

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
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
        try (BufferedReader bufferedReader = new BufferedReader(fileInputStream)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }
}
