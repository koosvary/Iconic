package org.iconic;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.TreeChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.gep.GeneExpressionProgramming;
import org.iconic.ea.operator.primitive.DoubleAddition;
import org.iconic.ea.operator.primitive.DoubleProduct;
import org.iconic.io.ArgsConverterFactory;

import java.util.List;

@Log4j2
public class Client {
    private final JCommander argParser;
    private final ArgsConverterFactory args;

    public static void main(String[] args) {
        // Create a new client and parse the arguments passed in to the program
        final Client client = new Client();
        client.parse(args);

        // Check if the user included the help flag
        if (client.getArgs().isHelp()) {
            client.usage();
        }

        // Check if the user passed in an input file
        final String inputFile = client.getArgs().getInput();

        if (!"".equals(inputFile) && !inputFile.isEmpty()) {
            final DataManager<Double> dm = new DataManager<>(Double.class, inputFile);

            int featureSize = DataManager.getFeatureSize();
            int sampleSize = DataManager.getSampleSize();

            log.info("Feature Size: {}", () -> featureSize);
            log.info("Sample Size: {}", () -> sampleSize);

            // Create an evolutionary algorithm using Gene Expression Programming
            GeneExpressionProgramming<Double> gep = new GeneExpressionProgramming<>();
            // Add in the functions it can use
            gep.addFunctionalPrimitive(new DoubleAddition());
            gep.addFunctionalPrimitive(new DoubleProduct());

            log.info("Function Primitives used: {}", gep::getFunctionalPrimitives);

            gep.generateGenePool(client.getArgs().getPopulation());

            for (int i = 0; i < client.getArgs().getGenerations(); ++i) {
                List<TreeChromosome<Double>> oldPopulation = gep.getChromosomes();
                List<TreeChromosome<Double>> newPopulation = gep.evolve(oldPopulation);
                gep.setChromosomes(newPopulation);
            }
        }
    }

    private Client() {
        this.args = new ArgsConverterFactory();
        this.argParser = new JCommander.Builder().programName("Iconic CLI").addObject(this.args).build();
    }

    private void parse(final String[] args) {
        getArgParser().parse(args);
    }

    private void usage() {
        getArgParser().usage();
    }

    private JCommander getArgParser() {
        return argParser;
    }

    private ArgsConverterFactory getArgs() {
        return args;
    }
}