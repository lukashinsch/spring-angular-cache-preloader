package eu.hinsch.spring.angular.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by lh on 09/05/15.
 */
public class UrlRewritingRequestWrapper extends HttpServletRequestWrapper {

    private String url;

    public UrlRewritingRequestWrapper(HttpServletRequest request, String url) {
        super(request);
        this.url = url;
    }

    @Override
    public String getRequestURI() {
        return getContextPath() + getServletPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        // TODO fix
        return new StringBuffer("http://localhost:8080" + getRequestURI());
    }

    @Override
    public String getServletPath() {
        return url.startsWith("/") ? url : "/" + url;
    }
}
