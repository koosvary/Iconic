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
package org.iconic.reflection;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import javax.inject.Singleton;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Singleton
public class ClassGraphClassLoaderService implements ClassLoaderService {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> getClasses(final String pkg) {
        try (final ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .whitelistPackages(pkg)
                .scan()
        ) {
            return scanResult.getAllStandardClasses().loadClasses();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> getSubclasses(final String pkg, final String superclass) {
        try (final ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .whitelistPackages(pkg)
                .scan()
        ) {
            return scanResult.getSubclasses(superclass).loadClasses();
        }
    }
}
