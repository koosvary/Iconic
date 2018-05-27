package org.iconic.ea.data;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class DataManager<T> {
    private final Class<T> clazz;
    private static String fileName;
    private List<List<T>> samples, originalSamples;
    private static ArrayList<String> sampleHeader;
    private static ArrayList<String> sampleDescription;
    private static int featureSize;
    private static int sampleSize;

    public DataManager(Class<T> clazz, String fileName) {
        this.clazz = clazz;
        DataManager.fileName = fileName;

        try {
            importData(fileName);
        } catch (IOException ex) {
            log.error("Bad File: {}", () -> fileName);
            log.error("Exception: {}", ex);
        }
    }

    private void importData(String fileName) throws IOException {
        DataManager.fileName = fileName;
        log.traceEntry();
        featureSize = 0;
        sampleSize = 0;
        boolean headerRow = true;

        // Check if the file is on the classpath, otherwise check outside
        InputStream resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);

        BufferedReader reader = (resource == null)
                ? new BufferedReader(new FileReader(fileName))
                : new BufferedReader(new InputStreamReader(resource));

        samples = new ArrayList<>();
        originalSamples = new ArrayList<>();
        sampleHeader = new ArrayList<>();

        // Sometimes data is given as a String like "Boy, Girl", this ArrayList keeps track of all the strings and returns the
        // index value of a string variable. This will ensure all data is in a numerical format.
        ArrayList<String> stringValues = new ArrayList<>();

        Scanner sc = new Scanner(reader);

        // Scan through the input file one line a time
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // Ensure the line isn't blank
            if (!"".equals(line)) {
                // Split the line values separated by a ","
                String[] values = line.split(",");

                // Track the number of features
                if (featureSize == 0) {
                    featureSize = values.length;
                }

                // currentValues holds all the collected values in a double format
                ArrayList<T> currentValues = new ArrayList<>();

                // Loop through all the collected values
                for (String value : values) {
                    if (headerRow) {
                        sampleHeader.add(value);
                    } else {
                        // If the value is already in a double format then instantly add it into the array
                        try {
                            if (clazz.isAssignableFrom(String.class)) {
                                currentValues.add((T) (value));
                            } else if (clazz.isAssignableFrom(Integer.class)) {
                                currentValues.add((T) Integer.valueOf(value));
                            } else if (clazz.isAssignableFrom(Boolean.class)) {
                                currentValues.add((T) Boolean.valueOf(value));
                            } else if (clazz.isAssignableFrom(Double.class)) {
                                currentValues.add((T) Double.valueOf(value));
                            } else {
                                throw new IllegalArgumentException("Bad type.");
                            }
                        }
                        // If the value collected is a string value then...
                        catch (NumberFormatException nfe) {
                            log.error("DataManager importData  Trying to cast the input to <T>");
                        }
                    }
                }

                if (headerRow)
                    headerRow = false;

                // At the end of each line being read in, add that array into the ArrayList for storage
                if (!currentValues.isEmpty()) {
                    // Track the sample size
                    sampleSize++;
                    samples.add(currentValues);
                    originalSamples.add((List<T>)currentValues.clone());
                }
            }
        }
        sc.close();
        log.info("DataManager importData - Successfully Imported Dataset");
    }

    public List<List<T>> getSamples() {
        return samples;
    }

    public List<T> getSampleRow(int row) {
        return samples.get(row);
    }

    public List<T> getSampleColumn(int column) {
        List<T> values = new ArrayList<>();

        for (List<T> row : samples)
            values.add(row.get(column));

        return values;
    }

    public T getInputVariable(int sample, int index) {
        return samples.get(sample).get(index);
    }

    public int getFeatureSize() {
        return featureSize - 1;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Loop through all samples and reset the current value back to the origninal.
     * Used when preprocessing methods are deactivated.
     *
     * @param column
     */
    public void resetSampleColumn(int column) {
        for (int i = 0; i < sampleSize; i++)
            samples.get(i).set(column, originalSamples.get(i).get(column));
    }

    public ArrayList<String> getSampleHeaders() { return sampleHeader; }

    public void setSampleColumn(int column, List<T> values) {
        for (int i = 0; i < sampleSize; i++) {
            T value = values.get(i);
            samples.get(i).set(column, value);
        }
    }
}