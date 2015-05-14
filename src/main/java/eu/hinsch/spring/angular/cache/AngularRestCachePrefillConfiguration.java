package eu.hinsch.spring.angular.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by lh on 14/05/15.
 */
@Component
@ConfigurationProperties(prefix = "cache.preload")
public class AngularRestCachePrefillConfiguration {

    private List<String> urls;

    private String placeholder = "{cachePreloadScript}";

    @NotNull
    private String module;

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
}
