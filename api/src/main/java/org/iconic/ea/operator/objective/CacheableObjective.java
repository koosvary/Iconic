/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
