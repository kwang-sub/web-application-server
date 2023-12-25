package util;

import exception.HttpConvertException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HttpMessageTest {

    @Test
    public void 맵형태로_헤더를_읽을_수_있다() throws IOException {
        String httpHeader = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        HttpMessage utils = new HttpMessage(byteArrayInputStream);

        Map<String, String> headers = utils.getHeaders();
        String method = headers.get(HttpMessage.METHOD);
        String url = headers.get(HttpMessage.URL);

        assertThat(method).isEqualTo("GET");
        assertThat(url).isEqualTo("/index.html");
    }

    @Test
    public void 헤더포맷예외발생() {
        String httpHeader = "GET/index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpHeader.getBytes());
        assertThatThrownBy(() -> new HttpMessage(byteArrayInputStream)).isInstanceOf(HttpConvertException.class);
    }
}
