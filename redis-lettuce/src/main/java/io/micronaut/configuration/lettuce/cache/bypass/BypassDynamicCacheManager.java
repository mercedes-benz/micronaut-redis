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
package io.micronaut.configuration.lettuce.cache.bypass;

import io.micronaut.cache.DynamicCacheManager;
import io.micronaut.cache.SyncCache;
import io.micronaut.configuration.lettuce.RedisSetting;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * An implementation of {@link DynamicCacheManager} to bypass the caching mechanism, if redis is deactivated.
 *
 * @author Armbruster Ferdinand
 * @since 6.7.1
 */
@Singleton
@Requires(classes = SyncCache.class, property = RedisSetting.PREFIX + ".enabled", defaultValue = StringUtils.TRUE, notEquals = StringUtils.TRUE)
public class BypassDynamicCacheManager implements DynamicCacheManager<Object> {

    private static final Logger LOG  = LoggerFactory.getLogger(BypassDynamicCacheManager.class);

    /**
     * Creates a new bypass cache for the given arguments.
     *
     * @param name                           The name of the dynamic cache
     */
    @Override
    public @NonNull SyncCache<Object> getCache(String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create BypassCache for {}", name);
        }
        return new BypassCache(name);
    }

    /**
     * An implementation of {@link SyncCache} to bypass the caching mechanism.
     *
     * @author Armbruster Ferdinand
     * @since 6.7.1
     */
    protected class BypassCache implements SyncCache<Object> {

        private final String name;

        public BypassCache(String name) {
            this.name = name;
        }

        @Override
        public @NonNull <T> Optional<T> get(@NonNull Object key, @NonNull Argument<T> requiredType) {
            return Optional.empty();
        }

        @Override
        public <T> T get(@NonNull Object key, @NonNull Argument<T> requiredType, @NonNull Supplier<T> supplier) {
            return null;
        }

        @Override
        public @NonNull <T> Optional<T> putIfAbsent(@NonNull Object key, @NonNull T value) {
            return Optional.empty();
        }

        @Override
        public void put(@NonNull Object key, @NonNull Object value) { }

        @Override
        public void invalidate(@NonNull Object key) { }

        @Override
        public void invalidateAll() { }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return null;
        }
    }

}

