package org.iconic.ea.data;

// import lombok.extern.log4j.log4j2;

import java.io.*;
import java.util.*;

// @log4j2
public class DataManager<T> {
    private String fileName;
    private String[] sampleHeader;
    private String[] expectedOutputHeaders;
    private HashMap<String, FeatureClass<Double>> dataset;
    private int featureSize;
    private int sampleSize;
    private boolean containsHeader = true;

    public DataManager(String fileName) {
        this.fileName = fileName;
        expectedOutputHeaders = new String[0];

        try {
            importData(this.fileName);
        } catch (IOException ex) {
            // log.error("Bad File: {}", () -> fileName);
            // log.error("Exception: {}", ex);
        }
    }

    private void importData(String fileName) throws IOException {
        this.fileName = fileName;
        sampleSize = 0;
        dataset = new HashMap<>();

        // log.traceEntry();

        // Check if the file is on the classpath, otherwise check outside
        InputStream resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);

        BufferedReader reader = (resource == null)
                ? new BufferedReader(new FileReader(fileName))
                : new BufferedReader(new InputStreamReader(resource));


        Scanner sc = new Scanner(reader);

        // Check the file isn't empty
        if (!sc.hasNextLine()) {
            // log.error("The data file is empty");
            return;
        }

        // Get the first line from the datafile
        String line = getNextLineFromDataFile(sc);

        if (containsHeader) {
            // Assume the delimiter is a comma
            String[] headers = line.split(",");

            // Update the headers
            sampleHeader = headers;

            // Update the feature size
            featureSize = sampleHeader.length;

            // Set the last column by default as the expected output
            expectedOutputHeaders = new String[]{headers[featureSize - 1]};

            // Read in the next line for later (Needed because the
            // !containsHeader route already reads in the next line)
            line = getNextLineFromDataFile(sc);
        } else {
            // Generate all the header names such as: A, B, C, ..., Z, AA, BB, etc
            String[] sampleValues = line.split(",");

            // Update the feature size
            featureSize = sampleValues.length;

            // Set the Header size
            String[] headers = new String[featureSize];

            // Generate the header names
            for (int i = 0; i < featureSize; i++) {
                headers[i] = intToHeader(i);
            }

            // Update the headers
            sampleHeader = headers;
        }

        // Create the array
        FeatureClass<Double>[] featureClasses = new FeatureClass[featureSize];

        for (int i = 0; i < featureClasses.length; i++) {
            featureClasses[i] = new FeatureClass<>();
        }

        // Scan through the input file one line a time
        do {
            if (line == null)
                break;

            sampleSize++;

            // Assume the delimiter is a comma
            String[] values = line.split(",");

            // Parse the string values to a double and add to FeatureClass
            for (int i = 0; i < values.length; i++) {
                Double value = Double.parseDouble(values[i]);
                featureClasses[i].addSampleValue(value);
            }

            line = getNextLineFromDataFile(sc);
        } while (line != null);

        // Add all the feature classes to the hashmap
        for (int i = 0; i < featureSize; i++) {
            dataset.put(sampleHeader[i], featureClasses[i]);
        }

        sc.close();
        // log.info("DataManager importData - Successfully Imported Dataset");
    }

    private String getNextLineFromDataFile(Scanner sc) {
        if (!sc.hasNextLine())
            return null;

        // Read in the next line of the file
        String line = sc.nextLine();

        // While there are comments or empty lines in the file, read next line
        while(line.startsWith("#") || line.equals("")) {
            if (sc.hasNextLine()) {
                line = sc.nextLine();
            } else {
                // log.error("The data file is empty");
                return null;
            }
        }

        return line;
    }

    // Takes an int value and converts it into the excel format for a header
    // Example (0 = A, 1 = B, 26 = AA, 27 = AB)
    public String intToHeader(int num) {
        String name = "";
        do {
            char letter = (char) (65 + num % 26);
            name = letter + name;
            if (num < 26)
                break;
            num /= 26;
            num -= 1;
        } while (num >= 0);
        return name;
    }

    public void applyPreProcessing() {
        /* Display content using Iterator */
        Set set = dataset.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();

            FeatureClass featureClass = (FeatureClass) mentry.getValue();

            featureClass.applyPreProcessing();
        }
    }

    public HashMap<String, FeatureClass<Double>> getDataset() {
        return dataset;
    }

    public List<T> getSampleRow(int row) {
        return null;
    }

    public ArrayList<T> getSampleColumn(int column) {
        String columnName = sampleHeader[column];

        return getSampleColumn(columnName);
    }

    public ArrayList<T> getSampleColumn(String columnName) {
        FeatureClass featureClass = dataset.get(columnName);

        return featureClass.getSamples();
    }

    public Double getSampleVariable(String headerName, int row) {
        return dataset.get(headerName).getSampleValue(row);
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public String[] getSampleHeaders() { return sampleHeader; }
}