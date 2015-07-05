package eu.hinsch.spring.angular.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

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
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test");

        // when
        String servletPath = wrapper.getServletPath();

        // then
        assertThat(servletPath, is("/test"));
    }

    @Test
    public void shouldAddMissingSlashToServletPath() {
        // given
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "test");

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
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test");

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
        UrlRewritingRequestWrapper wrapper = new UrlRewritingRequestWrapper(httpServletRequest, "/test");

        // when
        StringBuffer requestURL = wrapper.getRequestURL();

        // then
        assertThat(requestURL.toString(), is("http://localhost:8080/context/test"));
    }

}