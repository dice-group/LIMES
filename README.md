# LIMES - Link Discovery Framework for Metric Spaces.

[![Build Status](https://github.com/dice-group/LIMES/actions/workflows/run-tests.yml/badge.svg?branch=master&event=push)](https://github.com/dice-group/LIMES/actions/workflows/run-tests.yml)
[![DockerHub](https://badgen.net/badge/dockerhub/dicegroup%2Flimes/blue?icon=docker)](https://hub.docker.com/r/dicegroup/limes)
[![GNU Affero General Public License v3.0](https://badgen.net/badge/license/GNU_Affero_General_Public_License_v3.0/orange)](./LICENSE)
![Java 11+](https://badgen.net/badge/java/11+/gray?icon=maven)


## Running LIMES

To bundle LIMES as a single jar file, do

```bash
mvn clean package shade:shade -Dmaven.test.skip=true
```

Then execute it using

```bash
java -jar limes-core/target/limes-core-${current-version}.jar
```

## Using Docker

For running LIMES server in Docker, we expose port 8080. The image accepts the same arguments as the
limes-core.jar, i.e. to run a configuration at `./my-configuration`:

```bash
docker run -it --rm \
  -v $(pwd):/data \
  dicegroup/limes:latest \
    /data/my-configuration.xml
```

To run LIMES server:

```bash
docker run -it --rm \
  -p 8080:8080 \
  dicegroup/limes:latest \
    -s
```

To build and run Docker with WordNet:

```bash
docker build -f wordnet.Dockerfile . -t limes-wordnet

docker run -it --rm \
  -v $(pwd):/data \
  limes-wordnet \
    /data/my-configuration.xml
```

## Maven

```xml

<dependencies>
    <dependency>
        <groupId>org.aksw.limes</groupId>
        <artifactId>limes-core</artifactId>
        <version>1.7.5</version>
    </dependency>
</dependencies>
```

```xml

<repositories>
    <repository>
        <id>maven.aksw.internal</id>
        <name>University Leipzig, AKSW Maven2 Internal Repository</name>
        <url>http://maven.aksw.org/repository/internal/</url>
    </repository>

    <repository>
        <id>maven.aksw.snapshots</id>
        <name>University Leipzig, AKSW Maven2 Snapshot Repository</name>
        <url>http://maven.aksw.org/repository/snapshots/</url>
    </repository>
</repositories>
```

## How to cite

```bibtex
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
