# Installation and Setup

## Generating Jar File
Go to the `limes-core` directory and install using:
```
mvn clean install
```

To create a runnable jar file, include the dependencies using:
```
mvn clean package shade:shade
```

### Importing into Eclipse
In case Eclipse does not recognize the project as Java. Please run the following from the `limes-core/` directory:
```
mvn eclipse:eclipse
```
Then, update the project on Eclipse.
