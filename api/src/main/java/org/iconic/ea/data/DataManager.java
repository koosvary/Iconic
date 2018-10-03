/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.ea.data;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.*;

@Log4j2
/**
 * TODO: generify this - DataManager => NumericDataManager / StringDataManager / PictureDataManager
 */
public class DataManager<T> {

    private String fileName;
    private List<String> sampleHeaders;
    private List<String> expectedOutputHeaders;
    private HashMap<String, FeatureClass<Number>> dataset;
    private int featureSize;
    private int sampleSize;
    private boolean containsHeader = false;

    public DataManager(){
        expectedOutputHeaders = new ArrayList<>();
        sampleHeaders = new ArrayList<>();
        createNewDataset();
    }

    public DataManager(String fileName) {
        this.fileName = fileName;
        expectedOutputHeaders = new ArrayList<>();
        sampleHeaders = new ArrayList<>();

        try {
            importData(this.fileName);
        } catch (IOException ex) {
            log.error("Bad File: {}", () -> fileName);
            log.error("Exception: {}", ex);
        }
    }

    public void saveDatasetToFile(File fileName) throws IOException {
        FileWriter fileWriter = null;

        try{
            fileWriter = new FileWriter(fileName);
            if(containsHeader){
                for(int j = 0; j < featureSize-1; j ++) {
                    fileWriter.append(String.valueOf(sampleHeaders.get(j)));
                    fileWriter.append(",");
                }
                fileWriter.append(String.valueOf(sampleHeaders.get(featureSize-1)));
                fileWriter.append(System.getProperty("line.separator"));
            }
            for(int i = 0; i < sampleSize; i++){
                List<Number> currentRow = getSampleRow(i);
                for(int j = 0; j < currentRow.size()-1; j ++) {
                    fileWriter.append(String.valueOf(currentRow.get(j)));
                    fileWriter.append(",");
                }
                fileWriter.append(String.valueOf(currentRow.get(currentRow.size()-1)));
                fileWriter.append(System.getProperty("line.separator"));
            }
        } catch (Exception ex){
            log.error("Error when saving file. File: {}", () -> fileName);
            log.error("Exception: {}", ex);
        } finally{
            try{
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex){
                log.error("Error when closing FileWriter. File: {}", () -> fileName);
                log.error("Exception: {}", ex);
            }
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
            log.error("The input file is empty");
            return;
        }

        // Get the first line from the datafile
        String line = getNextLineFromDataFile(sc);

        // Assume the delimiter is a comma, and set feature size
        String[] split = line.split(",");
        featureSize = split.length;

        // Try to determine if the datafile contains a header row
        for (String header : split) {
            try {
                Double.parseDouble(header);
            } catch (NumberFormatException e) {
                containsHeader = true;
                break;
            }
        }

        if (containsHeader) {
            // Update the headers
            Collections.addAll(sampleHeaders, split);

            // Read in the next line for later (needed because the `else` block already reads in the next line)
            line = getNextLineFromDataFile(sc);
        } else {
            // Generate all the header names such as: A, B, C, ..., Z, AA, BB, etc
            for (int i = 0; i < featureSize; i++) {
                sampleHeaders.add(intToHeader(i));
            }
            containsHeader = true;
        }

        // Set the last column by default as the expected output
        expectedOutputHeaders.add(sampleHeaders.get(featureSize - 1));

        // Create a list of all features
        ArrayList<FeatureClass<Number>> featureClasses = new ArrayList<>(featureSize);

        for (String aSampleHeader : sampleHeaders) {
            if (expectedOutputHeaders.contains(aSampleHeader)) {
                featureClasses.add(new NumericFeatureClass(true));
            } else {
                featureClasses.add(new NumericFeatureClass(false));
            }
        }

        // Scan through the input file one line a time
        do {
            if (line == null) {
                break;
            }

            sampleSize++;

            // Assume the delimiter is a comma
            String[] values = line.split(",");

            // Parse the string values to a double and add to FeatureClass
            for (int i = 0; i < values.length; i++) {
                Double value = Double.parseDouble(values[i]);
                featureClasses.get(i).addSampleValue(value);
            }

            line = getNextLineFromDataFile(sc);
        } while (line != null);

        // Add all the feature classes to the map
        for (int i = 0; i < featureSize; i++) {
            dataset.put(sampleHeaders.get(i), featureClasses.get(i));
        }

        sc.close();
        // log.info("Successfully Imported Dataset");
    }

    public void addNewFeature(String sampleHeader, List<Number> feature){
        sampleHeaders.add(sampleHeader);
        expectedOutputHeaders.add(sampleHeader);
        FeatureClass<Number> featureClass = new NumericFeatureClass(true);
        for (Number number : feature) {
            featureClass.addSampleValue(number);
        }
        dataset.put(sampleHeader,featureClass);
        featureSize++;
    }

