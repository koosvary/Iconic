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
import com.beust.jcommander.IStringConverterFactory;
import org.iconic.ea.EvolutionaryAlgorithmType;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EvolutionaryAlgorithmTypeConverterFactory implements IStringConverterFactory {
    @Override
    public Class<? extends IStringConverter<?>> getConverter(Class forType) {
        if (forType.equals(EvolutionaryAlgorithmType.class)) {
            return EvolutionaryAlgorithmTypeConverter.class;
        } else {
            return null;
        }
    }
}
