package org.iconic.io;

import com.beust.jcommander.IStringConverter;
import org.iconic.ea.EvolutionaryAlgorithmType;

public class EvolutionaryAlgorithmTypeConverter implements IStringConverter<EvolutionaryAlgorithmType> {
    @Override
    public EvolutionaryAlgorithmType convert(String value) {
        return EvolutionaryAlgorithmType.valueOf(value);
    }
}