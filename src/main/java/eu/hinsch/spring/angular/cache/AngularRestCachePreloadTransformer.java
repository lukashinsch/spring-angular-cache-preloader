package eu.hinsch.spring.angular.cache;

import eu.hinsch.spring.angular.cache.AngularRestCachePreloadConfiguration.CachedUrl;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.ResourceTransformerSupport;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukas.hinsch on 08.05.2015.
 */
@Component
public class AngularRestCachePreloadTransformer extends ResourceTransformerSupport{

    private static final String DEFAULT_ENCODING = "UTF-8";

    private final AngularRestCachePreloadConfiguration config;
    private final DispatcherServlet dispatcherServlet;
    private final Configuration freemarkerConfig;
    private final SpelExpressionParser expressionParser;
    private final StandardEvaluationContext evaluationContext;

    @Autowired
    public AngularRestCachePreloadTransformer(final AngularRestCachePreloadConfiguration config,
                                              final DispatcherServlet dispatcherServlet,
                                              final BeanFactory beanFactory) {
        this.config = config;
        this.dispatcherServlet = dispatcherServlet;

        expressionParser = new SpelExpressionParser();
        evaluationContext = new StandardEvaluationContext();
        evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));

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

        return new TransformedResource(transformedResource, content.getBytes(DEFAULT_ENCODING));
    }

    private Map<String, String> createCache(final HttpServletRequest request) {
        Map<String,String> cache = new HashMap<>();

        config.getCachedUrls()
                .stream()
                .map(this::replaceParameters)
                .forEach(url -> doRequestAndAddToCache(request, cache, url));

        return cache;
    }

    private String replaceParameters(final CachedUrl cachedUrl) {
        String url = cachedUrl.getUrl();
        for (Map.Entry<String, String> parameter : cachedUrl.getParameters().entrySet()) {
            url = url.replace("{" + parameter.getKey() + "}", getParameterValue(parameter.getValue()));
        }
        return url;
    }

    private String getParameterValue(final String value) {
        Expression expression = expressionParser.parseExpression(value);
        final String result = String.valueOf(expression.getValue(evaluationContext));
        try {
            return URLEncoder.encode(result, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding error", e);
        }
    }

    private void doRequestAndAddToCache(final HttpServletRequest request, final Map<String, String> cache, final String url) {
        ContentBufferingResponse response = new ContentBufferingResponse();
        try {
            dispatcherServlet.service(new UrlRewritingRequestWrapper(request, urlDecode(url)), response);
            String controllerResponse = response.getResponseContent();
            cache.put(url, controllerResponse);
        } catch (Exception e) {
            throw new RuntimeException("error caching request " + url, e);
        }
    }

    private String urlDecode(final String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, DEFAULT_ENCODING);
    }

    private String createScript(final Map<String, String> cache) {
        Map<String,Object> model = new HashMap<>();
        model.put("module", config.getAngularModule());
        model.put("caches", cache.entrySet());
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("preload-cache.html.ftl"), model);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("error processing template", e);
        }
    }

}
