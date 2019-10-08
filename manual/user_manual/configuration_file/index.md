# Components of a LIMES XML Configuration File

A LIMES configuration file consists of ten parts, of which some are optional.

## Metadata

The `metadata` tag always consists of the following bits of XML:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE LIMES SYSTEM "limes.dtd">
<LIMES>
```

## Prefixes

Defining a prefix in a LIMES file demands setting two values: 
 The `namespace` that will be addressed by the prefix's `label`

```xml
<PREFIX>
    <NAMESPACE>http://www.w3.org/1999/02/22-rdf-syntax-ns#</NAMESPACE>
    <LABEL>rdf</LABEL>
</PREFIX>
```

Here, we set the prefix `rdf` to correspond to `http://www.w3.org/1999/02/22-rdf-syntax-ns#`. A LIMES link specification can contain as many prefixes as required.

## Data Sources

LIMES computes links between items contained in two Linked Data sources dubbed source and target. Both source and target need to be configured using the same tags. An example of a configuration for both data sources is shown below.

```xml
<SOURCE>
    <ID>mesh</ID>
    <ENDPOINT>http://mesh.bio2rdf.org/sparql</ENDPOINT>
    <VAR>?y</VAR>
    <PAGESIZE>5000</PAGESIZE>
    <RESTRICTION>?y rdf:type meshr:Concept</RESTRICTION>
    <PROPERTY>dc:title</PROPERTY>
    <TYPE>sparql</TYPE>
</SOURCE>    
<TARGET>
    <ID>linkedct</ID>
    <ENDPOINT>http://data.linkedct.org/sparql</ENDPOINT>
    <VAR>?x</VAR>
    <PAGESIZE>5000</PAGESIZE>
    <RESTRICTION>?x rdf:type linkedct:condition</RESTRICTION>
    <PROPERTY>linkedct:condition_name</PROPERTY>
</TARGET>
```

Six properties need to be set. 

* Each data source must be given an ID via the tag `ID`.
* The endpoint of the data source needs to be explicated via the `ENDPOINT` tag. 
    + If the data is to be queried from a SPARQL end point, the `ENDPOINT` tag must be set to the corresponding SPARQL endpoint URI.
    + In case the data is stored in a local file (CSV, N3, TURTLE, etc.), `ENDPOINT` tag must be set to the absolute path of the file containing the data.
