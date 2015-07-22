# Changelog

## 0.2.5
- Do not throw exception on error but log and cache nothing (closes [#18](https://github.com/lukashinsch/spring-angular-cache-preloader/issues/18))
- Do not include script when all cached urls are disabled (closes [#14](https://github.com/lukashinsch/spring-angular-cache-preloader/issues/14))
- Remove dependency on angular-resource (closes [#17](https://github.com/lukashinsch/spring-angular-cache-preloader/issues/17))

## 0.2.4
- Conditional caching based on per url expression

## 0.2.3
- No longer ignore errors during request execution (closes [#5](https://github.com/lukashinsch/spring-angular-cache-preloader/issues/5))

## 0.2.2
- Url en/decoding now works both ways

## 0.2.1
- Handle url encoding

## 0.2.0
- Support parametrized urls with SpEL
- call DispatcherServlet internally to get cached values
- Simplified config properties (see test/resources/application.yml for details)

## 0.1.0
- First release