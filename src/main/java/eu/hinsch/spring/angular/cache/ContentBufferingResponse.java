package eu.hinsch.spring.angular.cache;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by lh on 09/05/15.
 */
@SuppressWarnings("deprecation")
public class ContentBufferingResponse implements HttpServletResponse {

    private final ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
    private int sc;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public String getResponseContent() {
        return new String(outputStream.toByteArray());
    }

    @Override
    public void setStatus(int sc) {
        this.sc = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.sc = sc;
    }

    @Override
    public int getStatus() {
        return sc;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.sc = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.sc = sc;
    }

    // empty methods below

    @Override
    public void addCookie(Cookie cookie) {}

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendRedirect(String location) throws IOException {}

    @Override
    public void setDateHeader(String name, long date) {}

    @Override
    public void addDateHeader(String name, long date) {}

    @Override
    public void setHeader(String name, String value) {}

    @Override
    public void addHeader(String name, String value) {}

    @Override
    public void setIntHeader(String name, int value) {}

    @Override
    public void addIntHeader(String name, int value) {}

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {}

    @Override
    public void setContentLength(int len) {}

    @Override
    public void setContentLengthLong(long length) {}

    @Override
    public void setContentType(String type) {}

    @Override
    public void setBufferSize(int size) {}

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {}

    @Override
    public void resetBuffer() {}

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {}

    @Override
    public void setLocale(Locale loc) {}

    @Override
    public Locale getLocale() {
        return null;
    }
}
