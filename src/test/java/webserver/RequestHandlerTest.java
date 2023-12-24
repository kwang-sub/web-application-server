package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestHandlerTest {

    private RequestHandler requestHandler;
    ByteArrayOutputStream byteArrayOutputStream;

    @BeforeEach
    public void init() {
        String httpHeader = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        byteArrayOutputStream = new ByteArrayOutputStream();
        MockSocket mockSocket = new MockSocket(byteArrayInputStream, byteArrayOutputStream);
        requestHandler = new RequestHandler(mockSocket);
    }

    @Test
    public void indexHtmlTest() throws InterruptedException, IOException {
        requestHandler.start();
        requestHandler.join();
        String response = byteArrayOutputStream.toString();
        String indexHtml = RequestHandler.getHtmlFileToString("index.html");
        assertThat(response).contains(indexHtml);
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