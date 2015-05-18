package eu.hinsch.spring.angular.cache.test;

import eu.hinsch.spring.angular.cache.AngularRestCachePreloadTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Created by lh on 10/05/15.
 */
@SpringBootApplication
@Controller
public class TestApplication extends WebMvcConfigurerAdapter {
    @Autowired
    private AngularRestCachePreloadTransformer angularRestCachePreloadTransformer;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @RequestMapping(value = "/api/simple-list" /*, consumes = "application/json"*/, produces = "application/json")
    @ResponseBody
    public List<String> simpleList() {
        System.out.println("simpleList");
        return asList("One", "Two", "Three");
    }

    @RequestMapping(value = "/api/long-list", /* consumes = "application/json",*/ produces = "application/json")
    @ResponseBody
    public List<String> longList() {
        System.out.println("longList");
        return IntStream.range(0, 1000).mapToObj(String::valueOf).collect(toList());
    }

    @RequestMapping("/api/steps/{by}")
    @ResponseBody
    public List<String> steps(@PathVariable int by) {
        return IntStream.range(0, 10).map(i -> i * by).mapToObj(String::valueOf).collect(toList());
    }

    @RequestMapping("/api/split/{argument}")
    @ResponseBody
    public String[] split(@PathVariable String argument) {
        return argument.split("\\|");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html", "/")
                .addResourceLocations("classpath:/web/")
                .setCachePeriod(-1)
                .resourceChain(false)
                .addTransformer(angularRestCachePreloadTransformer);
    }
}
