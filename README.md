# LIMES - Link Discovery Framework for Metric Spaces.

[![Build Status](https://travis-ci.org/dice-group/LIMES.svg?branch=master)](https://travis-ci.org/dice-group/LIMES)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f565ba159e0340ad9dd3e5de41b12ed9)](https://www.codacy.com/app/MSherif/LIMES?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dice-group/LIMES&amp;utm_campaign=Badge_Grade)

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

The `limes-gui/target/jfx/app/lib` folder needs to be in the same folder as the .jar for the .jar to work!

### Importing into Eclipse
In case Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.


## More details

* [Project web site](http://cs.uni-paderborn.de/ds/research/research-projects/active-projects/limes/)
* [User manual](http://dice-group.github.io/LIMES/user_manual/)
* [Developer manual](http://dice-group.github.io/LIMES/developer_manual/)
