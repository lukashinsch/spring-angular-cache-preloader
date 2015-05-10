package eu.hinsch.spring.angular.cache;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.ResourceTransformerSupport;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by lukas.hinsch on 08.05.2015.
 */
public class AngularRestCachePrefillTransformer extends ResourceTransformerSupport{

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ApplicationContext applicationContext;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final List<String> cachedUrls;
    private final String placeholder;
    private final String module;

    @Autowired
    public AngularRestCachePrefillTransformer(final RequestMappingHandlerMapping requestMappingHandlerMapping,
            final ApplicationContext applicationContext,
            final RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            final Environment environment) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.applicationContext = applicationContext;
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        cachedUrls = asList(environment.getProperty("cache.preload.urls", "").split(","));
        placeholder = environment.getProperty("cache.preload.placeholder", "{cachePreloadScript}");
        module = environment.getRequiredProperty("cache.preload.module");
    }


    @Override
    public Resource transform(final HttpServletRequest request,
            final Resource resource,
            final ResourceTransformerChain transformerChain) throws IOException {
        final Resource transformedResource = transformerChain.transform(request, resource);

        String content = IOUtils.toString(transformedResource.getInputStream());

        Map<String,String> cache = new HashMap<>();

        for (String cachedUrl : cachedUrls) {
            HandlerMethod handlerMethod = getHandlerMethod(cachedUrl);

            try {
                ContentBufferingResponse response = new ContentBufferingResponse();
                requestMappingHandlerAdapter.handle(new UrlRewritingRequestWrapper(request, cachedUrl), response, handlerMethod);
                cache.put(cachedUrl, response.getResponseContent());
            } catch (Exception e) {
                throw new RuntimeException("error caching request " + cachedUrl, e);
            }

        }

        content = content.replace(placeholder, createScript(cache));

        return new TransformedResource(transformedResource, content.getBytes("UTF-8"));
    }

    private HandlerMethod getHandlerMethod(String url) {
        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        return handlerMethods
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                                .getPatternsCondition()
                                .getPatterns()
                                .contains(url)
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no handler method found for " + url))
                .getValue();
    }

    // TODO template
    private String createScript(Map<String, String> cache) {
        StringBuilder script = new StringBuilder("<script>")
                .append("angular.module('")
                .append(module)
                .append("')")
                .append(".run(function($cacheFactory) {")
                .append("var httpCache = $cacheFactory.get('$http');");

        script.append(cache.entrySet()
                        .stream()
                        .map(entry -> "httpCache.put('" + entry.getKey() + "','" + entry.getValue() + "');")
                        .reduce("", (a, b) -> a + b)
        );

        script.append("});");
        script.append("</script>");
        return script.toString();
    }

}
