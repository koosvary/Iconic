package org.iconic;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.iconic.io.ArgsConverterFactory;

@Log4j2
public class Client {
    private final JCommander argParser;
    private final ArgsConverterFactory args;

    public static void main(String[] args) {
        final Client client = new Client();
        client.parse(args);
        client.getArgs().getEaType();

        if (client.getArgs().isHelp()) {
            client.getArgParser().usage();
        }
    }

    private Client() {
        this.args = new ArgsConverterFactory();
        this.argParser = new JCommander.Builder().programName("Iconic CLI").addObject(this.args).build();
    }

    private void parse(final String[] args) {
        getArgParser().parse(args);
    }

    private JCommander getArgParser() {
        return argParser;
    }

    private ArgsConverterFactory getArgs() {
        return args;
    }
}