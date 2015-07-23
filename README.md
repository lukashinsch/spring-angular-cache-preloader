[![Coverage Status](https://coveralls.io/repos/lukashinsch/spring-angular-cache-preloader/badge.svg?branch=master)](https://coveralls.io/r/lukashinsch/spring-angular-cache-preloader?branch=master)
[![Build Status](https://travis-ci.org/lukashinsch/spring-angular-cache-preloader.svg?branch=master)](https://travis-ci.org/lukashinsch/spring-angular-cache-preloader)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.hinsch/spring-angular-cache-preloader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.hinsch/spring-angular-cache-preloader/)


# spring-angular-cache-preloader
Component to pre-fill caches for angular js rest request though index page

# Overview

This component can be hooked into a Spring WebMVC application to tunnel REST requests that a single page app makes on first page load through the index page and prefill angular's $http cache with the values.

# How to use

## Gradle dependency
```
compile('eu.hinsch:spring-angular-cache-preloader:0.2.5')
```

## Maven dependency
```
<dependency>
  <groupId>eu.hinsch</groupId>
  <artifactId>spring-angular-cache-preloader</artifactId>
  <version>0.2.5</version>
</dependency>
```

## Configure resource transformer
```
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private AngularRestCachePreloadTransformer angularRestCachePreloadTransformer;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html", "/") // or whatever your welcome page is
                .addResourceLocations("classpath:/location-of-index/")
                .resourceChain(false)
                .addTransformer(angularRestCachePreloadTransformer);
    }
}
```

## Placeholder for generated script
Add ```{cachePreloadScript}``` in your index.html (right before the </body> end tag), this will be replaced by ```<script>...</script>```

## Turn on caching for request in angular
For example, if you are using ng-resource then you can turn on caching for a request by
```
$resource('some-url', {}, {
    query : {
        method : 'GET',
        cache : true,
        isArray: true
    }})
    .query();
```

## Configure urls to cache
In application.yml
```
cache-preload:
  # name of some existing angular module in your app that is being loaded
  angular-module: myapp
  
  # token in static resource to replace with script, defaults to {cachePreloadScript}
  placeholder: 'mytoken'
  
  cached-urls:
    # static url to be cached (must be the same as configured in $resource)
    - url: some-url
      # only cache if expression evaluates to true
      enabled: my-expression
    
    # dynamic url, where the parameter (likely) to be used is known/can be guessed by server
    - url: some-dynamic-url/{some-param}
      parameters:
        # any SpEL expression (with access to any bean)
        some-param: my-expression
```

## Example

See [test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java),
[test/resources/application.yml](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/resources/application.yml)
and [test/resources/web/index.html](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/resources/web/index.html) for details.
