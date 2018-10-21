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

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * <p>
 * A SizeObjective is an objective function that simply returns the chromosome's size.
 */
@Log4j2
public class SizeObjective extends MonoObjective<Double> {
    /**
     * Constructs a new SizeObjective.
     */
    public SizeObjective() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<Double> c) {
        return c.getSize();
    }
}
