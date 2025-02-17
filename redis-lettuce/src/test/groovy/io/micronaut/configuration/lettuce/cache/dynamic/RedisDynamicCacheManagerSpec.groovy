package io.micronaut.configuration.lettuce.cache.dynamic

import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.configuration.lettuce.RedisSpec
import io.micronaut.configuration.lettuce.cache.RedisCache
import io.micronaut.context.ApplicationContext
import io.micronaut.context.exceptions.NoSuchBeanException
import io.micronaut.redis.test.RedisContainerUtils

/**
 * @author Ferdinand Armbruster
 * @since 6.7.1
 */
class RedisDynamicCacheManagerSpec extends RedisSpec {

    ApplicationContext createApplicationContext(boolean redisEnabled = true, boolean dynamicEnabled = true) {
        ApplicationContext.run([
                "redis.enabled"              : redisEnabled,
                'redis.port'                 : RedisContainerUtils.getRedisPort(),
                "redis.cache.dynamic.enabled": dynamicEnabled
        ])
    }

    void "test RedisDynamicCacheManager should not be available if redis is deactivated"() {
        setup:
        ApplicationContext applicationContext = createApplicationContext(false, true)

        when:
        applicationContext.getBean(RedisDynamicCacheManager)

        then:
        thrown(NoSuchBeanException)
    }

    void "test RedisDynamicCacheManager should not be available if dynamic is deactivated"() {
        setup:
        ApplicationContext applicationContext = createApplicationContext(true, false)

        when:
        applicationContext.getBean(RedisDynamicCacheManager)

        then:
        thrown(NoSuchBeanException)
    }

    void "test creation of redisCache object for given name"() {
        setup:
        ApplicationContext applicationContext = createApplicationContext(true, true)

        when:
        RedisDynamicCacheManager redisDynamicCacheManager = applicationContext.getBean(RedisDynamicCacheManager)
        RedisCache redisCache = redisDynamicCacheManager.getCache("test")

        then:
        redisCache != null
        redisCache.getNativeCache() instanceof StatefulRedisConnection
    }

}
