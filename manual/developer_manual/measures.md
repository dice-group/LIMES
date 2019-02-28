# Measures
LIMES supports a set of metrics that can be used as a part of an LS to link resources (see [user_manual.md](user_manual.md) for more details). Each metric corresponds to one mapper. One mapper can correspond to more that one metric. The `Measures` package is divided into two sub-packages: `Measure` and `Mapper`:

- The `Measure` packages include the interface `IMeasure`, the abstract class `AMeasure`, the set of metrics implemented in LIMES, a `MeasureType` class, a `MeasureFactory` class and a `MeasureProcessor` class:
	
	* The `IMeasure` interface includes all the basic functions that any metric class must implement.
	* The `AMeasure` class implements the `IMeasure` interface.
	* The `MeasureType` class includes all the labels of the metrics implemented in LIMES. 
	* The metrics are divided into type packages based on the type of property they are comparing (*string*, *pointsets*, *space*, *topology* and *space*). Each type package includes an interface and an abstract class, i.e. the *string* type package includes the `IStringMeasure` interface and the `IStringMeasure` abstract class. All metrics that belong to a particular type package must implement the corresponding interface and abstract class. Additionally, the type package abstract class must extend `AMeasure` and implement the corresponding type interface. The type interface must extend the `IMeasure` interface. 
	* The `MeasureFactory` class returns an object of a measure given an input measure name. 
	* The `MeasureProcessor` class is responsible for returning the similarity between two instances, given a simple or complex metric expression and a threshold.
	

- If you want to create a new measure, you have to follow a set of steps:

	* In case a suitable type package exists then:
	
		1. The new metric name must end with the word Measure.
		2. The new metric must extend the type abstract class.
		3. The new metric must implement all overridden methods of the type interface. No unsupported functions are allowed.
		4. Go to the `MeasureType` class and add a representative name of that metric.
		5. Go to the `MeasureFactory` class and create a `public static final` field of String type using the same name of metric as the one used in the `MeasureType` class i.e. `public static final String COSINE = "cosine";` and then add the choice of returning this measure at the `getMeasureType` and `createMeasure` functions following the pattern used for other metrics.

	* In case you want to create a new type package:

		1. Create a new type abstract class that extends `AMeasure` implements `IStringMeasure`.
		2. Create a new type interface that extends `IMeasure`.
		3. Follow steps 1-5 described above.

- The `Mapper` has almost the same hierarchy as the `Measure` package. The `Mapper` packages include the interface `IMapper`, the abstract class `AMapper`, a `MapperFactory` class and a `MappingOperations` class:
	
	* The `IMapper` interface includes all the basic functions that any mapper class must implement.
	* The `AMapper` class implements the `IMapper` interface.
	* The mappers are divided into type packages based on the type of property their corresponding metrics are comparing (string, pointsets, space, topology and space).  All mappers must extend the `AMapper` abstract class.
	* The `MapperFactory` class returns an object of a mapper given an input measure name. 
	* The `MappingOperations` includes the basic functions between mappings.

- If you want to create a new mapper, you have to follow a set of steps:

	1. The new mapper name must include the word Mapper at the end.
	2. The new mapper must extend the `AMapper` abstract class as a result, it must implement all overridden methods of the `IMapper` interface. No unsupported functions are allowed.
	3. Go to the `MapperFactory` class and add the choice of returning this mapper at the `createMapper` function following the pattern used for other mappers.
