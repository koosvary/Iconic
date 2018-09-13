# Iconic Project

## Build Status

## What's New

### Release Notes - 0.3.0 - August 31st, 2018

#### Improvements
* Abstract our Evolutionary Algorithm class to handle both GEP and CGP
* Implement CGP for use with the command line interface (CLI)
* The client can now be compiled into a single stand-alone jar by executing the gradle task `fatjar`
* The client output will only update when a solution with a better fitness is found, to allow for better readability
* More unit test classes under the hood
* API functionality to handle missing values, **this is not in use yet**.
  * Different modes for handling these values are:
    * Ignore the row
    * Copy the previous rows value
    * Take the mean value from the set
    * Take the median value
    * Use a zero
    * Use a one
    * Use a specifically set value

#### Fixes
* Executing gradle build tasks now run all the tests

### Release Notes - 0.2.0 - August 17th, 2018

#### Improvements
* “Input Data” screen has been overhauled
  * Datasets now display as a spreadsheet
  * This spreadsheet is editable and updates automatically in local memory
  * Edited dataset can be exported to a file
  * Will prompt to create a new project when loading dataset if one doesn’t exist
  * Will alert the user to select a project when importing a dataset, if one exists and is not selected
* Under the hood changes to the data structure used to store and process datasets in memory
* Improved support for header rows in the dataset
  * If the first line of the dataset contains all strings, it is assumed to be a header row.
* This is a temporary workaround and is due to change
  * Otherwise, it is assumed there is no header row and default headers will be given
    * Default headers follow the A-Z, AA-ZZ format
* Data normalisation and smoothing have been updated to work with new data structure
  * Other pre-processing features to come
  * The smoothing function by default takes the 2 neighbours on either side of the current value and takes the average of those numbers, which becomes the new value for that index. This continues for all values in the feature and updates values once complete
* Target Expression now updates when a dataset is loaded
  * Displays in the form of y=f(x,w,…,z)
    * Where y is the last feature in the dataset
    * x,w,…,z are each of the other features
  * This is a GUI change only, which represents the default target expression used by the search
    * The GUI and API are unlinked in this respect. The target expression still cannot be customized in the search
    * This will be fixed in the next release
* A variety of new building blocks for expressions have been added under the hood.
  * A list of these should be available soon
  * These are not currently functional in the GUI


#### Fixes
* “Load Dataset” button now working

### Release Notes - 0.1.0 - May 28th, 2018

#### Improvements
* Gene Expression Programming is now in, if a few decades late.
* A new and improved user interface! Still no isomorphism.
* A much better build process, we're done with Batch scripts.

#### Fixes
* The back end has changed so much practically everything's been
fixed in some way. Or at least broken more nicely.

## Building

### Compiling

All commands are assumed to be made from the root project folder

#### Application Programming Interface

```bash
gradlew :api:build
```

#### Command Line Interface

```bash
gradlew :cli:run
gradlew :cli:run -PappArgs="['-i', 'dataChronicKidneyDisease2.txt', '-p', '100', '-g', '500', '-mP', '1.0', '-cP', '1.0']"
```

#### Workbench

To run the client:

```bash
gradlew :client:jfxRun
```

To package a native executable:

```bash
gradlew :client:jfxNative
```