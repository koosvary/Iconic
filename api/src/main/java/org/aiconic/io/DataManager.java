package org.aiconic.io;

import java.util.*;
import java.lang.Object.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.*;
import java.lang.Object.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataManager
{
    private static String fileName;
    private static ArrayList<double[]> samples;
    private static String[] sampleHeader;
    private static String[] sampleDescription;
    private static int featureSize;
    private static int sampleSize;
    
    public DataManager() {
        
    }
    
    public DataManager(String fileName)
    {
        DataManager.fileName = fileName;
        importData(fileName);
    }
    
    public void importData(String fileName) 
    {
        Debug.out("DataManager  importData()");
        featureSize = 0;
        sampleSize = 0;
        File file = new File(fileName);
        
        // This ArrayList holds all the data collected from the file
        samples = new ArrayList<double[]>();
        
        // Sometimes data is given as a String like "Boy, Girl", this ArrayList keeps track of all the strings and returns the
        // index value of a string variable. This will ensure all data is in a numerical format.
        ArrayList<String> stringValues = new ArrayList<String>();
        
        try {
            Scanner sc = new Scanner(file);

            // Scan through the input file one line a time
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                
                // Ensure the line isn't blank
                if (line != "")
                {
                    // Track the sample size
                    sampleSize++;
                    
                    // Split the line values seperated by a ","
                    String[] values = line.split(",");
                    
                    // Track the number of features
                    if (featureSize == 0)
                        featureSize = values.length;
                    
                    // currentValues holds all the collected values in a double format
                    double[] currentValues = new double[values.length];
                    
                    // Loop through all the collected values
                    for (int i = 0; i < values.length; i++) 
                    {
                        // If the value is already in a dobule format then instantly add it into the array
                        try  
                        {  
                            currentValues[i] = Double.parseDouble(values[i]);  
                        }  
                        // If the value collected is a string value then...
                        catch(NumberFormatException nfe)  
                        {  
                            // If the value has already been collected take the current index of that value as the input value
                            if ( stringValues.contains(values[i]) )
                            {
                                currentValues[i] = stringValues.indexOf(values[i]);
                            }
                            // If the value has not been seen before, add it into the list and add that index as the input value
                            else 
                            {
                                stringValues.add(values[i]);
                                currentValues[i] = stringValues.indexOf(values[i]);
                            }   
                        }  
                    }
                    
                    // At the end of each line being read in, add that array into the ArrayList for storage
                    samples.add(currentValues);
                    //Debug.out(Arrays.toString(currentValues));
                }         
            }
            sc.close();
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void handleMissingValues() {}
    
    public void normalizeScale() 
    {
        double[] minValues = new double[featureSize];
        double[] maxValues = new double[featureSize];
        
        // Collect the smallest and largest values of each feature
        for (int i = 0; i < sampleSize; i++)
        {
            double[] sample = samples.get(i);
            for (int j = 0; j < featureSize; j++)
            {
                double value = sample[j];
                minValues[j] = Math.min(minValues[j], value);
                maxValues[j] = Math.max(maxValues[j], value);
            }
        }
        
        // Remap the values between -1 and 1
        for (int i = 0; i < sampleSize; i++)
        {
            for (int j = 0; j < featureSize; j++)
            {
                samples.get(i)[j] = map(samples.get(i)[j], minValues[j], maxValues[j], -1, 1);
            }
        }
    }
    
    /**
    * Maps a value from an original range to a new range
    *
    * @param  value the number to be shifted
    * @param  oldMin the original minimum range value
    * @param  oldMax the original maximum range value
    * @param  newMin the new minimum range value
    * @param  newMax the new maximum range value
    * @return the new value that is the same percentage between (newMin to oldMin) & (oldMin to oldMax)
    */
    private double map(double value, double oldMin, double oldMax, double newMin, double newMax)
    {
        return newMin + ((value - oldMin) * (newMax - newMin)) / (oldMax - oldMin);
    }
    
    // Later Implimentation
    public void smoothDataPoints() {}
    public void removeOutliers()  {}
    
    public static double getInputVariable(int sample, int index)
    {
        return samples.get(sample)[index];
    }
    
    public static int getFeatureSize() {
        return featureSize;
    }
    
    public static int getSampleSize() {
        return sampleSize;
    }
}