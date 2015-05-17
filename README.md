# spring-angular-cache-preloader
Component to pre-fill caches for angular js rest request though index page

# Overview

This component can be hooked into a Spring WebMVC application to tunnel REST requests that a single page app makes on first page load through the index page and prefill angular's $http cache with the values.

# How to use

## Gradle dependency
```
runtime('eu.hinsch:spring-angular-cache-preloader:0.2.0')
```

## Maven dependency
```
<dependency>
  <groupId>eu.hinsch</groupId>
  <artifactId>spring-angular-cache-preloader</artifactId>
  <version>0.2.0</version>
</dependency>
```

## Example

See [test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/java/eu/hinsch/spring/angular/cache/test/TestApplication.java)
and [test/resources/application.yml](https://github.com/lukashinsch/spring-angular-cache-preloader/blob/master/src/test/resources/application.yml) for details.