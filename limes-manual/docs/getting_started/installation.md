# Installation and Setup

## Building LIMES

LIMES is [publicly available](https://github.com/dice-group/limes) 
offering the entire Java source code as well as indicative configurations.
Java SDK 1.8 (or later) as well as Maven 3.3.3 (or later) must be installed
and properly configured in order to compile and execute DEER.
The `pom.xml` file contains the project's configuration in Maven and has been successfully
tested in Mac OS, Microsoft Windows and Linux environments.
The following building instructions assume that Git is also installed.


### Headless (Client or Server)

In order to build the command line version from source,
first the master branch of LIMES must be cloned to a preferred location
by running:
```bash
git clone -b master --single-branch https://github.com/dice-group/limes.git limes
```
It is recommended to use the `--single-branch` parameter to save some time and avoid pulling the
whole history of the project.
Then the following commands need to be executed in order to create the
runnable jar file including the dependencies: 
```bash
cd limes/limes-core/
mvn clean package shade:shade -Dcheckstyle.skip=true -Dmaven.test.skip=true
```
After a successful installation, a target directory should have been created containing the
limes-core-${version}-SNAPSHOT.jar (version depending on POM configuration).

### GUI 

Optionally, LIMES provides a graphical user interface (GUI), which helps the
user build complicated link specifications without having to manually
define rules in XML files.
The GUI installation assumes that the command line version is already
installed and uses the command-line version as a library.
Similarly, the LIMES repository needs to be cloned to a preferred location on
your system by running:
```bash
git clone -b master --single-branch https://github.com/dice-group/limes.git limes
```
Then create the launchable jar file containing the LIMES GUI by running:
```bash
cd limes/limes-gui/
mvn jfx:jar -Dcheckstyle.skip=true -Dmaven.test.skip=true
```

The .jar will be placed in limes-gui/target/jfx/app/limes-gui-${version}-SNAPSHOT-jfx.jar
Note that in order to run the jar, the `lib/` folder generated alongside it has to be on the same folder
as the jar.

## Importing into Eclipse

In case Eclipse does not recognize the project as Java, please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.
