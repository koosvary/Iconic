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
package org.iconic.ea.operator.evolutionary.selection;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.iconic.ea.chromosome.Chromosome;

import java.util.List;

public class RandomUniformSelector<T extends Chromosome<?>> implements Selector<T> {
    @Override
    public T apply(final List<T> population) {
        assert (population.size() >= 1);
        //probability distribution to select the node to mutate
        UniformIntegerDistribution distribution = new UniformIntegerDistribution(0, population.size() - 1);
        return population.get(distribution.sample());
    }
}
