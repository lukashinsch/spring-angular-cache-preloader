package eu.hinsch.spring.angular.cache;

import eu.hinsch.spring.angular.cache.AngularRestCachePreloadConfiguration.CachedUrl;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by lh on 14/06/15.
 */
public class AngularRestCachePreloadTransformerTest {

    private static final String REST_RESPONSE = "['A', 'B']";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(chain.transform(request, resource)).thenReturn(resource);
        when(config.getAngularModule()).thenReturn("myModule");
        when(config.getPlaceholder()).thenReturn("{cachePreloadScript}");
        when(config.getEncoding()).thenReturn("UTF-8");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("--{cachePreloadScript}--".getBytes()));
    }

    @Test
    public void shouldAddRestResponseToAngularCache() throws Exception {
        // given
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url")));
        mockResponse(REST_RESPONSE, 200);

        // when
        Resource transformedResource = transformer.transform(request, resource, chain);

        // then
        String content = getContent(transformedResource);
        assertThat(content, containsString("httpCache.put('/test/url', '" + REST_RESPONSE + "');"));
    }

    @Test
    public void shouldHandleDynamicParameters() throws Exception {
        // given
        Map<String,String> parameters = new HashMap<>();
        parameters.put("parameter", "1 + 1");
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url/{parameter}", parameters)));
        mockResponse(REST_RESPONSE, 200);

        // when
        Resource transformedResource = transformer.transform(request, resource, chain);

        // then
        String content = getContent(transformedResource);
        assertThat(content, containsString("httpCache.put('/test/url/2', '" + REST_RESPONSE + "');"));
    }

    @Test
    public void shouldNotCacheWhenConditionalExpressionEvaluatesFalse() throws Exception {
        CachedUrl cachedUrl = new CachedUrl("/test/url");
        cachedUrl.setEnabled("false");
        when(config.getCachedUrls()).thenReturn(singletonList(cachedUrl));
        mockResponse(REST_RESPONSE, 200);

        // when
        Resource transformedResource = transformer.transform(request, resource, chain);

        // then
        String content = getContent(transformedResource);
        assertThat(content, not(containsString("httpCache.put('/test/url', '" + REST_RESPONSE + "');")));
    }

    @Test
    public void shouldCacheWhenConditionalExpressionEvaluatesTrue() throws Exception {
        CachedUrl cachedUrl = new CachedUrl("/test/url");
        cachedUrl.setEnabled("true");
        when(config.getCachedUrls()).thenReturn(singletonList(cachedUrl));
        mockResponse(REST_RESPONSE, 200);

        // when
        Resource transformedResource = transformer.transform(request, resource, chain);

        // then
        String content = getContent(transformedResource);
        assertThat(content, containsString("httpCache.put('/test/url', '" + REST_RESPONSE + "');"));
    }

    @Test
    public void shouldThrowExceptionOnClientErrorResponse() throws Exception {
        // given
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url")));
        mockResponse("", 400);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error caching request /test/url, response status was 400");

        // when
        transformer.transform(request, resource, chain);

        // then -> exception
    }

    @Test
    public void shouldThrowExceptionOnServerErrorResponse() throws Exception {
        // given
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url")));
        mockResponse("", 500);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error caching request /test/url, response status was 500");

        // when
        transformer.transform(request, resource, chain);

        // then -> exception
    }

    @Test
    public void shouldWrapExceptionThrownByDispatchServlet() throws Exception {
        // given
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url")));
        doThrow(new IllegalArgumentException("my exception"))
                .when(dispatcherServlet).service(any(ServletRequest.class), any(ServletResponse.class));
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("error caching request /test/url");
        expectedException.expectCause(instanceOf(IllegalArgumentException.class));

        // when
        transformer.transform(request, resource, chain);

        // then -> exception
    }

    @Test
    public void shouldThrowExceptionOnUnsupportedEncoding() throws Exception {
        // given
        Map<String,String> parameters = new HashMap<>();
        parameters.put("parameter", "1 + 1");
        when(config.getCachedUrls()).thenReturn(singletonList(new CachedUrl("/test/url/{parameter}", parameters)));
        mockResponse(REST_RESPONSE, 200);
        when(config.getEncoding()).thenReturn("INVALID-ENCODING");

        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(instanceOf(UnsupportedEncodingException.class));
        expectedException.expectMessage("Encoding error");

        // when
        transformer.transform(request, resource, chain);
    }

    private void mockResponse(String restResponse, int status) throws ServletException, IOException {
        doAnswer(invocation -> {
            HttpServletResponse response = (HttpServletResponse) invocation.getArguments()[1];
            response.getOutputStream().write(restResponse.getBytes());
            response.setStatus(status);
            return null;
        }).when(dispatcherServlet).service(any(ServletRequest.class), any(ServletResponse.class));
    }


    private String getContent(Resource transformedResource) throws IOException {
        return IOUtils.toString(transformedResource.getInputStream());
    }


}