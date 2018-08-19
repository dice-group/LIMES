# Defining Link Specifications

Links Specifications (LS) can be simple or complex.
A simple LS just consists of the measure name together with the arguments to the measure.
Possible arguments to a measure are all properties as defined in [Data Sources](./index.md#data-sources). 
A possible simple LS is shown in the following example:
```
<METRIC>trigrams(x.rdfs:label,y.dc:title)</METRIC>
```
While such simple metrics can be used in many cases, complex metrics are
necessary in complex linking cases.
LIMES includes a formal grammar for specifying complex configurations of
arbitrary complexity.
For this purpose, two categories of binary operations are supported:

1. Metric operations and 
2. Boolean operations.

## Metric operations

Metric operations allow to combine metric values. They include the operators
`MIN`, `MAX` and `ADD` e.g. as follows:

```
MAX(trigrams(x.rdfs:label,y.dc:title)|0.3,euclidean(x.lat|long, y.latitude|longitude)|0.5).
```

This specification computes the maximum of:

1. The trigram similarity of x's `rdfs:label` and y's `dc:title` is greater or equal to 0.3
2. The 2-dimension euclidean distance of `x`'s `lat` and `long` with `y`'s `latitude` and `longitude`, i.e.,  $$ \sqrt{((x.lat- y.latitude)^2 + (x.long - y.longitude)^2)} $$ is greater or equal to 0.5. 

Note that euclidean supports arbitrarily many dimensions. In addition, note that `ADD` allows to define weighted sums as follows:`ADD(0.3*trigrams(x.rdfs:label,y.dc:title)|0.3, 0.7*euclidean(x.lat|x.long,y.latitude|y.longitude)|0.5)`.

We call `trigrams(x.rdfs:label,y.dc:title)|0.3` the left child of the specification and `euclidean(x.lat|long, y.latitude|longitude)|0.5` the right child of the specification. Both children specifications are simple specifications and combined with a metric operator, they create a complex specification. LIMES gives the user the opportunity to combine **exactly two ** complex or simple spefications to create a new complex specification. Note that each child specification must be accompanied by its own threshold.

## Boolean operations

Boolean operations allow to combine and filter the results of metric operations and include `AND`, `OR`, `DIFF`, e.g. as `AND(trigrams(x.rdfs:label,y.dc:title)|0.9, euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7)`.
    
This specification returns all links such that:
    
1. the trigram similarity of `x`'s `rdfs:label` and `y`'s `dc:title` is greater or equal to 0.9 and
2. the 2-dimension euclidean distance of `x`'s `lat` and `long` mit y's `latitude` and `longitude` is greater or equal to `0.7`.
    
We call `trigrams(x.rdfs:label,y.dc:title)|0.9` the left child of the speficiation and `euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7`the right child of the specification. Both children specifications are simple specifications and combined with a boolean operator, they create a complex specification.
LIMES gives the user the opportunity to combine **exactly two** complex or simple spefications to create a new complex specification. Note that each child specification must be accompanied by its own threshold.

## Implemented Measures

Measures are organized in packages, based on the type of resource they are designed to operate with. 
Several measure packages ship with LIMES and it is easy to extend it with your own.

The current version of LIMES ships with the following measure packages included:

* string
* vector space
* pointset
* topological
* temporal
* resource set

More complex distance measures are being added continuously.
 
### String Measures

The string measures package consists of the following measures: 

* `Cosine`
* `ExactMatch`
* `Jaccard`  
* `Jaro`
* `JaroWinkler` 
* `Levenshtein`
* `MongeElkan` 
* `Overlap`
* `Qgrams` 
* `RatcliffObershelp`
* `Soundex`
* `Trigram`

Example of atomic LS that consists of the string measure  `Trigram` and a threshold `theta = 0.8`:

`trigram(x.label, y.title) | 0.8`

where `label` and `title` are properties of the source and target KB reps., whose values are strings. 


### Vector Space Measures
  
LIMES supports comparing numeric vectors by using the vector space measures package consisting of the following measures:

* `Euclidean` metric 
 - e.g. `euclidean(a.wgs84:lat|wgs84:long,b.wgs84:lat|wgs84:long)`
* `Geo_Orthodromic` distance 
* `Geo_Great_Elliptic` distance

### Point-Set Measures
  
The similarity between polygons can be measured by using the following point-set distances:

* `Geo_Centroid_Indexed_Hausdorff` 
* `Geo_Fast_Hausdorff` 
* `Geo_Hausdorff` 
* `Geo_Indexed_Hausdorff` 
* `Geo_Naive_Hausdorff`
* `Geo_Scan_Indexed_Hausdorff`
* `Geo_Symmetric_Hausdorff`
* `Geo_Max`
* `Geo_Min`
* `Geo_Mean`
* `Geo_Avg`
* `Geo_Frechet`
* `Geo_Link`
* `Geo_Sum_Of_Min`
* `Geo_Naive_Surjection`
* `Geo_Fair_Surjection`

The threshold $$\theta$$ corresponds to the distance $$x$$ between two points in km via the following formula:
$$\theta = \frac{1}{1+x}$$.

### Topological Measures
  
The topological relations between spatial resources can be found by using the following relations:

* `Top_Contains`
* `Top_Covered_By`
* `Top_Crosses`
* `Top_Disjoint`
* `Top_Equals`
* `Top_Intersects`
* `Top_Overlaps`
* `Top_Touches`
* `Top_Within`


### Temporal Measures
  
The temporal relations between event resources can be found by using the following relations:

* `Tmp_Concurrent`: given a source and a target KB, `Tmp_Concurrent` links the source and the target events that have the same begin date and were produced by the same machine. For example: Tmp_Concurrent(x.beginDate1|machine1,y.beginDate2|machine2)|1.0 
* `Tmp_Predecessor`: given a source and a target KB, `Tmp_Predecessor` links the source events to the set of target events that happen exactly before them. For example: Tmp_Predecessor(x.beginDate1,y.beginDate2)|1.0. If the `Tmp_Predecessor` measure is used in a complex LS, the `CANONICAL` planner should be used. 
* `Tmp_Successor`: given a source and a target KB, `Tmp_Successor` links the source events to the set of target events that happen exactly after them. For example: Tmp_Successor(x.beginDate1,y.beginDate2)|1.0. If the `Tmp_Successor` measure is used in a complex LS, the `CANONICAL` planner should be used. 



Allen's temporal relations (https://en.wikipedia.org/wiki/Allen's_interval_algebra):

* `Tmp_After`
* `Tmp_Before`
* `Tmp_During`
* `Tmp_During_Reverse`
* `Tmp_Equals`
* `Tmp_Finishes`
* `Tmp_Finishes`
* `Tmp_Is_Finished_By`
* `Tmp_Is_Met_By`
* `Tmp_Is_Overlapped_By`
* `Tmp_Is_Started_By`
* `Tmp_Meets`
* `Tmp_Overlaps`
* `Tmp_Starts`


Example of atomic LS that consists of the temporal measure  `Tmp_Finishes` and a threshold `theta = 1.0`:

`Tmp_Finishes(x.beginDate1|endDate1, y.beginDate2|endDate2) | 0.8`

where `beginDate1` and `beginDate2` are properties of the source and target KB reps. whose values indicate the begin of a temporal event instance and `endDate1` and `endDate2` are properties of the source and target KB reps. whose values indicate the end of a temporal event instance. Both begin and end properties for both source and target MUST be included in an atomic LS whose measure is temporal. Also, the acceptable values for all properties are in the format: `2015-04-22T11:29:51+02:00`.


### Resource-Set Measures
  
To compare sets of resources (e.g. in rdf containers), use the following relations:

* `Set_Jaccard`

Please note that rdf collections are not supported yet.