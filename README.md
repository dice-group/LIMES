# LIMES-dev

[![Build Status](https://travis-ci.org/AKSW/LIMES-dev.svg?branch=dev)](https://travis-ci.org/AKSW/LIMES-dev)

Development branch for LIMES - Link Discovery Framework for Metric Spaces.

## Known issues

### Could not transfer artifact
Artifact `fr.ign.cogit:geoxygene-api:pom:1.6` cannot be transferred due to a certificate problem. Please run:
```
mvn clean install -U -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.test.skip=true
```

### Importing into Eclipse
Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.

## Binaries
Current releases can be found at https://github.com/AKSW/LIMES.

## More details

* [Project web site](http://aksw.org/Projects/LIMES)
* [User manual](https://github.com/AKSW/LIMES-dev/blob/dev/limes-core/manual/user_manual.md)
* [Developer manual](https://github.com/AKSW/LIMES-dev/blob/dev/limes-core/manual/developer_manual.md)
