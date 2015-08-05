package eu.hinsch.spring.angular.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.enumeration;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by lh on 05/07/15.
 */
public class UrlRewritingRequestWrapperTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnServletPathUnchangedIfStartingWithSlash() {
        // given
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());

        // when
        String servletPath = wrapper.getServletPath();

        // then
        assertThat(servletPath, is("/test"));
    }

    @Test
    public void shouldAddMissingSlashToServletPath() {
        // given
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "test", emptyMap());

        // when
        String servletPath = wrapper.getServletPath();

        // then
        assertThat(servletPath, is("/test"));
    }

    @Test
    public void shouldRequestURIReplaceLocalUrl() {
        // given
        when(httpServletRequest.getServletPath()).thenReturn("/original");
        when(httpServletRequest.getContextPath()).thenReturn("/context");
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());

        // when
        String requestURI = wrapper.getRequestURI();

        // then
        assertThat(requestURI, is("/context/test"));
    }

    @Test
    public void shouldRequestURLSubstituteServletPath() {
        // given
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/context/original"));
        when(httpServletRequest.getServletPath()).thenReturn("/original");
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());

        // when
        StringBuffer requestURL = wrapper.getRequestURL();

        // then
        assertThat(requestURL.toString(), is("http://localhost:8080/context/test"));
    }

    @Test
    public void shouldReturnOverwrittenContentType() {
        // given
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "overwritten content type");
        when(httpServletRequest.getContentType()).thenReturn("wrapped request content type");
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", headers);

        // when
        String contentType = wrapper.getContentType();

        // then
        assertThat(contentType, is("overwritten content type"));
    }

    @Test
    public void shouldReturnContentTypeFromWrappedRequest() {
        // given
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());
        when(httpServletRequest.getContentType()).thenReturn("wrapped request content type");

        // when
        String contentType = wrapper.getContentType();

        // then
        assertThat(contentType, is("wrapped request content type"));
    }

    @Test
    public void shouldReturnOverwrittenIntHeader() {
        // given
        Map<String,String> headers = new HashMap<>();
        headers.put("X-my-header", "1");
        when(httpServletRequest.getIntHeader("X-my-header")).thenReturn(0);
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", headers);

        // when
        int value = wrapper.getIntHeader("X-my-header");

        // then
        assertThat(value, is(1));
    }

    @Test
    public void shouldReturnIntHeaderFromWrappedRequest() {
        // given
        when(httpServletRequest.getIntHeader("X-my-header")).thenReturn(1);
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());

        // when
        int value = wrapper.getIntHeader("X-my-header");

        // then
        assertThat(value, is(1));
    }

    @Test
    public void shouldReturnOverwrittenHeader() {
        // given
        Map<String,String> headers = new HashMap<>();
        headers.put("X-my-header", "overwritten value");
        when(httpServletRequest.getHeader("X-my-header")).thenReturn("wrapped request value");
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", headers);

        // when
        String value = wrapper.getHeader("X-my-header");

        // then
        assertThat(value, is("overwritten value"));
    }

    @Test
    public void shouldReturnHeaderFromWrappedRequest() {
        // given
        when(httpServletRequest.getHeader("X-my-header")).thenReturn("wrapped request value");
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", emptyMap());

        // when
        String value = wrapper.getHeader("X-my-header");

        // then
        assertThat(value, is("wrapped request value"));
    }

    @Test
    public void shouldReturnAddedAndOriginalHeaders() {
        // given
        Map<String,String> headers = new HashMap<>();
        headers.put("X-my-header", "added value");
        when(httpServletRequest.getHeaders("X-my-header")).thenReturn(enumeration(singletonList("wrapped request value")));
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", headers);

        // when
        Enumeration<String> value = wrapper.getHeaders("X-my-header");

        // then
        assertThat(value.nextElement(), is("added value"));
        assertThat(value.nextElement(), is("wrapped request value"));
    }

    @Test
    public void shouldReturnAddedAndOriginalHeaderNames() {
        // given
        Map<String,String> headers = new HashMap<>();
        headers.put("X-my-header-1", "added value");
        when(httpServletRequest.getHeaderNames()).thenReturn(enumeration(singletonList("X-my-header-2")));
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test", headers);

        // when
        Enumeration<String> value = wrapper.getHeaderNames();

        // then
        assertThat(value.nextElement(), is("X-my-header-1"));
        assertThat(value.nextElement(), is("X-my-header-2"));
    }



}