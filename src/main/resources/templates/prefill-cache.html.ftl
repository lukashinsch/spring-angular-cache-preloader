<script>
angular.module('${module}')
    .run(function($cacheFactory, $resource) {
        var httpCache = $cacheFactory.get('$http');
        <#list caches as cache>
        httpCache.put('${cache.key}', '${cache.value}');
        </#list>
    });
</script>