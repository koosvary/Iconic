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
package org.iconic.io.cli;

import com.beust.jcommander.IStringConverter;
import io.github.classgraph.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithmType;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class PrimitiveTypeConverter implements IStringConverter<FunctionalPrimitive<Double, Double>> {
    @Getter
    final private Map<String, FunctionalPrimitive<Double, Double>> primitives;

    @SuppressWarnings("unchecked")
    public PrimitiveTypeConverter() {
        super();
        primitives = new LinkedHashMap<>();
        final String pkg = "org.iconic.ea.operator.primitive";
        final String superclass = pkg + ".ArithmeticPrimitive";
        final String[] exclude = new String[]{
                superclass
        };

        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages(pkg)
                             .blacklistClasses(exclude)
                             .scan()
        ) {
            for (ClassInfo classInfo : scanResult.getSubclasses(superclass)) {
                FunctionalPrimitive<Double, Double> primitive =
                        (FunctionalPrimitive<Double, Double>) classInfo.loadClass().newInstance();
                primitives.put(primitive.getSymbol(), primitive);
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            log.error("{}: {}", ex::getMessage, ex::getCause);
        }
    }

    @Override
    public FunctionalPrimitive<Double, Double> convert(String value) {
        return primitives.get(value);
    }
}