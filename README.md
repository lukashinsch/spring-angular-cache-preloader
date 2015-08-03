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
  
  # map of headers to be used on the internal request
  headers:
    Accept: application/json
    Content-Type: application/json
    X-My-Header: my-value
  
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

## Request headers
By default the original http request made by the browser to retrieve the index page is 
re-used to "fake" the internal request to the REST endpoint changing only the URL.
That way all headers sent by the browser (esp. authentication and cookies) are preserved.
However, this differs slightly from the real AJAX request that would be made as some of the headers would be different (e.g. "Accept"). 
Also, any headers programmatically set by a javascript client would not be present.
For this reason, a map of headers used by the internal request can be specified in the environment configuration (see example).
Note that currently only those headers are supported that are retrieved from the Request object via the getHeader, getHeaders, getIntHeader or getContentType methods. Cookies cannot currently be set.

## Example

See [test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java),
[test/resources/application.yml](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/resources/application.yml)
and [test/resources/web/index.html](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/resources/web/index.html) for details.
