package eu.hinsch.spring.angular.cache;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by lh on 05/07/15.
 */
public class ContentBufferingResponseTest {

    private ContentBufferingResponse response;

    @Before
    public void setup() {
        response = new ContentBufferingResponse();
    }

    @Test
    public void shouldReturnOutputStreamContent() throws IOException {
        response.getOutputStream().print("test");
        assertThat(response.getResponseContent(), is("test"));
    }

    @Test
    public void shouldStoreStatus() {
        response.setStatus(200);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void shouldStoreStatus2() {
        response.setStatus(200, "to be ignored");
        assertThat(response.getStatus(), is(200));
    }

    /**
     * The methods called by this test are never used, they just have to be implemented
     * as part of the interface. To get a meaningful coverage report (and to focus on
     * what is really missing) we just call these methods to generate coverage.
     */
    @Test
    public void shouldGenerateCoverageForUnusedMethods() throws IOException {
        response.addCookie(null);
        response.containsHeader(null);
        response.encodeUrl(null);
        response.encodeRedirectURL(null);
        response.encodeUrl(null);
        response.encodeRedirectUrl(null);
        response.sendError(0, null);
        response.sendError(0);
        response.sendRedirect(null);
        response.setDateHeader(null, 0L);
        response.addDateHeader(null, 0L);
        response.setHeader(null, null);
        response.addHeader(null, null);
        response.setIntHeader(null, 0);
        response.addIntHeader(null, 0);
        response.getHeader(null);
        response.getHeaders(null);
        response.getHeaderNames();
        response.getCharacterEncoding();
        response.getContentType();
        response.getWriter();
        response.setCharacterEncoding(null);
        response.setContentLength(0);
        response.setContentLengthLong(0L);
        response.setContentType(null);
        response.setBufferSize(0);
        response.getBufferSize();
        response.flushBuffer();
        response.resetBuffer();
        response.isCommitted();
        response.reset();
        response.setLocale(null);
        response.getLocale();
    }


}