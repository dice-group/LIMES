# LIMES-dev

[![Build Status](https://travis-ci.org/AKSW/LIMES-dev.svg?branch=dev)](https://travis-ci.org/AKSW/LIMES-dev)

Development branch for LIMES - Link Discovery Framework for Metric Spaces.

## Generating Jar File
installing use:
```
mvn clean install
```

Creating the runnable jar file including the dependencies use:
```
mvn clean package shade:shade
```

### Importing into Eclipse
In case Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.

## Binaries
Current releases can be found at https://github.com/AKSW/LIMES.

## More details

* [Project web site](http://aksw.org/Projects/LIMES)
* [User manual](http://aksw.github.io/LIMES-dev/user_manual/)
* [Developer manual](http://aksw.github.io/LIMES-dev/developer_manual/)
