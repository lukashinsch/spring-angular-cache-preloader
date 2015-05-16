package eu.hinsch.spring.angular.cache;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
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
import java.util.Map;

/**
 * Created by lukas.hinsch on 08.05.2015.
 */
@Component
public class AngularRestCachePreloadTransformer extends ResourceTransformerSupport{

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ApplicationContext applicationContext;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private AngularRestCachePreloadConfiguration config;
    private final Configuration freemarkerConfig;

    @Autowired
    public AngularRestCachePreloadTransformer(final RequestMappingHandlerMapping requestMappingHandlerMapping,
                                              final ApplicationContext applicationContext,
                                              final RequestMappingHandlerAdapter requestMappingHandlerAdapter,
                                              final AngularRestCachePreloadConfiguration config) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.applicationContext = applicationContext;
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.config = config;

        freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
    }


    @Override
    public Resource transform(final HttpServletRequest request,
            final Resource resource,
            final ResourceTransformerChain transformerChain) throws IOException {
        final Resource transformedResource = transformerChain.transform(request, resource);

        String content = IOUtils.toString(transformedResource.getInputStream());

        Map<String, String> cache = createCache(request);
        String script = createScript(cache);
        content = content.replace(config.getPlaceholder(), script);

        return new TransformedResource(transformedResource, content.getBytes("UTF-8"));
    }

    private Map<String, String> createCache(HttpServletRequest request) {
        Map<String,String> cache = new HashMap<>();

        for (String url : config.getUrls()) {
            String controllerResponse = executeControllerMethod(request, url);
            cache.put(url, controllerResponse);
        }
        return cache;
    }

    private String executeControllerMethod(HttpServletRequest request, String cachedUrl) {
        ContentBufferingResponse response = new ContentBufferingResponse();
        HandlerMethod controllerHandlerMethod = createControllerHandlerMethod(cachedUrl);

        try {
            requestMappingHandlerAdapter.handle(new UrlRewritingRequestWrapper(request, cachedUrl), response, controllerHandlerMethod);
        } catch (Exception e) {
            throw new RuntimeException("error caching request " + cachedUrl, e);
        }
        return response.getResponseContent();
    }

    private HandlerMethod createControllerHandlerMethod(String cachedUrl) {
        HandlerMethod handlerMethod = getOriginalHandlerMethod(cachedUrl);
        Object controller = applicationContext.getBean((String) handlerMethod.getBean());
        return new HandlerMethod(controller, handlerMethod.getMethod());
    }

    private HandlerMethod getOriginalHandlerMethod(String url) {
        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        return handlerMethods
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                                    .getPatternsCondition()
                                    .getPatterns()
                                    .contains(url.startsWith("/") ? url : "/" + url)
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no handler method found for " + url))
                .getValue();
    }

    private String createScript(Map<String, String> cache) {
        Map<String,Object> model = new HashMap<>();
        model.put("module", config.getModule());
        model.put("caches", cache.entrySet());
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("preload-cache.html.ftl"), model);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("error processing template", e);
        }
    }

}
