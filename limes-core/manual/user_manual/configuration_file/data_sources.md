# Data Sources
LIMES computes links between items contained in two Linked Data sources dubbed source and target. Both source and target need to be configured using the same tags. An example of a configuration for both data sources is shown below.

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


Six properties need to be set. 

* Each data source must be given an ID via the tag `ID`.
* The endpoint of the data source needs to be explicated via the `ENDPOINT` tag. 
    + If the data is to be queried from a SPARQL end point, the `ENDPOINT` tag must be set to the corresponding SPARQL endpoint URI.
    + In case the data is stored in a local file (CSV, N3, TURTLE, etc.), `ENDPOINT` tag must be set to the absolute path of the file containing the data.
* The `VAR` tag describes the variable associated with the aformentioned endpoint. This variable is also used later, when specifying the metric used to link the entities retrieved from the source and target endpoints.
* The fourth property is set via the `PAGESIZE` tag. This property must be set to the maximal number of triples returned by the SPARQL endpoint. For example, the [DBpedia endpoint](http://dbpedia.org/sparql) returns a maximum of 1000 triples for each query. LIMES' SPARQL module can still retrieve all relevant instances for the mapping even the value is set. If the SPARQL endpoint does not limit the number of triples it returns or if the input is a file, the value of `PAGESIZE` should be set to -1. 
* The restrictions on the queried data can be set via the `RESTRICTION` tag. This tag allows to constrain the entries that are retrieved the LIMES' query module. In this particular example, we only instances of MESH concepts. 
* The `PROPERTY` tag allows to specify the properties that will be used during the linking. It is important to note that the property tag can also be used to specify the preprocessing on the input data. For example, setting `rdfs:label AS nolang`, one can ensure that the language tags get removed from each `rdfs:label` before it is written in the cache. Pre-processing functions can be piped into one another by using `->`. For example, `rdfs:label AS nolang->lowercase` will compute `lowercase(nolang(rdfs:label))`.

## Pre-processing Functions

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
* `uriasstring` returns the last part of an URI as a String. Additional parsing `_` as space

Sometimes, generating the right link specification might either require merging property values (for example, the `dc:title` and `foaf:name` of MESH concepts) or splitting property values (for example, comparing the label and `foaf:homepage` of source instances and the `foaf:homepage` of target instances as well as `foaf:homepage AS cleaniri` of the target instances with the `rdfs:label` of target instances. To enable this, LIMES provides the `RENAME` operator which simply store either the values of a property or the results of a preprocessing into a different property field. For example, `foaf:homepage AS cleaniri RENAME label` would stored the homepage of a object without all the prefixes in the name property. The user could then access this value during the specification of the similarity measure for comparing sources and target instances. Note that the same property value can be used several times. Thus, the following specification fragment is valid and leads to the the `dc:title` and `foaf:name` of individuals)  of MESH concepts being first cast down to the lowercase and then merged to a single property.

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

In addition, the following allows splitting the values of `foaf:homepage` into the property values `name` and `homepage`.


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

In addition, a source type can be set via `TYPE`. The default type is set to `SPARQL` (for a SPARQL endpoint) but LIMES also supports reading files directly from the hard-drive. The supported data formats are
* `CSV`: Character-separated file can be loaded directly into LIMES. Note that the separation character is set to `TAB` as a default. The user can alter this setting programmatically. 
* `N3` (which also reads `NT` files) reads files in the `N3` language.
* `N-TRIPLE` reads files in W3C's core [N-Triples format](http://www.w3.org/TR/rdf-testcases/\#ntriples)
* `TURTLE` allows reading files in the `Turtle` [syntax](http://www.w3.org/TR/turtle/).

Moreover, if you want to download data from a SPARQL endpoint, there is no need to set the `<TYPE>` tag. 
Instead, if you want to read the source (or target) data from a file, you should fill `<ENDPOINT>` tag with the absolute path of the input file, e.g. `<ENDPOINT>C:/Files/dbpedia.nt</ENDPOINT>`, and you should also set the `<TYPE>` tag  with the type of the input data, for example `<TYPE>NT</TYPE>`.