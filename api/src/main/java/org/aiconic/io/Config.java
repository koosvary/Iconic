package org.aiconic.io;

import java.util.*;
import java.io.*;

public class Config
{
    private static Properties defaultProperties = new Properties();
    private String fileName = "defaultProperties.config";
    private Set<Object> settings;
    
    public Config()
    {
        loadConfig();
    }
    
    public void loadConfig()
    {
        Debug.out("Config   loadConfig()");
        try
        {
            // Create and load default properties
            FileInputStream in = new FileInputStream(fileName);
            defaultProperties.load(in);
            in.close();
            
            // Show all Variables and values in hashtable.
            settings = defaultProperties.keySet();   // get set-view of keys
            Iterator<Object> itr = settings.iterator();
            String str;

            while(itr.hasNext()) {
                str = (String) itr.next();
                //Debug.out("   " + str + " is " + defaultProperties.getProperty(str) + ".");
            }     
            
            // look for state not in list -- specify default
            //str = defaultProperties.getProperty("INTCONSTANT", "Not Found");
            //System.out.println("INTCONSTANT " + str + ".");
        } 
        catch (IOException i) {
            i.printStackTrace();
        } 
    }
    
    public static String getProperty(String property)
    {
        return defaultProperties.getProperty(property, "Not Found");
    }
}