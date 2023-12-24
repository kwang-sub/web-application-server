package util;

import exception.HttpConvertException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HttpHeaderUtilsTest {

    @Test
    public void 맵형태로_헤더를_읽을_수_있다() {
        String httpHeader = "GET /index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";
        HttpHeaderUtils utils = new HttpHeaderUtils(httpHeader);

        Map<String, String> headers = utils.getHeaders();
        String method = headers.get(HttpHeaderUtils.METHOD);
        String url = headers.get(HttpHeaderUtils.URL);

        assertThat(method).isEqualTo("GET");
        assertThat(url).isEqualTo("/index.html");
    }


    @Test
    public void 헤더포맷예외발생() {
        String httpHeader = "GET/index.html HTTP/1.1\nHost: localhost:8080\nConnection: keep-alive\nAccept: */*";

        assertThatThrownBy(() -> new HttpHeaderUtils(httpHeader)).isInstanceOf(HttpConvertException.class);
    }
}
