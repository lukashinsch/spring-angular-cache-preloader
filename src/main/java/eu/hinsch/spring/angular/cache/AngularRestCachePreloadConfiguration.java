package eu.hinsch.spring.angular.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by lh on 14/05/15.
 */
@Component
@ConfigurationProperties(prefix = "cache.preload")
public class AngularRestCachePreloadConfiguration {

    private List<String> urls;

    private String placeholder = "{cachePreloadScript}";

    @NotNull
    private String module;

    private List<ParameterizedUrl> parameterizedUrls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<ParameterizedUrl> getParameterizedUrls() {
        return parameterizedUrls;
    }

    public void setParameterizedUrls(List<ParameterizedUrl> parameterizedUrls) {
        this.parameterizedUrls = parameterizedUrls;
    }

    public static class ParameterizedUrl {
        @NotNull
        private String url;
        @NotNull
        private Map<String,String> parameters;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }
    }
}
