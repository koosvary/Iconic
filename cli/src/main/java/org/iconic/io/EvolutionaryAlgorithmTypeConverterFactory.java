package org.iconic.io;

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
