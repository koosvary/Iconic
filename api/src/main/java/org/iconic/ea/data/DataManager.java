/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private List<String> sampleInfo;
    private List<String> expectedOutputHeaders;
    private HashMap<String, FeatureClass<Number>> dataset;
    private int featureSize;
    private int sampleSize;
    private boolean containsHeader = false;
    private boolean containsInfo = false;
    private String infoPlaceholder = "Enter variable description here";

    public DataManager(){
        expectedOutputHeaders = new ArrayList<>();
        sampleHeaders = new ArrayList<>();
        sampleInfo = new ArrayList<>();
        createNewDataset();
    }

    public DataManager(String fileName) {
        this.fileName = fileName;
        expectedOutputHeaders = new ArrayList<>();
        sampleHeaders = new ArrayList<>();
        sampleInfo = new ArrayList<>();

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
            if(containsInfo){
                for(int j = 0; j < featureSize-1; j ++) {
                    fileWriter.append(String.valueOf(sampleInfo.get(j)));
                    fileWriter.append(",");
                }
                fileWriter.append(String.valueOf(sampleInfo.get(featureSize-1)));
                fileWriter.append(System.getProperty("line.separator"));
            }
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

        //Feature size is set as number of commas as line.split(",")
        //does not account for trailing commas
        featureSize = 0;
        if (line != null) {
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) == ',') featureSize++;
            }
            //Plus 1 for last value in line
            featureSize++;
        }

        // Assume the delimiter is a comma, and set feature size
        String[] split1 = line.split(",");

        // Try to determine if the datafile contains a header row
        for (String info : split1) {
            try {
                Double.parseDouble(info);
            } catch (NumberFormatException e) {
                //Check that it is not a missing number
                if(!info.trim().isEmpty()) {
                    containsHeader = true;
                    // Read in the next line for later (needed because the `else` block already reads in the next line)
                    line = getNextLineFromDataFile(sc);
                    break;
                }
            }
        }

        // Assume the delimiter is a comma, and set feature size
        String[] split2 = line.split(",");

        // Try to determine if the datafile contains a header row
        for (String header : split2) {
            try {
                Double.parseDouble(header);
            } catch (NumberFormatException e) {
                //Check that it is not a missing number
                if(!header.trim().isEmpty()) {
                    containsInfo = true;
                    // Read in the next line for later (needed because the `else` block already reads in the next line)
                    line = getNextLineFromDataFile(sc);
                    break;
                }
            }
        }

        if(containsHeader && !containsInfo){
            split2 = split1.clone();
        }

        if (containsInfo) {
            int missingInfoCount = 1;
            int infoCount = 0;
            //Update headers setting missing values as column
            while(infoCount < split1.length){
                String info = split1[infoCount];
                //If the header is missing
                if(info.trim().isEmpty()) {
                    sampleInfo.add("Missing" + missingInfoCount);
                    missingInfoCount++;
                }
                else{
                    sampleInfo.add(info);
                }
                infoCount++;
            }
            //Add missing headers from trailing commas
            while(infoCount < featureSize){
                sampleInfo.add("Blank" + missingInfoCount);
                missingInfoCount++;
                infoCount++;
            }
        }
        else {
            for (int i = 0; i < featureSize; i++) {
                sampleInfo.add(infoPlaceholder);
            }
            containsInfo = true;
        }

        if (containsHeader) {
            int missingHeaderCount = 1;
            int headerCount = 0;
            //Update headers setting missing values as column
            while(headerCount < split2.length){
                String header = split2[headerCount];
                //If the header is missing
                if(header.trim().isEmpty()) {
                    sampleHeaders.add("Missing" + missingHeaderCount);
                    missingHeaderCount++;
                }
                //Check for duplicate header tame for example bloodtype, bloodtype, age will be
                //changed to bloodtype, bloodtype, age
                else if(sampleHeaders.contains(header)){
                    int duplicateCount = Collections.frequency(sampleHeaders, header) + 1;
                    sampleHeaders.add(header+duplicateCount);
                }
                else{
                    sampleHeaders.add(header);
                }
                headerCount++;
            }
            //Add missing headers from trailing commas
            while(headerCount < featureSize){
                sampleHeaders.add("Blank" + missingHeaderCount);
                missingHeaderCount++;
                headerCount++;
            }
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
            int i  = 0;
            // Parse the string values to a double and add to FeatureClass
            while(i < values.length) {
                try {
                    Double value = Double.parseDouble(values[i]);
                    featureClasses.get(i).addSampleValue(value);
                }catch (Exception e) {
                    featureClasses.get(i).addSampleValue(null);
                }
                i++;
            }
            while(i < featureSize){
                featureClasses.get(i).addSampleValue(null);
                i++;
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

    public void addNewFeature(String info, String sampleHeader, List<Number> feature){
        sampleInfo.add(info);
        sampleHeaders.add(sampleHeader);
        sampleInfo.add(infoPlaceholder);
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
        featureSize = 1;
        dataset = new HashMap<>();

        // Generate all the header names such as: A, B, C, ..., Z, AA, BB, etc
        for (int i = 0; i < featureSize; i++) {
            sampleHeaders.add(intToHeader(i));
        }
        containsHeader = true;

        // Generate all the header names such as: A, B, C, ..., Z, AA, BB, etc
        for (int i = 0; i < featureSize; i++) {
            sampleInfo.add(infoPlaceholder);
        }
        containsInfo = true;

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

    public List<String> getSampleInfo() {
        return sampleInfo;
    }

    public boolean containsHeader() {
        return containsHeader;
    }

    public boolean containsInfo() {
        return containsInfo;
    }

    public void updateHeaderAtIndex(int index, String newHeader){
        String oldHeader = sampleHeaders.get(index);
        sampleHeaders.set(index,newHeader);
        dataset.put(newHeader,dataset.remove(oldHeader));
    }

    public void updateInfoAtIndex(int index, String newHeader){
        sampleInfo.set(index,newHeader);
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
    
    // Note(Meyer): This is only for single target expressions, not sure how we'd be graphing multi-objective anyway.
    public List<Number> getExpectedOutputValues()
    {
        List<Number> expectedOutputValues = null;

        for(String header : sampleHeaders)
        {
            FeatureClass fClass = dataset.get(header);

            if(fClass.isOutput())
            {
                log.info(header + " is output");
                expectedOutputValues = fClass.getSamples();
            }
        }

        return expectedOutputValues;
    }
}