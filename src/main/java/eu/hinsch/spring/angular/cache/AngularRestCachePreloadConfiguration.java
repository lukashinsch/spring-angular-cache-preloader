package eu.hinsch.spring.angular.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lh on 14/05/15.
 */
@Component
@ConfigurationProperties(prefix = "cache-preload")
public class AngularRestCachePreloadConfiguration {

    /**
     * placeholder used to inject script into index.html
     */
    private String placeholder = "{cachePreloadScript}";

    /**
     * name of an angular module that is being loaded by the main application.
     * Needed to access angular $cacheFactory
     */
    @NotNull
    private String angularModule;

    @Size(min = 1)
    private List<CachedUrl> cachedUrls;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getAngularModule() {
        return angularModule;
    }

    public void setAngularModule(String angularModule) {
        this.angularModule = angularModule;
    }

    public List<CachedUrl> getCachedUrls() {
        return cachedUrls;
    }

    public void setCachedUrls(List<CachedUrl> cachedUrls) {
        this.cachedUrls = cachedUrls;
    }

    public static class CachedUrl {

        public CachedUrl() {
        }

        public CachedUrl(final String url, Map<String,String> parameters) {
            this.url = url;
            this.parameters = parameters;
        }

        public CachedUrl(String url) {
            this.url = url;
        }

        @NotNull
        private String url;

        private Map<String,String> parameters = new HashMap<>();

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }
    }
}
