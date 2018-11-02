# Iconic Project

## Build Status

## What's New

### Release Notes - 0.7.0 - November 2nd, 2018

#### CLI
* Graph output changed from PDF to PNG, bringing speed improvements
* Logarithmic scaling of axes in the solution fit plot has been disabled
* The ability to choose building blocks (primitives) has been implemented

#### General
* Added link to repository to the “About Iconic Workbench” dialog in the Help section of the menu bar
* Updated range of constants in GEP search from 0 – 100 to -100 – 100.

#### Input Data
* Bugs around copying from Iconic into Excel and vice versa have been fixed
* Deleting values from cells now working correctly
* Can copy, paste and delete using the Edit section of the menu bar

#### Define Search
* Implemented Enable All / Disable All button for building blocks
* Added additional checks and a warning dialog to starting a search with no primitives
* Changed default complexities back to 1

#### Results
* Fixed a bug where results from a CGP search would not plot correctly

### Release Notes - 0.6.0 - October 25th, 2018

#### General
* Changed license to Apache 2.0
* Searching once again supports constants/coefficients
    * GEP searching only
* Project tree now has lovely icons
* Cartesian Genetic Programming has been implemented in the GUI and can now be selected as a search
* Major overhaul of searches in projects
    * The user can now add multiple searches to a project
    * The user can select between Cartesian Genetic Programming and Gene Expression Programming search types
* Various themes are now selectable via the menu bar
    * Select “View” in the menu then select a theme
* Availability of screens now depends on what you have selected in the project tree
    * Selecting a project will only allow you to view “Input Data”
    * Selecting a dataset will allow “Input Data” and “Process Data”
    * Selecting a search will allow “Define Search”, “Start Search” and “Results”

#### Input Data
* Copy/paste into/from excel is now supported
* Empty values are now denoted via blank cells instead of 0.0

#### Process Data
* Handle missing values has been implemented and supports the following
    * Copy previous row
    * Set to mean
    * Set to median
    * Set to 0
    * Set to 1
* Remove outliers is implemented. Values outside of range will be set to null (so handle missing values must be run after this)
    * The user specifies a threshold. If the distance between a data point and the mean value is greater than the threshold * IQR (inter-quartile range), it is removed
* Order of operations now displayed when selecting preprocessing functions
    * First come first executed basis
    * Hovering over the order number will display a tooltip explaining the order numbers
* User can now specify a smoothing window in the GUI
* Additional error checking for preprocessor inputs

#### Define Search
* Has two different views to support setting parameters of both search types
    * CGP
        * Target expression
        * Dataset
        * Error function (Mean squared error only)
        * Population size
        * Generations (set to 0 to run indefinitely)
        * Number of outputs, columns, rows and levels back
        * Mutation function and rate
        * Building Blocks
    * GEP
        * Target expression
        * Dataset
        * Error function (Mean squared error only)
        * Population size
        * Generations (set to 0 to run indefinitely)
        * Head length (1-5)
        * Mutation function and rate (Simple expression mutation only)
        * Crossover function and rate (Simple expression crossover only)
        * Building blocks
* Attempting to select a dataset with missing values will display an error prompt
* Tool tips added when hovering over individual option labels
* Building block names have been standardised to use words instead of symbols
* A default set of building blocks are now enabled by default
* Double-clicking a building block will toggle it on/off
* Various cleanup and polish

#### Start Search
* Progress over time graph now updates in real time as the search progresses
    * Significant speed improvements since last unofficial release to client
* Search statistics now update in real time; additional information added to the statistics box
* Console/log has been overhauled and now supports copying
* Pausing and resuming a search is now supported properly
    * The user may change the following when a search is paused:
        * Mutation rate
        * Crossover rate
        * Number of generations
        * Building blocks

#### Results
* Actual vs predicted plot implemented
* User can right click -> copy an expression from the results table
* Expressions are now simplified using a custom implementation
* Expressions now display the full feature name instead of F1, F2, etc.
* Results table now automatically sorts by lowest error
* Various cleanup and polish

#### Reports
* Reports view is disabled as the feature is currently not implemented

### Release Notes - 0.5.0 - October 11th, 2018

#### General
* Added license (MIT) to each source file
    * Utilises a build plugin to ensure the license exists

#### Input Data
* Datasets can now be created from scratch
    * Currently defaults to 26 features. This is to be fixed in a future release
* New features can now be added to datasets by scrolling horizontally
* Empty rows and features are now blank until a value is added
* “info” and “name” labels added to info and name rows
    * Previously labelled 1 & 2, with dataset starting from 3
* Datasets with missing headers are now supported
    * Will be replaced with “Missing” and a number appended
* Datasets with missing values no longer cause a crash
    * Missing values will default to 0.0
* Many bug fixes related to importing and editing datasets

#### Process Data
* Handle outliers has been implemented

#### Define Search
* Building blocks now have a default complexity set
    * Although complexity is still not used
* Building blocks now display a description of their function when selected

#### Simple Evolutionary Algorithm for Multi-objective Optimization (SEAMO)
* Two new multi-objective evolutionary algorithms, SEAMO and elitist SEAMO
    * Elitist SEAMO is experimental and very inefficient
* Objective decorator caches fitness results computed by its underlying objective
    * Single active gene mutation benefits very little from caching
* Supports multi-objective type of objective

### Release Notes - 0.4.0 - September 21st, 2018

#### General
* Removed the 500 generation limit on searches
    * Searches now run until manually stopped
* Added a method that takes a prefix expression and converts it into an infix expression

#### Input Data
* Now supports display and editing of feature header and description
* Can add additional rows to the dataset by scrolling down
    * These rows will only persist if a cell is edited
* Renamed buttons
* Spreadsheet view does not show when dataset is not selected
* Added placeholder "Create Dataset" button

#### Process Data
* Preprocessing functions improved
* Preprocessing checkboxes are greyed out when a feature is not selected
* Checkboxes now correlate to each feature individually
* Features will display "modified" if they have transformations applied
*Note: Handle missing values and Remove outliers are not currently implemented in the GUI*

#### Define Search
* Target expression can be defined
* Additional building blocks have been added
* Building blocks can now be toggled on and off
* Building block complexity can now be modified

#### Results
* Now shows a live-updating list of best solutions of each size as they are found

#### Bug Fixes
* Fixed a bug where “offset values” was added cumulatively each time it was applied
    * E.g. entering 5 as an offset, then changing the offset to 10 would cause the total offset to be 15

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