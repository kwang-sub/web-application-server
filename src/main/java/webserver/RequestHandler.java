package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpHeaderUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line= bufferedReader.readLine()) != null) {
                if (line.isBlank()) break;
                log.debug("HTTP 헤더 " + line);
                sb.append(line).append("\n");
            }
            log.debug("end sb append = {}", sb);
            if (sb.toString().isBlank()) return;
            // 루프가 완료된 후에 생성자 호출
            HttpHeaderUtils httpHeaderUtils = new HttpHeaderUtils(sb.toString());
            String url = httpHeaderUtils.getHeader(HttpHeaderUtils.URL);

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;
            if (url.equals("/index.html")) {
                body = getHtmlFileToString("index.html").getBytes();
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
