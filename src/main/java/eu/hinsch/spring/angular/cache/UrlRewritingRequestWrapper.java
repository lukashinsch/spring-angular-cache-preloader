package eu.hinsch.spring.angular.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Created by lh on 09/05/15.
 */
public class UrlRewritingRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private String url;
    private Map<String, String> headers;

    public UrlRewritingRequestWrapper(HttpServletRequest request, String url, Map<String,String> headers) {
        super(request);
        this.request = request;
        this.url = url;
        this.headers = headers;
    }

    @Override
    public String getRequestURI() {
        return getContextPath() + getServletPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        final StringBuffer originalRequestURL = request.getRequestURL();
        final String prefix = originalRequestURL.substring(0, originalRequestURL.length() - request.getServletPath().length());
        return new StringBuffer(prefix + getServletPath());
    }

    @Override
    public String getServletPath() {
        return url.startsWith("/") ? url : "/" + url;
    }

    @Override
    public int getIntHeader(String name) {
        if (headers.containsKey(name)) {
            return Integer.parseInt(headers.get(name));
        }
        return super.getIntHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return headers.getOrDefault(name, super.getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        HashSet<String> values = new HashSet<>(Collections.list(super.getHeaders(name)));
        values.add(headers.get(name));
        return Collections.enumeration(values);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        HashSet<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
        names.addAll(headers.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String getContentType() {
        return headers.getOrDefault("Content-Type", super.getContentType());
    }


}