* The `VAR` tag describes the variable associated with the aformentioned endpoint. This variable is also used later, when specifying the metric used to link the entities retrieved from the source and target endpoints.
* The fourth property is set via the `PAGESIZE` tag. This property must be set to the maximal number of triples returned by the SPARQL endpoint. For example, the [DBpedia endpoint](http://dbpedia.org/sparql) returns a maximum of 1000 triples for each query. LIMES' SPARQL module can still retrieve all relevant instances for the mapping even the value is set. If the SPARQL endpoint does not limit the number of triples it returns or if the input is a file, the value of `PAGESIZE` should be set to -1. 
* The restrictions on the queried data can be set via the `RESTRICTION` tag. This tag allows to constrain the entries that are retrieved the LIMES' query module. In this particular example, we only use instances of MESH concepts. Additionally, multiple `RESTRICTION` tags are allowed per data source.
* The `PROPERTY` tag allows to specify the properties that will be used during the linking. It is important to note that the property tag can also be used to specify the preprocessing on the input data. For example, setting `rdfs:label AS nolang`, one can ensure that the language tags get removed from each `rdfs:label` before it is written in the cache. Pre-processing functions can be piped into one another by using `->`. For example, `rdfs:label AS nolang->lowercase` will compute `lowercase(nolang(rdfs:label))`. If you are not sure if all your entities have a certain property you can use the `OPTIONAL_PROPERTY` tag instead of `PROPERTY`. Additionally, multiple `PROPERTY` tags are allowed per data source.

Optional properties can be set to segment the requested dataset.

* The graph of the endpoint can be specified directly ofter the `ENDPOINT` tag using the `GRAPH` tag.
* The limits of the query can be set with the `MINOFFSET` and `MAXOFFSET` tags directly after the `PAGESIZE` tag. The resulting query will ask about the statements in the interval [`MINOFFSET`, `MAXOFFSET`]. Note that `MINOFFSET` must be smaller than `MAXOFFSET`! If both `SOURCE` and `TARGET` are restricted, a warning is generated.

### Pre-processing Functions
#### Simple

Currently, LIMES supports the following set of pre-processing functions:
* `nolang` for removing language tags
* `lowercase` for converting the input string into lower case
* `uppercase` for converting the input string into upper case 
* `number` for ensuring that only the numeric characters, "." and "," are contained in the input string
* `replace(String a,String b)` for replacing each occurrence of `a` with `b`
* `regexreplace(String x,String b)` for replacing each occurrence the regular excepression `x` with `b`
* `cleaniri` for removing all the prefixes from IRIs
* `celsius` for converting Fahrenheit to Celsius
* `fahrenheit` for converting Celsius to Fahrenheit
* `removebraces` for removing the braces
* `regularAlphabet` for removing nun-alphanumeric characters
* `uriasstring` returns the last part of a URI as a String. Additional parsing `_` as space

Sometimes, generating the right link specification might either require merging property values (for example, the `dc:title` and `foaf:name` of MESH concepts) or splitting property values (for example, comparing the label and `foaf:homepage` of source instances and the `foaf:homepage` of target instances as well as `foaf:homepage AS cleaniri` of the target instances with the `rdfs:label` of target instances. To enable this, LIMES provides the `RENAME` operator which simply store either the values of a property or the results of a preprocessing into a different property field. For example, `foaf:homepage AS cleaniri RENAME label` would store the homepage of an object without all the prefixes in the name property. The user could then access this value during the specification of the similarity measure for comparing sources and target instances. Note that the same property value can be used several times. Thus, the following specification fragment is valid and leads to the the `dc:title` and `foaf:name` of individuals)  of MESH concepts being the first cast down to the lowercase and then merged to a single property.
```xml
<SOURCE>
    <ID>mesh</ID>
    <ENDPOINT>http://mesh.bio2rdf.org/sparql</ENDPOINT>
    <VAR>?y</VAR>
    <PAGESIZE>5000</PAGESIZE>
    <RESTRICTION>?y rdf:type meshr:Concept</RESTRICTION>
    <PROPERTY>dc:title AS lowercase RENAME name</PROPERTY>
    <PROPERTY>foaf:name AS lowercase RENAME name</PROPERTY>
    <TYPE>sparql</TYPE>
</SOURCE>
```

In addition, the following allows splitting the values of `foaf:homepage` into the property values `name` and `homepage`.

```xml
<SOURCE>
    <ID>mesh</ID>
    <ENDPOINT>http://mesh.bio2rdf.org/sparql</ENDPOINT>
    <VAR>?y</VAR>
    <PAGESIZE>5000</PAGESIZE>
    <RESTRICTION>?y rdf:type meshr:Concept</RESTRICTION>
    <PROPERTY>foaf:homepage AS lowercase RENAME homepage</PROPERTY>
    <PROPERTY>foaf:homepage AS cleaniri->lowercase RENAME name</PROPERTY>
    <TYPE>sparql</TYPE>
</SOURCE>
```

#### Complex 

It is also possible to use complex pre-processing functions, i.e. functions that manipulate multiple values at once.
Currently the following complex pre-processing functions are available:
* `concat(property1, property2, glue=",") RENAME newprop` this concatenates the values of `property1` and `property2` into `newprop` using a comma as glue between the values. The use of the `glue=` argument is optional. So `concat(property1, property2) RENAME newprop` would be valid as well.
* `split(property, splitChar=",") RENAME prop1,prop2` this splits the values of `property` on the comma character, the first part is stored in `prop1` the rest in `prop2`. The `splitChar=` argument is mandatory. You can control the maximum number of splits by providing more or less properties after the `RENAME` keyword. For example if you would write `RENAME prop1,prop2,prop3` the values of `property` would be split into at most 3 values if 3 or more commas are present.
* `toWktPoint(property1, property2) RENAME wktPoint` this takes the values of property1 and property2 and stores them as `POINT(p1 p2)`. The values of the two properties have to be numbers.

Complex pre-processing functions are provided using the `FUNCTION` tag.

```xml
<SOURCE>
    <ID>mesh</ID>
    <ENDPOINT>http://dbpedia.org/sparql</ENDPOINT>
    <VAR>?y</VAR>
    <PAGESIZE>5000</PAGESIZE>
    <RESTRICTION>?y rdf:type dbo:Airport</RESTRICTION>
    <PROPERTY>rdfs:label</PROPERTY>
    <PROPERTY>geo:lat</PROPERTY>
    <PROPERTY>geo:long</PROPERTY>
    <FUNCTION>toWktPoint(geo:lat, geo:long) RENAME geoWktPoint</FUNCTION>
    <TYPE>sparql</TYPE>
</SOURCE>
```

Please note that complex functions always require the `RENAME` operation.


### Source types

A source type can be set via `TYPE`. The default type is set to `SPARQL`
(for a SPARQL endpoint) but LIMES also supports reading files directly from
the hard-drive. The supported data formats are:
* `CSV`: Character-separated file can be loaded directly into LIMES.
  Note that the separation character is set to `TAB` as a default. 
  The user can alter this setting programmatically. 
* `N3` (which also reads `NT` files) reads files in the `N3` language.
* `N-TRIPLE` reads files in W3C's core 
  [N-Triples format](http://www.w3.org/TR/rdf-testcases/\#ntriples)
* `TURTLE` allows reading files in the
  `Turtle` [syntax](http://www.w3.org/TR/turtle/).

Moreover, if you want to download data from a SPARQL endpoint, there is
no need to set the `<TYPE>` tag. 
Instead, if you want to read the source (or target) data from a file,
you should fill `<ENDPOINT>` tag with the absolute path of the input file, 
.g. `<ENDPOINT>C:/Files/dbpedia.nt</ENDPOINT>`, and you should also set the
`<TYPE>` tag  with the type of the input data, for example `<TYPE>NT</TYPE>`.

## Metric Expression for Similarity Measurement

One of the core improvements of the newest LIMES kernels is the provision of a highly flexible
language for the specification of complex metrics for linking
(set by using the `METRIC` tag as exemplified below).

```xml
<METRIC>
    trigrams(y.dc:title, x.linkedct:condition_name)
</METRIC>
```

In this example, we use the `trigrams` metric to compare the `dc:title` of
the instances retrieved from the source data source (with which the
variable `y` is associated) with the `linkedct:condition` (with which the
variable `x` is associated).

For detailed instructions on how to assemble a valid link specification
and a complete catalogue of all measure types included in LIMES, see [Defining Link Specifications](defining_link_specifications.md)


## Execution (optional)

Three LIMES execution parameters could be set here:
 * `REWRITER`: LIMES 1.0.0 implements the `DEFAULT` rewriter.
 * `PLANNER`: the user can choose between:
 	* `CANONICAL`: It generates an immutable plan in a static manner.
 	* `HELIOS`: It generates an immutable plan in a dynamic manner.
 	* `DYNAMIC`: It generates an mutable plan in a dynamic manner.
 	* `DEFAULT`: same as `CANONICAL`.
 * `ENGINE`: the user can choose between:
 	* `SIMPLE`: It executes each independent part of the plan sequentially.
 	* `DEFAULT`: same as `SIMPLE`.
 
If not set, the `DEFAULT` value for each parameter will be used.

## Machine Learning (optional)

In most cases, finding a good link specification (i.e. one that achieves high F-Measure)
is not a trivial task.
Therefore, we implemented a number of machine learning algorithm for auto-generation
of link specifications.
In order to use a machine learning algorithm in your configuration file,
put the `MLALGORITHM` tag instead of the `METRIC` tag.
For example:

```xml
<MLALGORITHM>
    <NAME>wombat simple</NAME>
    <TYPE>supervised batch</TYPE>
    <TRAINING>trainingData.nt</TRAINING>
    <PARAMETER> 
        <NAME>max execution time in minutes</NAME>
        <VALUE>60</VALUE>
    </PARAMETER>
</MLALGORITHM>
```

For more information on the meaning and possible values of the elements within the `MLALGORITHM` tag,
please refer to [Defining Machine Learning Tasks]{./defining_ml_tasks.md}.

## Granularity (optional)
The user can choose positive integers to set the granularity of HYPPO, HR3 or ORCHID by setting

    <GRANULARITY>2</GRANULARITY>.

## Acceptance Condition
Filling the acceptance condition consists of setting the threshold value to the minimum value that two instances must have in order to satisfy a relation. This can be carried out as exemplified below. 

```xml
<ACCEPTANCE>
    <THRESHOLD>0.98</THRESHOLD>
    <FILE>accepted.nt</FILE>
    <RELATION>owl:sameAs</RELATION>
</ACCEPTANCE>
```

By using the `THRESHOLD` tag, the user can set the minimum value that two instances must have in order to satisfy the relation specified in the `RELATION` tag, i.e., `owl:sameAs` in our example. Setting the tag `<FILE>` allows to specify where the links should be written. Currently, LIMES produces output files in the N3 format.

Future versions of LIMES will allow to write the output to other streams and in other data formats.

## Review Condition
Setting the condition upon which links must be reviewed manually is very similar to setting the acceptance condition as shown below.

```xml
<REVIEW>
    <THRESHOLD>0.95</THRESHOLD>
    <FILE>reviewme.nt</FILE>
    <RELATION>owl:sameAs</RELATION>
</REVIEW>
```

All instances that have a similarity between the threshold set in `REVIEW` (0.95 in our example) and the threshold set in `ACCEPTANCE` (0.98 in our example) will be written in the review file and linked via the relation set in `REVIEW`. 
 
## Output Format
The user can choose between `TAB` and `N3` as output format by setting

```xml
<OUTPUT>N3</OUTPUT>
```

Finally, the LIMES configuration file should be concluded with

```xml
</LIMES>
```