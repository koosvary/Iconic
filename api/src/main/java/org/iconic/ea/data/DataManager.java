package org.iconic.ea.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        this.fileName = fileName;
        importData(fileName);
    }

    public void importData(String fileName) {
        this.fileName = fileName;
        System.out.println("DataManager importData");
        //logger.traceEntry();
        featureSize = 0;
        sampleSize = 0;

        // Check if the file is on the classpath, otherwise check outside
        URL resource = getClass().getClassLoader().getResource(fileName);
        File file = (resource != null) ? new File(resource.getFile()) : new File(fileName);

        // This ArrayList holds all the data collected from the file
        samples = new ArrayList<>();

        // Sometimes data is given as a String like "Boy, Girl", this ArrayList keeps track of all the strings and returns the
        // index value of a string variable. This will ensure all data is in a numerical format.
        ArrayList<String> stringValues = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

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
                                currentValues.add((T)(values[i]));
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
        } catch (FileNotFoundException e) {
        }
    }

    public List<T> getSampleRow(int row) { return samples.get(row); }

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