

# Iconic
## User Manual
- Version 1.5
- 29/10/2018
- Document Number: 1.0

 
## Table of Contents
1. [Introduction](#introduction)
2. [Overview](#overview)
    1. [Conventions](#conventions)
    2. [Cautions & Warnings](#cautions-warnings)
3. [Getting Started](#getting-started)
    1. [Set-up Considerations](#set-up-considerations)
    2. [User Access Considerations](#user-access-considerations)
    3. [Accessing the System](#accessing-the-system)
    4. [System Organization & Navigation](#system-organization-navigation)
    5. [Exiting the System](#exiting-the-system)
4. [Using the System](#using-the-system)
    1. [Load, View, & Edit a Dataset](#load-view-edit-a-dataset)
    2. [Pre-Process Data](#pre-process-data)
    3. [Define Search](#define-search)
    4. [Start Search](#start-search)
    5. [Results](#results)
    6. [<Given Function/Feature>](#given-function-feature)
    7. [<Given Sub-Function/Sub-Feature>](#given-sub-function-sub-feature)
5. [Using the Command-Line](#using-the-command-line)
    1. [Choosing the Input File](#choosing-the-input-file)
    2. [Number of Outputs](#number-of-outputs)
    3. [Number of Generations](#number-of-generations)
    4. [Size of Population](#size-of-population)
    5. [Primitives to Use](#primitives-to-use)
    6. [Crossover Probability](#crossover-probability)
    7. [Mutation Probability](#mutation-probability)
    8. [Number of Repetitions](#type-of-algorithm)
    9. [Graphing the Results](#graphing-the-results)
    10. [Exporting the Results as CSV](#exporting-the-results-as-csv)
    11. [Cartesian-Specific Parameters](#cartesian-specific-parameters)
        1. [Number of Columns](#number-of-columns)
        2. [Number of Rows](#number-of-rows)
        3. [Number of Levels Back](#number-of-levels-back)
6. [Troubleshooting & Support](#troubleshooting-support)
    1. [Error Messages](#error-messages)
    2. [Special Considerations](#special-considerations)
    3. [Support](#support)
7. [Appendix](#appendix)
    1. A: Record of Changes
      2. B: Acronyms
      3. C: Glossary
      4. D: Referenced Documents
      5. E: Approvals
      6. F: Additional Appendices
      7. G: Notes / Instructions

## Introduction
*Instructions: Provide full identifying information for the automated system, application, or situation for which the User Manual applies, including as applicable, identifications number(s), title(s)/name(s), abbreviation(s)/acronym(s), part number(s), version number(s), and release number(s). Summarize the purpose of the document, the scope of activities that resulted in its development, the intended audience for the document, and expected evolution of the document. Also describe any security or privacy considerations associated with use of the User Manual.*

This User Manual (UM) provides the information necessary for users to effectively use the **Iconic Workbench**.  It is up-to-date as of Iconic v0.6.0 released 26/10/2018.

## Overview
*Instructions: Briefly describe in general terms the system/application and the purpose for which it is intended, written in non-technical terminology. Consider including a high-level, business context diagram(s) for the system. The description should include, but is not limited to, the following:*
- *Key features or major functions performed by the system/application*
- *Architecture of the system in non-technical terms (e.g., client server, Web-based, etc.)*
- *User access mode (e.g., graphical user interface)*
- *System environment or special conditions*

The **Iconic Workbench** provides a graphical user interface over the Iconic API. It allows users to easily import and modify datasets, preprocess data and start evolutionary searches to find an expression which represents the data. The Iconic Workbench aims to provide users an easy way to generate predictive models for their data.

The key features it provides are:
* Two evolutionary algorithms
    * Cartesian Genetic Programming
    * Gene Expression Programming
* Modifiable search parameters
* Import, modify and export datasets all within the application
* Preprocess data with a variety of functions such as normalise and smooth
* Plot found solution results against dataset

### Conventions
*Instructions: If applicable, describe any stylistic and command syntax conventions used within the User Manual. The following text is provided as an example only.*

This document provides screen prints and corresponding narrative to describe how to use the **Iconic Workbench**. It will describe general usage of the application in the Getting Started section.

The term ‘user’ is used throughout this document to refer to a person who requires and/or has acquired access to the **Iconic Workbench**.


### Cautions & Warnings
*Instructions: If applicable, identify any cautions or warnings that the user should know about before using the system (e.g., noted prohibitions, penalties for unauthorized access, etc.). If waiver use or copy permissions need to be obtained, describe the process.*

Below is a list of known bugs with the Iconic Workbench. For an optimal experience, please refrain from replicating the following scenarios:

#### General
1. Most buttons in the menu bar do not work. Please be aware that using these buttons may not have the desired effect

#### Input Data

1. CSV data may contain a small amount of CSV header information at the start of the file. This will cause the Iconic Workbench to display "ï»¿" prepended to the first row of the dataset
    * This will not affect performance if the CSV contains a feature header row
    * If the CSV does not contain a feature header row, the CSV header information will cause the first row in the dataset to be taken as a feature header row
2. Iconic Workbench does not support importing, saving or exporting the "info" row in a dataset

#### Process Data

1. Applying the "Remove Outliers" transformation on a feature with missing values may lock the preprocessing options for that feature
    * This is due to the following: Features with missing values must have those missing values handled before any other transformations can be applied. The user enables "Handle Missing Values", which then unlocks the other transformation functions. If "Remove Outliers" is then enabled, this will cause the feature to move back into a missing values state if outliers are removed, and the other transformations become locked. Since "Handle Missing Values" is already enabled and was applied before "Remove Outliers", the feature remains in a missing values state.
2. Changing the threshold for "Remove Outliers" will require the user to disable and re-enable "Handle Missing Values"
    * Once outliers are removed, the feature goes into a missing values state, locking all transformations except "Handle Missing Values". The user must then apply "Handle Missing Values" to unlock the other transformations. If the user changes the threshold for "Remove Outliers", it will be applied after "Handle Missing Values". This can be resolved by disabling and re-enabling "Handle Missing Values"

#### Results

1. Selected result does not remain selected when the results table is updated
2. Cartesian Genetic Programming searches may not output all results when number of outputs > 1
3. Coefficients are not truncated to 3d.p.


## Getting Started
*Instructions: Provide a general walkthrough of the system from initiation through exit. The logical arrangement of the information should enable the user to understand the sequence and flow of the system. Use screen prints to depict examples of text under each heading.*
Below is an overview of each of the screens in the Iconic Workbench, followed by a short guide on how to run your first search.

### Set-up Considerations

Java Runtime Environment version 8 or higher must be installed to use the Iconic Workbench. It can be downloaded [here](https://www.java.com/en/download/)

To optimize utilisation of the **Iconic Workbench**:
1. Use Java version 8.0 or higher.
2. Screens are designed to be viewed at a screen resolution of 1920 x 1080.

### Screens
#### Input Data

![Input Data screen with a project selected](gettingStarted/InputDataView1.png)

1. Project tree
2. Searches in the project
3. A dataset in the project
4. The Input Data tab
5. Create Dataset button
6. Import Dataset button
7. Create New Search button

![Input Data screen with a dataset selected](gettingStarted/InputDataView2.png)

8. Information/Description row of the spreadsheet view
9. Feature name/Header row
10. First row of data in the dataset
11. Export Dataset/Save button

#### Process Data

![Process Data screen](gettingStarted/ProcessData1.png)

1. Process Data tab
2. List of dataset features
3. Smooth data
4. Handle missing values
5. Normalise
6. Remove outliers
7. Offset values
8. Plot of data points

![Process Data screen with preprocessors applied](gettingStarted/ProcessData2.png)

  9. Smoothing window input
 10. Missing values method dropdown
 11. Min and Max scale input
 12. Outlier Threshold input
 13. Offset value input
 14. Preprocessor order of operations

#### Define Search

![Define Search screen for CGP search](gettingStarted/DefineSearchCGP.png)

1. Define Search tab
2. Target expression input
3. Dataset selection dropdown
4. Error metric dropdown
5. Population size input
6. Number of generations input
7.  Number of outputs input
8. Number of columns input
9. Number of rows input
10. Number of levels back input
11. Mutation algorithm dropdown
12. Mutation rate slider
13. Building block display
14. Enable building block checkbox
15. Building block symbol
16. Building block complexity
17. Building block description

![Define Search screen for GEP search](gettingStarted/DefineSearchGep.png)

18. Head length input
19. Crossover algorithm dropdown
20. Crossover rate slider

#### Start Search

![Start search screen with a search started](gettingStarted/StartSearch.png)

1. Start Search tab
2. Start search button
3. Pause search button
4. Stop search button
5. Search information display
6. Progress over time chart
7. Search console log

#### Results

![Results screen with a result selected](gettingStarted/Results.png)

1. Results tab
2. Results table
3. Size of solution
4. Error of solution
5. Solution expression
6. Solution fit plot

#### Dialogs

![Add new project dialog](gettingStarted/NewProjectDialog.png)

1. Project name input

![Add new dataset dialog](gettingStarted/NewDatasetDialog.png)

2. Dataset name input

![Add new search dialog](gettingStarted/AddSearchDialog.png)

3. Search name input
4. Search type selection dropdown

### Running a search
Below is a simple start to finish guide to running a search.

1. Start the Iconic Workbench
2. Click the "Import Dataset" button

![Clicking "Import Dataset" button](gettingStarted/runSearch/1ImportDataset.png)

3. Navigate to and select a dataset, click "Open"

![Selecting a dataset from the system explorer](gettingStarted/runSearch/2ImportDatasetDialog.png)

4. Enter a name for your project and click "OK"

![Create a Project dialog](gettingStarted/runSearch/3NewProjectDialog.png)

5. Enter a search name, select a search and click "OK"
*Note: This guide uses the "Gene Expression Programming" search type*

![Adding a Gene Expression Programming search](gettingStarted/runSearch/3bAddSearchDialog.png)

6. Select the dataset in the project tree to view the data
*Note: You may edit the data by double-clicking a cell, entering a new value and pressing enter*

![Dataset shown in spreadsheet view](gettingStarted/runSearch/4SelectDatasetInProject.png)

7. With the dataset selected, click the "Process Data" tab to view and process each feature individually
*Note: You may click individual features to plot them and apply preprocessing transformations on this screen*

![Process Data screen with a feature selected](gettingStarted/runSearch/5ProcessData.png)

8. Select the search in the project tree, then select the "Define Search" tab
*Note: You may define search parameters on this screen. Besides selecting the dataset to use, modifying search parameters is optional*

![Define Search screen with a search selected](gettingStarted/runSearch/6SelectSearchAndDefineSearch.png)

9. Select a dataset from the dropdown menu
*Note: Selecting a dataset with missing values will give a warning. If your dataset has missing values, select the dataset in the project tree, navigate to the "Process Data" screen and enable "Handle missing values" for each feature with missing values*

![Selecting a dataset to use in the search](gettingStarted/runSearch/7DefineSearchSelectDataset.png)

10. OPTIONAL: Increase mutation and crossover rates

![Increasing the mutation and crossover rated to 50% each](gettingStarted/runSearch/8DefineSearchChangeMutationAndCrossover.png)

11. OPTIONAL: Enable and disable building blocks as necessary
*Note: For optimal results, you must choose building blocks which make sense for your dataset. In this example, SIN and COS are not appropriate for the dataset used. EXP and POW are more relevant, and have been enabled*

![Selecing appropriate building blocks](gettingStarted/runSearch/9SelectBuildingBlocks.png)

12. Click the "Start Search" tab and then start the search by clicking the "Start Search" button

![Starting the search from the Start Search view](gettingStarted/runSearch/10StartSearchAndStartSearch.png)

13. The search will be started and you can immediately observe search progress

![Displaying search progress from the Start Search view](gettingStarted/runSearch/11StartSearchSearchProgress.png)

14. Click the "Results" tab and then select a result from the table to plot it
*Note: You may right click a result and select "Copy" to copy the expression to the clipboard

![Plotting results on the Solution Fit Plot](gettingStarted/runSearch/12ResultsAndSolutionChart.png)

### Exiting the System

Before exiting the system, it is recommended to stop all running searches.

1. For each search in the project tree, select the search, navigate to the "Start Search" view and click the "Stop Search" button

![Stopping a search](gettingStarted/runSearch/13StopSearch.png)

2. Click the exit button on the window, or navigate to the "File" menu located at the top left of the window and click "Exit"

## Using the System
*Instructions: Provide a detailed description of each user function and/or feature, explaining in detail the characteristics of the required input and system-produced output. Each function/feature should be described under a separate sub-section header, 4.1-4.x, and should correspond sequentially to the system functions (e.g., menu items) and/or features listed in certain sub-sections found in this document. Include screen prints as needed to depict examples. This section of the User Manual may also be tailored or customized based on defined user roles, if appropriate.*

*If applicable, include sub-sections that describe the pre-programmed and/or ad hoc query and retrieval capabilities of the system and associated user procedures (e.g., sequenced control instructions to extract query requests from the database). Include the query name or code the user would invoke to execute the query and any query parameters.*

*If applicable, include sub-sections to describe and depict all standard and/or ad hoc report capabilities available to the end user and any associated user procedures. Include formats for each available report and the meaning of each field shown on the report. Also describe any special formats associated with ad hoc reports that the user may be able to create. Provide detailed instructions for executing and printing the different reports that are available.*
*Include descriptions of output procedures, identifying output formats and specifying the output’s purpose, frequency, options, media, and location.*

The following sub-sections provide detailed, step-by-step instructions on how to use the various functions or features of the **Iconic Workbench**.

### Create a New Project
Projects provide logical groupings for multiple datasets and searches.
1. Click "File" in the menu bar then click "New Project..." OR press Ctrl+N
2. Give your project a name and click "OK"

Alternatively, if no project exists, you will be prompted to create one when importing or creating a dataset

### Import or Create a Dataset
You may either import an existing dataset in CSV format, or create one from scratch and enter or paste in values.
#### Import a Dataset
1. Create a project and select it in the project tree
2. Click the "Import Dataset" button on the "Input Data" screen
3. Navigate to the desired dataset using your systems file explorer
4. Select the dataset and click "Open"

#### Create a Dataset
1. Create a project and select it in the project tree
2. Click the "Create Dataset" button on the "Input Data" screen
3. Give the dataset a name and click "OK"

*NOTE: If a project does not exist, you will be asked to create one when importing or creating a dataset

### Add a Search
You may add multiple searches to a dataset. There are two types of searches to choose from : Gene Expression Programming or Cartesian Genetic Programming.

1. Create a project and select it in the project tree
2. Click the "New Search" button on the "Input Data" screen
3. Give the search a name
4. Select a search type
5. Click "OK"

### View & Edit a Dataset
You may view an edit the dataset in a spreadsheet view. This supports copy and paste both to and from Microsoft Excel
1. Import or create a dataset and select it in the project tree
2. Navigate to the "Input Data" screen
    a. Here you can view the dataset in a spreadsheet fashion
3. Double-click a cell to edit it, enter a new value and press the enter key or click out of the cell to save
4. Select a cell and press the delete key to remove the value
5. You may select multiple cells by clicking and dragging the mouse
6. You may copy data via right-click -> Copy OR Edit -> Copy in the menu bar OR by pressing Ctrl+C
7. You may paste data via selecting a cell, right-click -> Paste OR Edit -> Paste or by pressing Ctrl+V
8. You may edit the "name" row to change or give a name to the feature

### Export a Dataset
You may save a dataset from the Iconic Workbench to your system
1. Select a dataset in the project tree
2. Navigate to the "Input Data" screen
3. Click the "Export Dataset" button
4. Select a location, give the dataset a name (and file extension) and click "Save"

This will save the dataset in a CSV format

### Viewing Feature Data
You may view a plot of the data for each individual feature in the dataset
1. Select a dataset in the project tree
2. Navigate to the "Process Data" screen
3. Select a feature in the table

This will plot the data from that feature below. Additionally, a label "(missing values)" will appear next to the feature in the table if the feature is missing values.

### Preprocess Data
You may apply a number of transformations to each feature in the dataset. These can be enabled in a user specified order. This ordering will appear on the right hand side of the screen. Features with preprocessors applied will be labelled with "(modified)". This will not affect the dataset in the "Input Data" screen, nor will modified values be exported when exporting the dataset.

#### Smooth Data
You may smooth feature data using a sliding window approach. By default, the window is 2. This means that for every data point, we take the average or the two before, two after and the point itself. This becomes the new value for this data point. This is calculated in advanced before making any changes to the dataset. If there is no values immediately before or after the data point, the window will wrap around to the nearest point.
1. Select a feature in the feature table
2. Click the "Smooth data points of ..." checkbox
3. Adjust the window size as necessary, pressing enter to submit the new window size

#### Handle Missing Values
If the dataset is missing values, you **MUST** apply this preprocessor before any others can be applied. This is also true if the "Remove Outliers" (see below) preprocessor is applied and removes values from the dataset.
There are 5 methods for removing outliers:
* Copy values from previous row
* Set to the mean value
* Set to the median value
* Set to 0
* Set to 1
1. Select a feature in the feature table
2. Click the "Handle missing values of ..." checkbox
3. Choose a method from the drop-down menu

#### Normalise Scale
You may normalise the scale of the data points between two user defined values. For example, if your data range is 0 to 100, you can use this preprocessor to scale it between 0 and 1.
1. Select a feature in the feature table
2. Click the "Normalise scale of ..." checkbox
3. Set the minimum value and press the enter key
4. Set the maximum value and press the enter key

#### Remove Outliers
You may use this preprocessor to remove outlying data points from the feature. You may specify a threshold. A data point is considered an outlier if the distance between the data point and the mean value is greater than the threshold multiplied by IQR (interquartile range).
If outliers are removed, you must handle missing values before continuing.
1. Select a feature in the feature table
2. Click the "Remove outliers of ..." checkbox
3. Specify the threshold by entering a value into the box and pressing the enter key or by using the arrows
4. Apply the "Handle Missing Values" operation

#### Offset Values
You may offset each data point in the feature by a specified positive or negative amount.
1. Select a feature in the feature table
2. Click the "Offset values of ..." checkbox
3. Enter a value and press enter to apply

### Define Search Parameters
You may define the parameters to use when searching. Availability and display of some parameters are restricted to certain search types. For quick reference, you may hover over a label for an explanation.
Additional information may be found here: [Using the Command-Line](#using-the-command-line)

#### Select a dataset
You **MUST** select the dataset to search on
1. Select a search in the project tree
2. Navigate to the "Define Search" page
3. Select a dataset to use in the search via the "Select a dataset" drop-down menu

*Note: You will not be able to select a dataset if it is missing values. You must handle these missing values via the "Process Data" screen*

#### Target Function
This specifies the target function to search for. It allows you to define the input features and output (classifier) feature to use for searching.
1. Select a search in the project tree
2. Navigate to the "Define Search" page
3. Select a dataset to use in the search via the "Select a dataset" drop-down menu
    a. The target function will automatically fill out in the following format: (y) = f((x), (w), (...), (z)) where y = output = the last feature in the dataset, and x,w,...,z = input = the remaining features in the dataset.
4. You may change the target function as long as it adheres to the following rules:
    a. Each feature name must be encapsulated within parenthesis
    b. There may only be one feature on the left side of the equation
    c. The list of input features must be encapsulated within parenthesis with an 'f' prepended
    d. Each feature name must be separated by a comma, followed by a space (except the last feature)
5. Press the enter key

Target Expression Syntax

![Syntax of target expression](images/TargetExpressionSyntax.png)

#### Building Blocks

These are the functional primitives to be included in the search. Each building block can be enabled or disabled individually, have their complexities set and will display a description when clicked.
1. Select a search in the project tree
2. Navigate to the "Define Search" page
3. Click the name of a building block to display its description on the right
4. Double click the name, or click the checkbox next to it, to toggle the building block enabled or disabled
5. Double click the complexity, enter a new value and press the enter key to change the complexity
6. Click the "Enable All" button to enable all building blocks
7. Click the "Disable All" button to disable all building blocks
    a. This button will only display if "Enable All" is pressed
    
##### Building block list
Below is a list of all currently implemented building blocks in alphabetical order

        ABS (a):
        Returns the positive value of a.

        ADD (a, b):
        Returns a + b.

        AND (a, b):
        Returns 1 if both a and b are greater than 0, 0 otherwise.

        ACOS (a):
        Returns the inverse cosine function of a.

        ASIN (a):
        Returns the inverse sine function of a.

        ATAN (a):
        Returns the inverse single argument tangent function of a.

        CEIL (a):
        Returns the integer of a rounded up.

        COS (a):
        Returns the cosine of a.

        DIV (a, b):
        Returns the division of a / b.

        EQUAL (a, b):
        Returns 1 if a is equal to b, 0 otherwise.

        EXP (a):
        Returns e^a.

        FLOOR (a):
        Returns the integer of a rounded down.

        GAUSS (a):
        Returns exp(-x^2), providing a normal distribution.

        GREATER (a, b):
        Returns 1 if a > b, 0 otherwise.

        GREATER_EQUAL (a, b):
        Returns 1 if a >= b, 0 otherwise.

        IF (a, b, c):
        Returns returns b if a > 0, c otherwise.

        LESS (a, b):
        Returns 1 if a < b, 0 otherwise.

        LESSEQUAL (a, b):
        Returns 1 if a <= b, 0 otherwise.

        LOGISTIC (a):
        Returns (1 / 1 + exp(-a)).
        This is a common sigmoid squashing function.

        MAX (a, b):
        Returns the maximum value of a and b.

        MIN (a, b):
        Returns the minimum value of a and b.

        MOD (a, b):
        Returns the remainder of a / b.

        MUL (a, b):
        Returns a * b.

        LN (a):
        Returns the natural logarithm (base e) of a.

        NEG (a):
        Returns - a.

        NOT (a):
        Returns 0 if a is greater than 0, 1 otherwise.

        OR (a, b):
        Returns 1 if either a or b are greater than 0, 0 otherwise.

        POW (a, b):
        Returns a^b.

        ROOT (a, b):
        Returns the b-th root of a if a is greater than 0, NaN otherwise.

        SGN (a):
        Returns -1 if a is negative, 1 if a is positive, 0 otherwise.

        SIN (a):
        Returns the sine of a.

        SQRT (a):
        Returns the square root of a.

        STEP (a):
        Returns 1 if x is positive, 0 otherwise.

        SUB (a, b):
        Returns a - b.

        TAN (a):
        Returns the tangent of a.

        TANH (a):
        Returns the hyperbolic tangent of a.
        This is a common squashing function returning a value between -1 and 1.

        ATAN2 (a, b):
        Returns the two argument inverse tangent function.

        XOR (a, b):
        Returns 1 if (a <= 0 and b > 0) or (a > 0 and b <= 0), 0 otherwise.


#### General Parameters
Below are the parameters which apply to both Gene Expression Programming and Cartesian Genetic Programming searches

##### Error Metric
This is the algorithm to use for determining result error. Currently, only Mean Squared Error is available

##### Population Size
This is the number of "children" to be generated each generation

##### Generations
This is the number of generations to run the search for. A value of 0 will run the search indefinitely

##### Mutation
This is the algorithm to use for mutation of chromosomes. Currently, only Single Active Gene Mutation is available. Mutation is a small random change in a chromosome

##### Mutation Rate
This is the chance of mutation as a percentage. The higher the mutation rate, the more chance a mutation will occur

#### Gene Expression Programming Specific Parameters
Below are the parameters which only apply to Gene Expression Programming searches

##### Head Length
This is the length of the Header to use.

The total length of the chromosome can be at minimum 1, and at max Header length + Tail length (where Tail length is Header length + 1). The Header part of the chromosome can pick building blocks, features of the dataset or constants. The Tail can only pick features or constants. The Tail was used to ensure that there is no leaf nodes expecting to have children.

![Diagram of a chromosome](images/HeaderLength.png)

In this diagram, the green F1 (meaning feature 1 in the dataset) doesn't use its children because it doesn't need to. But if it was a building block like a "+" then it would end up needing it.

##### Crossover
This is the algorithm to use for crossover of chromosomes. Currently, only Simple Expression Crossover is available. Crossover is the act of replacing part of the child's genes with those of a parents within the population

##### Crossover Rate
This is the chance of crossover as a percentage. The higher the crossover rate, the more chance a crossover will occur

#### Cartesian Genetic Programming Specific Parameters
Below are the parameters which only apply to Cartesian Genetic Programming searches

##### Number of Outputs
The number of outputs that the CGP chromosome can have, effectively splitting a solution into multiple parts

##### Number of Columns
The number of columns in the CGP chromosomes dimensions

##### Number of Rows
The number of rows in the CGP chromosomes dimensions. It is recommended to set this value to 1 as default.

##### Number of Levels Back
The number of levels back that any node in the CGP chromosome can reach to connect to another node.

### Searching

#### Start a Search
To start a search:
1. Select a search in the project tree
2. Ensure the following
    a. Ensure a dataset has been selected for the search on the "Define Search" screen
    b. Ensure all missing values have been handled
    c. Ensure the target expression meets the syntax
3. Navigate to the "Start Search" screen
4. Click the "Start Search" button

#### Stop a Search
To stop a search:
1. Select a currently running search in the project tree
2. Navigate to the "Start Search" screen
3. Click the "Stop Search" button

#### Pause & Resume a Search
You may pause a search and resume from where it left off.
1. Select a currently running search in the project tree
2. Navigate to the "Start Search Screen"
3. Click the "Pause Search" button
4. While a search is paused, you may modify the following parameters: Mutation rate, Crossover rate (GEP only), Generations, Building Blocks
5. To resume the search, click the "Start Search" button

#### View Search Progress
The Iconic Workbench gives live information to the user about the search progress. This includes: Progress over time, Time elapsed, number of generations, generations per second, time since last improvement, average improvement time and number of CPU cores.
1. Select a currently running search
2. Navigate to the "Start Search" screen

### Results
You may view live information of the search results via the "Results" screen. This displays a table of results as well as a solution fit plot when a result is selected.

#### View Results Table & Solution Fit Plot
1. Select a currently running search in the project tree
2. Navigate to the "Results" screen
    a. Here you can view the best result of each size (number of primitives). It will display the solution size, error, and the solution itself.
    b. This table is sorted by smallest error in ascending order. You may change this ordering by clicking on the table headers
3. Click on a result in the table to view the solution fit plot
    a. The solution fit plot will evaluate the expression against the dataset and plot the "Actual" values (output of the expression) against the "Expected" values (feature specified as the output in the target expression)

#### Exporting a Solution
Currently, Iconic only supports copy & paste of solutions from the results table
1. Select a currently running search in the project tree
2. Navigate to the "Results" screen
3. Right-click a result in the table
4. Click "Copy"

## Using the Command-Line

### Synopsis

```bash
$ java -jar iconic-cli.jar -i <file> --population <number> --generations <number> --outputs <number> --primitives <symbol,...> [--graph] [--csv]
```

### Description

`iconic-cli` is a simple command-line tool designed to expedite the generation of models without relying
on a graphical user interface. The current version `0.7.0` only uses 
`Global Simple Evolutionary Algorithm for Multiple Objectives (GSEMO)` with cartesian (graph-based) chromosomes on
two pre-defined objectives that minimise the:

 1. mean squared error, and,
 2. genome's size

`iconic-cli` takes a single input file, a population size and a number of generations.

While running it prints the current progress as a percentage of generations elapsed versus total generations,
the current least error and smallest size, and the total amount of time elapsed.

![An example of running the CLI with the minimum number of parameters](images\cli\example-run-basic.png)

The output of each run will be placed in a new folder named after the input file, arranged in subfolders according to 
the date the run was initiated. Unless additional flags are included only a README file will be output 
containing each of the parameters used and their values. 

```bash
$ java -jar iconic-cli.jar ...
$ ...
$ ls .
```

After running:

```bash
$ ls .
    Directory: C:\Path\to\iconic-cli
    

Mode                LastWriteTime         Length Name
----                -------------         ------ ----
d-----       31/10/2018  11:46 AM                inputFile          # Output directory
-a----       22/10/2018   2:21 PM                inputFile.csv
-a----       16/10/2018   9:29 AM                iconic-cli.jar
```

### Type of Algorithm

`(-a | --algorithm) <GENE_EXPRESSION_PROGRAMMING | CARTESIAN_GENETIC_PROGRAMMING>`

The type of algorithm determines which algorithm should be used to perform the search.
As of version `0.7.0` this parameter is ignored as only `GSEMO` with cartesian chromosomes
is used.

### Choosing the Input File

`(-i | --input) <string>`

The input file is a comma delimited list of values where each line is a new sample. 
The current version `0.7.0` doesn't support input files with column headers.

```csv
0, 1, 0.25
1, 1, 0.5
0, 0, 1
```

### Number of Outputs

`--outputs <integer>`

The number of outputs is used to specify how many outputs a chromosome can have.
If the chromosome doesn't support multiple outputs this parameter will be ignored.

In the current version `0.7.0` chromosomes with multiple outputs have each output summed
together to produce a single output.

### Number of Generations

`(-g | --generations) <integer>`

The number of generations is used to specify how many generations to let the population evolve.
Unlike the `Iconic Workbench` the number of generations must be greater than zero.

### Size of Population

`(-p | --population) <integer>`

The population size is used to specify the size of the initial starting population.
This is less meaningful with `GSEMO` as the population grows dynamically with only Pareto-optimal solutions 
being kept for the next generation. Setting an initial population size greater than one can still be
used to increase the genetic diversity of the initial population.

### Primitives to Use

`--primitives <symbol>,...`

The primitive set used by the algorithm must be specified as a list of comma-delimited symbols. 
If no primitives are specified all available primitives will be used by default.

`--primitives ADD,MUL,DIV,SUB,LOG,SIN`

A full list of available primitives can be seen by using `--listPrimitives`.

### Crossover Probability

`(-cP | --crossoverProbability) <percentage in range [0.0, 1.0]>`

The probability of crossover being used on an offspring during each instance of the 
evolutionary cycle. 
A crossover probability of `1.0` will cause crossover to always occur in each cycle,
whereas a probability of `0.0` will prevent crossover from ever occurring.

*The crossover probability will only take effect if the algorithm uses a crossover operator.
In version `0.7.0` no crossover is included by default with no way to change it.*

### Mutation Probability

`(-mP | --mutationProbability) <percentage in range [0.0, 1.0]>`

The probability of mutation being used on an offspring during each instance of the 
evolutionary cycle. 
A mutation probability of `1.0` will cause mutation to always occur in each cycle,
whereas a probability of `0.0` will prevent mutation from ever occurring.

*The mutation probability will only take effect if the algorithm uses a mutator.
In version `0.7.0` a mutator is included by default with no way to change it.*

### Number of Repetitions

`(-r | --repeat) <integer>`

The number of repetitions (trials) to repeat the experiment for. Each trial will use the 
same parameters as specified. 

If the `--graph` or `--csv` flags are enabled then the
results from each trial will be included within the same output file(s).

### Graphing the Results

`--graph`

If this flag is included `iconic-cli` will export the results to several charts in the PNG format.
These charts will be placed in the same output folder as the default output files.

The charts generated include a plot of every generation's non-dominated set, the last generation's
non-dominated set, and a solution-fit plot of the overall Pareto-optimal set.

Non-dominated solutions from every generation

![A chart of all non-dominated solutions from every generation](images\cli\results-all.png)

Non-dominated solutions from the last generation

![A chart of all non-dominated solutions from the last generation](images\cli\results-final.png)

Overall Pareto-optimal set's solution fit

![A chart of the overall Pareto-optimal set's solution-fit](images\cli\solution-fit.png)
 
### Exporting the Results as CSV

`--csv`

If this flag is included `iconic-cli` will export the results to several CSV files.
These CSV files will be placed in the same output folder as the default output files.

The CSV files generated include a list of chromosomes from every generation's non-dominated set,
and the chromosomes from the last generation's non-dominated set.
Chromosomes are formatted as a 3-tuple of (mean squared error, size, model).

### Cartesian-Specific Parameters

These parameters are exclusive to cartesian chromosomes. If any other type of chromosome
is used any option specified here will be ignored.

*Version `0.7.0` doesn't support the use of other chromosomal types so these parameters will
never be ignored.*

#### Number of Columns

`(--columns) <integer>`

The number of columns that the chromosome should use. A cartesian chromosome stores
its genotype as a graph, this parameter specifies the columnar dimensions of that graph.

#### Number of Rows

`(--rows) <integer>`

The number of rows that the chromosome should use. A cartesian chromosome stores
its genotype as a graph, this parameter specifies the row dimensions of that graph.

In general there is no reason to use a number of rows other than one as there's
always a functionally equivalent one-dimensional graph. If in doubt set this 
parameter to one.

#### Number of Levels Back

`(--levelsBack) <integer>`

The maximum number of levels back that any node within the chromosome can connect to. 
A cartesian chromosome stores its genotype as a graph, this parameter specifies how
far back in terms of columns that any node in the graph can reach. If a column is in
range the node may connect to any other node in that column.

A maximum number of levels back that's equal to or greater than the number of columns in
the chromosome means that any node in the graph can connect to any other node preceding it.
Reducing the maximum number of levels back will force the chromosome to produce larger
models.

## Troubleshooting & Support
*Instructions: Describe all recovery and error correction procedures, including error conditions that may be generated and corrective actions that may need to be taken. Organize the information in sub-sections as appropriate. The following are common sub-sections that may be included as appropriate.*

### Error Messages
*Instructions: Identify the error messages that a user may receive and the likely cause(s) and/or possible corrective actions for the error. If the list is extensive, this information may be best provided in an appendix to the document that is referenced here.*

### Special Considerations
*Instructions: If applicable, describe any special circumstances, actions, caveats, exceptions, etc., that should be considered for troubleshooting.*

### Support
*Instructions: Provide information on how the user can get emergency assistance and system support (e.g., help desk support, production support, etc.). Include the names of the responsible personnel and organization(s), telephone numbers, and email addresses of the staff who serve as points of contact for system support. The following table is provided as an example and may be modified as needed. Also provide instructions for how identified problems with the system are to be reported. Include instructions for security incident handling, as appropriate.*

##### Table 1 - Support Points of Contact
|Contact - Org|Phone|Email|Role|Responsibility|
|:---:|---|---|---|---|
|Jayden Urch - Iconic|N/A|jayden.urch@uon.edu.au|Project Manager|Project management|
|Tim Pitts - Iconic|N/A|timothy.pitts@uon.edu.au|Client Communications|External resourcing and communications|
|Dr Shamus Smith - UoN|(02)49216175|shamus.smith@newcastle.edu.au|Course Co-ordinator|Course Co-ordination|
|Dr Pablo Moscato - UoN|(02)49216056|pablo.moscato@newcastle.edu.au|Course Manager|Subject matter expert|
|Dr Markus Wagner - UoA|N/A|markus.wagner@adelaide.edu.au|Primary Client|Project direction and features|
|Contact Name & Organization|Phone|Email|Role|Responsibility|

## Appendix
  ### Appendix A: Record of Changes
*Instructions: Provide information on how the development and distribution of the User Manual will be controlled and tracked. Use the table below to provide the version number, the date of the version, the author/owner of the version, and a brief description of the reason for creating the revised version.*

##### Table 2 - Record of Changes
|Version Number|Date|Author/Owner|Description of Change|
|---|---|---|---|
|X.X|DD/MM/YYYY|Author/Owner|Description of Change|
|X.X|DD/MM/YYYY|Author/Owner|Description of Change|
|X.X|DD/MM/YYYY|Author/Owner|Description of Change|

### Appendix B: Acronyms
*Instructions: Provide a list of acronyms and associated literal translations used within the document. List the acronyms in alphabetical order using a tabular format as depicted below.*

##### Table 3 - Acronyms
|Acronym|Literal Translation|
|---|---|
|Acronym|Literal Translation|
|Acronym|Literal Translation|
|Acronym|Literal Translation|

### Appendix C: Glossary
*Instructions: Provide clear and concise definitions for terms used in this document that may be unfamiliar to readers of the document. Terms are to be listed in alphabetical order.*

##### Table 4 - Glossary
|Term|Acronym|Definition|
|---|---|---|
|Term|Acronym|Definition|
|Term|Acronym|Definition|
|Term|Acronym|Definition|

### Appendix D: Referenced Documents
*Instructions: Summarize the relationship of this document to other relevant documents. Provide identifying information for all documents used to arrive at and/or referenced within this document (e.g., related and/or companion documents, prerequisite documents, relevant technical documentation, etc.).*

##### Table 5 - Referenced Documents
|Document Name|Document Location and/or URL    Issuance|Date|
|---|---|---|
|Document Name|Document Location and/or URL|DD/MM/YYYY|
|Document Name|Document Location and/or URL|DD/MM/YYYY|
|Document Name|Document Location and/or URL|DD/MM/YYYY|

### Appendix E: Approvals
The undersigned acknowledge that they have reviewed the User Manual and agree with the information presented within this document. Changes to this User Manual will be coordinated with, and approved by, the undersigned, or their designated representatives.

*Instructions: List the individuals whose signatures are desired. Examples of such individuals are Business Owner, Project Manager (if identified), and any appropriate stakeholders. Add additional lines for signature as necessary.*

##### Table 6 - Approvals
|Document Approved By|Date Approved|
|---|---|
|Name: _Name_, _Job Title_ - _Company_|DD/MM/YYYY|
|Name: _Name_, _Job Title_ - _Company_|DD/MM/YYYY|
|Name: _Name_, _Job Title_ - _Company_|DD/MM/YYYY|
|Name: _Name_, _Job Title_ - _Company_|DD/MM/YYYY|

### Appendix F: Additional Appendices
*Instructions: Utilize additional appendices to facilitate ease of use and maintenance of the document.*

### Appendix G: Notes / Instructions
This document is a template for creating a User Manual for a given investment or project. The final document should be delivered in an electronically searchable format. The User Manual should stand on its own with all elements explained and acronyms spelled out for reader/reviewers, including reviewers outside CMS who may not be familiar with CMS projects and investments.

This template includes instructions, boilerplate text, and fields. The developer should note that:
- Each section provides instructions or describes the intent, assumptions, and context for content included in that section. Instructional text appears in italicized font throughout this template.
- When finished, instructional text in each section should be replaced with information specific to the particular investment.
- Some text and tables are provided as boilerplate examples of wording and formats that may be used or modified as appropriate.

When using this template, follow these steps:
1. Table captions and descriptions are to be placed left-aligned, above the table.
2. Modify any boilerplate text, as appropriate, to your specific investment.
3. Do not delete any headings. If the heading is not applicable to the investment, enter “Not Applicable” under the heading.
4. Figure captions and descriptions are to be placed left-aligned, below the figure. All figures must have an associated tag providing appropriate alternative text for Section 508 compliance.
5. Delete this “Notes / Instructions” page and all instructions to the programmer before finalizing the initial draft of the document.