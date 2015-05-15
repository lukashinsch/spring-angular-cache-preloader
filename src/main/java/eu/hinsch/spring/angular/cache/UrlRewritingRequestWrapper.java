package eu.hinsch.spring.angular.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by lh on 09/05/15.
 */
public class UrlRewritingRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private String url;

    public UrlRewritingRequestWrapper(HttpServletRequest request, String url) {
        super(request);
        this.request = request;
        this.url = url;
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
}
