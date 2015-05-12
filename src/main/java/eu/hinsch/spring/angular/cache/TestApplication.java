package eu.hinsch.spring.angular.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by lh on 10/05/15.
 */
@SpringBootApplication
@Controller
public class TestApplication extends WebMvcConfigurerAdapter {
    @Autowired
    private AngularRestCachePrefillTransformer angularRestCachePrefillTransformer;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @RequestMapping("/api/simple-list")
    @ResponseBody
    public List<String> simpleList() {
        System.out.println("simpleList");
        return asList("One", "Two", "Three");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html", "/")
                .addResourceLocations("classpath:/web/")
                .setCachePeriod(-1)
                .resourceChain(false)
                .addTransformer(angularRestCachePrefillTransformer);
    }
}
