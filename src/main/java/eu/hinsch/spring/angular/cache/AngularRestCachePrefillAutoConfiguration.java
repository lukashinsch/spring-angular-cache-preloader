package eu.hinsch.spring.angular.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by lh on 14/05/15.
 */
@Configuration
public class AngularRestCachePrefillAutoConfiguration {

    @Bean
    public AngularRestCachePrefillTransformer angularRestCachePrefillTransformer(
            final RequestMappingHandlerMapping requestMappingHandlerMapping,
            final ApplicationContext applicationContext,
            final RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            final Environment environment) {
        return new AngularRestCachePrefillTransformer(requestMappingHandlerMapping,
                applicationContext,
                requestMappingHandlerAdapter,
                environment);
    }
}
