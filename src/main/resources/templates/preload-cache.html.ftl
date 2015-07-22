<script>
angular.module('${module}')
    .run(['$cacheFactory', '$http', function($cacheFactory, $http) {
        var httpCache = $cacheFactory.get('$http');
        <#list caches as cache>
        httpCache.put('${cache.key}', '${cache.value}');
        </#list>
    }]);
</script>