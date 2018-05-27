# Iconic Project

## Build Status

## What's New

### Release Notes - 0.1.0 - May 28th, 2018

#### Improvements

* Gene Expression Programming is now in, if a few decades late.
* A new and improved user interface! Still no isomorphism.
* A much better build process, we're done with Batch scripts.

#### Fixes

* The back end has changed so much practically everything's been
fixed in some way. Or at least broken more nicely.

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