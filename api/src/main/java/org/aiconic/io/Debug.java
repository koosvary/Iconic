package org.aiconic.io;

/**
 * A utility class for printing debug statements.
 */
public class Debug {

    /**
     * Prints the provided output to the standard error outputstream
     * if the debug flag is set.
     * <p>
     * Example configuration:
     * <pre>{@code
     * java -Ddebug="true" my.jar %1
     * }
     * </pre>
     * @param      output   The output to print
     */
    public static void out(String output) {
        if (Boolean.getBoolean("debug")) {
            System.err.println(out);
        }
    }
}