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
    private List<ArrayList<T>> samples;
    private static ArrayList<String> sampleHeader;
    private static ArrayList<String> sampleDescription;
    private static int featureSize;
    private static int sampleSize;

    public DataManager(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DataManager(Class<T> clazz, String fileName) {
        this.clazz = clazz;
        DataManager.fileName = fileName;

        try {
            importData(fileName);
        } catch (IOException e) {
            log.error("File not found: {}", () -> fileName);
        }
    }

    public void importData(String fileName) throws IOException {
        DataManager.fileName = fileName;
        log.traceEntry();
        featureSize = 0;
        sampleSize = 0;

        // Check if the file is on the classpath, otherwise check outside
        InputStream resource = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));

        if (resource.available() <= 0) {
            return;
        }

        samples = new ArrayList<>();

        // Sometimes data is given as a String like "Boy, Girl", this ArrayList keeps track of all the strings and returns the
        // index value of a string variable. This will ensure all data is in a numerical format.
        ArrayList<String> stringValues = new ArrayList<>();

        Scanner sc = new Scanner(reader);

        // Scan through the input file one line a time
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // Ensure the line isn't blank
            if (!"".equals(line)) {
                // Track the sample size
                sampleSize++;

                // Split the line values separated by a ","
                String[] values = line.split(",");

                // Track the number of features
                if (featureSize == 0)
                    featureSize = values.length;

                // currentValues holds all the collected values in a double format
                ArrayList<T> currentValues = new ArrayList<>();

                // Loop through all the collected values
                for (int i = 0; i < values.length; i++) {
                    // If the value is already in a double format then instantly add it into the array
                    try {
                        if (clazz.isAssignableFrom(String.class)) {
                            currentValues.add((T) (values[i]));
                        } else if (clazz.isAssignableFrom(Integer.class)) {
                            currentValues.add((T) Integer.valueOf(values[i]));
                        } else if (clazz.isAssignableFrom(Boolean.class)) {
                            currentValues.add((T) Boolean.valueOf(values[i]));
                        } else if (clazz.isAssignableFrom(Double.class)) {
                            currentValues.add((T) Double.valueOf(values[i]));
                        } else {
                            throw new IllegalArgumentException("Bad type.");
                        }
                    }
                    // If the value collected is a string value then...
                    catch (NumberFormatException nfe) {
                        System.out.println("DataManager importData  Trying to cast the input to <T>");
                    }
                }

                // At the end of each line being read in, add that array into the ArrayList for storage
                samples.add(currentValues);
            }
        }
        sc.close();
    }

    public List<T> getSampleRow(int row) {
        return samples.get(row);
    }

    public T getInputVariable(int sample, int index) {
        return samples.get(sample).get(index);
    }

    public static int getFeatureSize() {
        return featureSize;
    }

    public static int getSampleSize() {
        return sampleSize;
    }
}