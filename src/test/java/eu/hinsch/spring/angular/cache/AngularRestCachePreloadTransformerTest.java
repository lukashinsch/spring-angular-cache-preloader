package eu.hinsch.spring.angular.cache;

import eu.hinsch.spring.angular.cache.AngularRestCachePreloadConfiguration.CachedUrl;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by lh on 14/06/15.
 */
public class AngularRestCachePreloadTransformerTest {

    private static final String REST_RESPONSE = "['A', 'B']";

    @Mock
    private AngularRestCachePreloadConfiguration config;
    @Mock
    private DispatcherServlet dispatcherServlet;
    @Mock
    private BeanFactory beanFactory;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ResourceTransformerChain chain;
    @Mock
    private Resource resource;

    @InjectMocks
    private AngularRestCachePreloadTransformer transformer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAddRestResponseToAngularCache() throws IOException, ServletException {
        // given
        when(chain.transform(request, resource)).thenReturn(resource);
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url")));
        when(config.getAngularModule()).thenReturn("myModule");
        when(config.getPlaceholder()).thenReturn("{cachePreloadScript}");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("--{cachePreloadScript}--".getBytes()));
        doAnswer(invocation -> {
            ServletResponse response = (ServletResponse) invocation.getArguments()[1];
            response.getOutputStream().write(REST_RESPONSE.getBytes());
            return null;
        }).when(dispatcherServlet).service(any(ServletRequest.class), any(ServletResponse.class));

        // when
        Resource transformedResource = transformer.transform(request, resource, chain);

        // then
        ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        verify(dispatcherServlet).service(requestCaptor.capture(), responseCaptor.capture());

        String content = IOUtils.toString(transformedResource.getInputStream());
        assertThat(content, containsString("httpCache.put('/test/url', '" + REST_RESPONSE + "');"));
    }


}