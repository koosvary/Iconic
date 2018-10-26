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

/**
 * Constructs a search
 */
public class SearchConfigurationModelFactory  {
    public SearchConfigurationModelFactory() {
        // Do nothing
    }

    public SearchConfigurationModel getSearchConfigurationModel(String name, EvolutionaryAlgorithmType type) {
        switch (type) {
            case GENE_EXPRESSION_PROGRAMMING: return new GepConfigurationModel(name);
            case CARTESIAN_GENETIC_PROGRAMMING: return new CgpConfigurationModel(name);
            default: return new GepConfigurationModel(name);
        }
    }
}
