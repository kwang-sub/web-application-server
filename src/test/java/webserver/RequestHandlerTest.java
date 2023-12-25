package webserver;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestHandlerTest {

    @Test
    public void indexHtmlTest() throws InterruptedException, IOException {
        String httpHeader = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MockSocket mockSocket = new MockSocket(byteArrayInputStream, byteArrayOutputStream);
        RequestHandler requestHandler = new RequestHandler(mockSocket);

        requestHandler.start();
        requestHandler.join();
        String response = byteArrayOutputStream.toString();

        String indexHtml = RequestHandler.getHtmlFileToString("index.html");
        assertThat(response).contains(indexHtml);
    }

    @Test
    public void formHtmlTest() throws InterruptedException, IOException {
        String httpHeader = "GET /user/form.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MockSocket mockSocket = new MockSocket(byteArrayInputStream, byteArrayOutputStream);
        RequestHandler requestHandler = new RequestHandler(mockSocket);

        requestHandler.start();
        requestHandler.join();
        String response = byteArrayOutputStream.toString();

        String indexHtml = RequestHandler.getHtmlFileToString("user/form.html");
        assertThat(response).contains(indexHtml);
    }

    @Test
    public void 회원가입() throws InterruptedException {
        String httpHeader = "POST /user/create HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*\n\n userId=kwang&password=password&name=kwangsub";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MockSocket mockSocket = new MockSocket(byteArrayInputStream, byteArrayOutputStream);
        RequestHandler requestHandler = new RequestHandler(mockSocket);

        requestHandler.start();
        requestHandler.join();
        String response = byteArrayOutputStream.toString();

        assertThat(response).contains("kwang", "password", "kwangsub");
    }
}


class MockSocket extends Socket {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public MockSocket(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}