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
package org.iconic.project.search.config;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.reflection.ClassLoaderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a search
 */
@Log4j2
public class SearchConfigurationModelFactory {
    private final List<FunctionalPrimitive<Double, Double>> primitives;

    public SearchConfigurationModelFactory(final ClassLoaderService classLoaderService) {
        primitives = new ArrayList<>();

        final String primitivePackage = "org.iconic.ea.operator.primitive";
        final String primitiveSuper = "org.iconic.ea.operator.primitive.ArithmeticPrimitive";
        try {
            for (Class<?> clazz : classLoaderService.getSubclasses(primitivePackage, primitiveSuper)) {
                //noinspection unchecked
                primitives.add((FunctionalPrimitive<Double, Double>) clazz.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            log.warn("{}: {}", ex, ex.getStackTrace());
        }
    }

    public SearchConfigurationModel getSearchConfigurationModel(
            final String name,
            final EvolutionaryAlgorithmType type
    ) {
        switch (type) {
            case GENE_EXPRESSION_PROGRAMMING:
                return new GepConfigurationModel(name, primitives);
            case CARTESIAN_GENETIC_PROGRAMMING:
                return new CgpConfigurationModel(name, primitives);
            default:
                return new GepConfigurationModel(name, primitives);
        }
    }

    public List<FunctionalPrimitive<Double, Double>> getPrimitives() {
        return primitives;
    }
}
