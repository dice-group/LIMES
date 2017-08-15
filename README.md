# LIMES-dev
Repository for LIMES - Link Discovery Framework for Metric Spaces.
=======

[![Build Status](https://travis-ci.org/AKSW/LIMES-dev.svg?branch=dev)](https://travis-ci.org/AKSW/LIMES-dev)

Development branch for LIMES - Link Discovery Framework for Metric Spaces.

## Generating Jar File (Headless)
installing use:
```
mvn clean install
```

Creating the runnable jar file including the dependencies use:
```
mvn clean package shade:shade -Dcheckstyle.skip=true -Dmaven.test.skip=true
```

The .jar will be placed in `limes-core/target/limes-core-VERSION-SNAPSHOT.jar`

## Generating Jar File (GUI)
switch to `limes-gui` and use:
```
mvn jfx:jar -Dcheckstyle.skip=true -Dmaven.test.skip=true
```

The .jar will be placed in `limes-gui/target/jfx/app/limes-GUI-VERSION-SNAPSHOT-jfx.jar`

### Importing into Eclipse
In case Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.


## More details

* [Project web site](http://aksw.org/Projects/LIMES)
* [User manual](http://aksw.github.io/LIMES-dev/user_manual/)
* [Developer manual](http://aksw.github.io/LIMES-dev/developer_manual/)
