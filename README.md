# LIMES - Link Discovery Framework for Metric Spaces.

[![Build Status](https://travis-ci.org/dice-group/LIMES.svg?branch=master)](https://travis-ci.org/dice-group/LIMES)

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

### Importing into Eclipse
In case Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.
## How to cite
```
@inproceedings{Ngomo2011LIMESA,
  title={LIMES - A Time-Efficient Approach for Large-Scale Link Discovery on the Web of Data},
  author={Axel-Cyrille Ngonga Ngomo and S{\"o}ren Auer},
  booktitle={IJCAI},
  year={2011}
}
```

## More details

* [Demo](https://dice-research.org/LIMES)
* [User manual](http://dice-group.github.io/LIMES/#/user_manual/index)
* [Developer manual](http://dice-group.github.io/LIMES/#/developer_manual/index)



