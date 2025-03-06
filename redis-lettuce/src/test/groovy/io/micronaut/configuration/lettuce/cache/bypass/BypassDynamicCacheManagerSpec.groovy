package io.micronaut.configuration.lettuce.cache.bypass


import io.micronaut.configuration.lettuce.RedisSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.context.exceptions.NoSuchBeanException

/**
 * @author Ferdinand Armbruster
 *
 * @since 6.7.1
 */
class BypassDynamicCacheManagerSpec extends RedisSpec {


    ApplicationContext createApplicationContext(Boolean enabled) {
        ApplicationContext.run(["redis.enabled": enabled])
    }


    void "test BypassDynamicCacheManager should not be available if redis is active"() {
        setup:
        ApplicationContext applicationContext = createApplicationContext(true)

        when:
        applicationContext.getBean(BypassDynamicCacheManager)

        then:
        thrown(NoSuchBeanException)
    }

    void "test bypassCache"() {
        setup:
        ApplicationContext applicationContext = createApplicationContext(false)

        when:
        BypassDynamicCacheManager bypassDynamicCacheManager = applicationContext.getBean(BypassDynamicCacheManager)
        BypassDynamicCacheManager.BypassCache bypassCache = bypassDynamicCacheManager.getCache("test")

        then:
        bypassDynamicCacheManager != null
        bypassCache != null
    }

}
