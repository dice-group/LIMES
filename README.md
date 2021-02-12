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
@article{KI_LIMES_2021,
  title={{LIMES - A Framework for Link Discovery on the Semantic Web}},
  author={Axel-Cyrille {Ngonga Ngomo} and Mohamed Ahmed Sherif and Kleanthi Georgala and Mofeed Hassan and Kevin Dreßler and Klaus Lyko and Daniel Obraczka and Tommaso Soru},
  journal={KI-K{\"u}nstliche Intelligenz, German Journal of Artificial Intelligence - Organ des Fachbereichs "Künstliche Intelligenz" der Gesellschaft für Informatik e.V.},
  year={2021},
  url = {https://papers.dice-research.org/2021/KI_LIMES/public.pdf},
  publisher={Springer}
}
```

## More details

* [Demo](https://dice-research.org/LIMES)
* [User manual](http://dice-group.github.io/LIMES/#/user_manual/index)
* [Developer manual](http://dice-group.github.io/LIMES/#/developer_manual/index)



