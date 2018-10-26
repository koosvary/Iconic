/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.ea.operator.objective;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 * <p>
 * An objective that caches its results.
 */
@Log4j2
public class CacheableObjective<T extends Comparable<T>>  implements Objective<T> {
    private final LoadingCache<Chromosome<T>, Double> cache;
    private final Objective<T> objective;

    public CacheableObjective(final Objective<T> objective) {
        this.objective = objective;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Chromosome<T>, Double>() {
                            public Double load(Chromosome<T> key) {
                                return getObjective().apply(key);
                            }
                        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<T> c) {
        return getCache().getUnchecked(c);
    }

    /**
     * Returns the underlying objective used by the decorator.
     *
     * @return The underlying objective used by this deccrator.
     */
    public Objective<T> getObjective() {
        return objective;
    }

    /**
     * Returns the cache of results that were previously computed by the objective.
     *
     * @return The cache of results that were previously computed by this objective.
     */
    private LoadingCache<Chromosome<T>, Double> getCache() {
        return cache;
    }
}
