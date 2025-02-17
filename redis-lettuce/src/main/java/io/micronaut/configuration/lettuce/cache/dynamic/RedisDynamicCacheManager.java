/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.configuration.lettuce.cache.dynamic;

import io.lettuce.core.api.StatefulConnection;
import io.micronaut.cache.DynamicCacheManager;
import io.micronaut.cache.SyncCache;
import io.micronaut.configuration.lettuce.RedisSetting;
import io.micronaut.configuration.lettuce.cache.DefaultRedisCacheConfiguration;
import io.micronaut.configuration.lettuce.cache.RedisCache;
import io.micronaut.configuration.lettuce.cache.RedisCacheConfiguration;
import io.micronaut.configuration.lettuce.cache.bypass.BypassDynamicCacheManager;
import io.micronaut.context.BeanLocator;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An implementation of {@link DynamicCacheManager} for to create caches without having a configuration entry.
 * By using default {@link DefaultRedisCacheConfiguration}.
 *
 * @author Armbruster Ferdinand
 * @since 6.7.1
 */
@Singleton
@Requires(classes = SyncCache.class, property = RedisSetting.PREFIX + ".enabled", defaultValue = StringUtils.TRUE, notEquals = StringUtils.FALSE)
@Requires(classes = SyncCache.class, property = RedisSetting.REDIS_DYNAMIC_CACHE + ".enabled", defaultValue = StringUtils.FALSE, notEquals = StringUtils.FALSE)
public class RedisDynamicCacheManager implements DynamicCacheManager<StatefulConnection<byte[], byte[]>> {

    private static final Logger LOG  = LoggerFactory.getLogger(BypassDynamicCacheManager.class);

    private ApplicationConfiguration applicationConfiguration;
    private DefaultRedisCacheConfiguration defaultRedisCacheConfiguration;
    private ConversionService conversionService;
    private BeanLocator beanLocator;

    public RedisDynamicCacheManager(
            ApplicationConfiguration applicationConfiguration,
            DefaultRedisCacheConfiguration defaultRedisCacheConfiguration,
            ConversionService conversionService,
            BeanLocator beanLocator
       ) {
        this.applicationConfiguration = applicationConfiguration;
        this.defaultRedisCacheConfiguration = defaultRedisCacheConfiguration;
        this.conversionService = conversionService;
        this.beanLocator = beanLocator;
    }

    /**
     * Creates a new dynamic redis cache for the given arguments.
     *
     * @param name                           The name of the dynamic cache
     */
    @Override
    public @NonNull SyncCache<StatefulConnection<byte[], byte[]>> getCache(String name) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Create DynamicRedisCache for {}", name);
        }

        return new RedisCache(
                defaultRedisCacheConfiguration,
                new RedisCacheConfiguration(name, applicationConfiguration),
                conversionService,
                beanLocator
        );
    }
}