    private void createNewDataset(){
        sampleSize = 0;
        featureSize = 26;
        dataset = new HashMap<>();

        // Generate all the header names such as: A, B, C, ..., Z, AA, BB, etc
        for (int i = 0; i < featureSize; i++) {
            sampleHeaders.add(intToHeader(i));
        }
        containsHeader = true;

        // Set the last column by default as the expected output
        expectedOutputHeaders.add(sampleHeaders.get(featureSize - 1));

        // Create a list of all features
        ArrayList<FeatureClass<Number>> featureClasses = new ArrayList<>(featureSize);

        for (String aSampleHeader : sampleHeaders) {
            if (expectedOutputHeaders.contains(aSampleHeader)) {
                featureClasses.add(new NumericFeatureClass(true));
            } else {
                featureClasses.add(new NumericFeatureClass(false));
            }
        }

        // Add all the feature classes to the map
        for (int i = 0; i < featureSize; i++) {
            dataset.put(sampleHeaders.get(i), featureClasses.get(i));
        }
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
        StringBuilder name = new StringBuilder();
        do {
            char letter = (char) (65 + num % 26);
            name.insert(0, letter);
            if (num < 26)
                break;
            num /= 26;
            num -= 1;
        } while (num >= 0);
        return name.toString();
    }

    public HashMap<String, FeatureClass<Number>> getDataset() {
        return dataset;
    }

    public List<Number> getSampleRow(int row) {
        List<Number> samples = new ArrayList<>();

        for (String header : sampleHeaders) {
            FeatureClass<Number> fc = dataset.get(header);
            Number value = fc.getSampleValue(row);
            samples.add(value);
        }

        return samples;
    }

    public void addRow(List<Number> numbers) {
        sampleSize++;
        for(int i = 0; i < sampleHeaders.size(); i ++){
            String header = sampleHeaders.get(i);
            FeatureClass<Number> fc = dataset.get(header);
            fc.addSampleValue(numbers.get(i));
        }
    }

    public List<Number> getSampleColumn(int column) {
        String columnName = sampleHeaders.get(column);
        return getSampleColumn(columnName);
    }

    public List<Number> getSampleColumn(String columnName) {
        FeatureClass<Number> featureClass = dataset.get(columnName);

        return featureClass.getSamples();
    }

    public Number getSampleVariable(String headerName, int row) {
        return dataset.get(headerName).getSampleValue(row);
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public List<String> getSampleHeaders() { return sampleHeaders; }

    public boolean containsHeader() {
        return containsHeader;
    }

    public void updateHeaderAtIndex(int index, String newHeader){
        String oldHeader = sampleHeaders.get(index);
        sampleHeaders.set(index,newHeader);
        dataset.put(newHeader,dataset.remove(oldHeader));
    }

    // Reads in the function specified by user,
    // sets the 'output' and 'active' states of the features
    public void defineFunction(String function)
    {
        String outputFeaturesStr = cleanParentheses(function.split("=")[0]);

        // Removes first 'f' as that's for the function definition (like in "y = f(x)")
        String activeFeaturesStr = cleanParentheses(function.split("=")[1].replaceFirst("f", ""));

        // NOTE(Meyer): This particular part may need fixing when we start doing multi-objective stuff
        //              Assuming only one for now, even with the for loop.
        // Get all the output features and set them to be output variables
        String[] outputFeatures = outputFeaturesStr.split(",");
        List<String> outputFeatureList = new ArrayList<>();
        for (String feature : outputFeatures) {
            outputFeatureList.add(cleanParentheses(feature));
        }

        // Get all the active features and set so they can be used in searched
        String[] activeFeatures = activeFeaturesStr.split(",");
        List<String> activeFeatureList = new ArrayList<>();
        for (String feature : activeFeatures) {
            activeFeatureList.add(cleanParentheses(feature));
        }

        for(String header : sampleHeaders)
        {
            FeatureClass fClass = dataset.get(header);
            fClass.setOutput(false);
            fClass.setActive(false);

            if(outputFeatureList.contains(header))
            {
                fClass.setOutput(true);
            }
            else if(activeFeatureList.contains(header))
            {
                fClass.setActive(true);
            }
        }
    }

    // Removes the encapsulating parenthesis from a string, and trims it.
    private String cleanParentheses(String featureName)
    {
        // Replace the first instance of open parenthesis
        featureName = featureName.replaceFirst("\\(", "");

        // Find the last instance of closing parenthesis and split, keeping first half
        int index = featureName.lastIndexOf(")");
        if(index > 0) {
            featureName = featureName.substring(0, index);
        }

        featureName = featureName.trim();

        return featureName;
    }
}