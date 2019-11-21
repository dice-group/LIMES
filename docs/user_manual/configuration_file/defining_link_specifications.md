# Defining Link Specifications

Links Specifications (LS) can be simple or complex.
A simple LS just consists of the measure name together with the arguments to the measure.
Possible arguments to a measure are all properties as defined in [Data Sources](index.md#data-sources).
A possible simple LS is shown in the following example:
```xml
<METRIC>trigrams(x.rdfs:label,y.dc:title)</METRIC>
```
While such simple metrics can be used in many cases, complex metrics are
necessary in complex linking cases.
LIMES includes a formal grammar for specifying complex configurations of
arbitrary complexity.
For this purpose, two categories of binary operations are supported:
../docs/user_manual/configuration_file/defining_link_specifications.md
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

2. The 2-dimension Euclidean distance of `x`'s `lat` and `long` with `y`'s `latitude` and `longitude`, i.e.,  $$ \sqrt{((x.lat- y.latitude)^2 + (x.long - y.longitude)^2)} $$ is greater or equal to 0.5.


Note that euclidean supports arbitrarily many dimensions. In addition, note that `ADD` allows to define weighted sums as follows:`ADD(0.3*trigrams(x.rdfs:label,y.dc:title)|0.3, 0.7*euclidean(x.lat|x.long,y.latitude|y.longitude)|0.5)`.

We call `trigrams(x.rdfs:label,y.dc:title)|0.3` the left child of the specification and `euclidean(x.lat|long, y.latitude|longitude)|0.5` the right child of the specification. Both children specifications are simple specifications and combined with a metric operator, they create a complex specification. LIMES gives the user the opportunity to combine **exactly two** complex or simple specifications to create a new complex specification. Note that each child specification must be accompanied by its own threshold.

## Boolean operations

Boolean operations allow to combine and filter the results of metric operations and include `AND`, `OR`, `MINUS`, e.g. as `AND(trigrams(x.rdfs:label,y.dc:title)|0.9, euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7)`.

This specification returns all links such that:

1. the trigram similarity of `x`'s `rdfs:label` and `y`'s `dc:title` is greater or equal to 0.9 and
2. the 2-dimension euclidean distance of `x`'s `lat` and `long` mit y's `latitude` and `longitude` is greater or equal to `0.7`.

We call `trigrams(x.rdfs:label,y.dc:title)|0.9` the left child of the speficiation and `euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7`the right child of the specification. Both children specifications are simple specifications and combined with a boolean operator, they create a complex specification.
LIMES gives the user the opportunity to combine **exactly two** complex or simple spefications to create a new complex specification. Note that each child specification must be accompanied by its own threshold.

## Implemented Measures

Measures are organized in packages, based on the type of resource they are designed to operate with.
Several measure packages ship with LIMES and it is easy to extend it with your own.

The current version of LIMES ships with the following measure packages:

* String measures
* Vector space measures
* Point-set measures
* Topological measures
* Temporal measures
* Resource-set measures
* Edge-counting semantic measures

More complex distance measures are being added continuously.
We give more details about each of the measure type in the following sections.

### String Measures

The string measures package consists of the following measures:

* **Cosine**: Cosine string similarity is a measure of similarity between two non-zero vectors
representations of the two input strings of an inner product space that measures the cosine of
the angle between them. The outcome of the Cosine string similarity is neatly bounded in $[0,1]$
* **ExactMatch**: Exact match string similarity is a measure of similarity between two input
strings that returns one in case the two input strings were identical, zero otherwise.
* **Jaccard**: The Jaccard index, also known as Intersection over Union and the Jaccard similarity
coefficient (originally coined coefficient de communauté by Paul Jaccard), is a statistic used for
comparing the similarity and diversity of sample sets. The Jaccard coefficient measures similarity
between finite sample sets, and is defined as the size of the intersection divided by the size of
the union of the sample sets. In LIMES, we use trigrams of the input strings to generate our sample sets.
* **Overlap**: The overlap coefficient or Szymkiewicz–Simpson coefficient, is a similarity measure
that measures the overlap between two sets. It is related to the Jaccard index and is defined as
the size of the intersection divided by the smaller of the size of the two sets.
* **Jaro**: The Jaro distance between two strings is related to the minimum number of single-character
transpositions required to change one string into the other.
* **JaroWinkler**: The Jaro–Winkler distance is a string metric for measuring the edit
distance between two sequences. It is a variant proposed in 1990 by William E. Winkler of the Jaro
distance metric. The Jaro–Winkler distance uses a prefix scale which gives more favourable
ratings to strings that match from the beginning for a set prefix length. The lower
the Jaro–Winkler distance for two strings is, the more similar the strings are. The score is
normalized such that 0 equates to no similarity and 1 is an exact match.
* **Levenshtein**: The Levenshtein distance is a string metric for measuring the difference
between two strings. Informally, the Levenshtein distance between two strings is the minimum number
of single-character edits (insertions, deletNullIndexerException(ions or substitutions) required to change one string into
the other. It is named after the Soviet mathematician Vladimir Levenshtein, who considered this
distance in 1965. Normalized Levenshtein distance is computed by dividing the Levenshtein distance
by the length of the input string. Let $d_{lvn}$ be the normalized Levenshtein distance.
Then we define the Levenshtein similarity in LIMES as $\frac{1}{1-d_{lvn}}$.
* **MongeElkan**: The Monge-Elkan similarity measure is a type of hybrid similarity measure that
combines the benefits of sequence-based and set-based methods. This can be effective for domains
in which more control is needed over the similarity measure. In LIMES, we use trigrams of the input
strings to generate our sample string subsets.
* **RatcliffObershelp**: In Ratcliff/Obershelp, we compute the similarity of the two input strings
as the number of matching characters divided by the total number of characters in the two strings.
Matching characters are those in the longest common subsequence plus, recursively, matching characters
in the unmatched region on either side of the longest common subsequence.
* **Soundex**: Soundex is a phonetic algorithm for indexing names by sound, as pronounced in English.
The goal is for homophones to be encoded to the same representation so that they can be matched
despite minor differences in spelling. The algorithm mainly encodes consonants, a vowel will not be
encoded unless it is the first letter. In LIMES, we compute the Soundex similarity score as the reverse of
the distance between the encoding of the two input strings.
* **Koeln**: This phonetic similarity measure uses the Cologne phonetics algorithm which is closely
related to the previously mentioned Soundex algorithm but is optimized to match the German language.
In LIMES, we compute the Koeln similarity score as the reverse of the distance between the Cologne
phonetics encoding of the two input strings.
* **DoubleMetaphone**: This is a phonetic algorithm for indexing words by their English pronunciation.
It was designed based on the Soundex algorithm and aims to deal with most of its shortcomings.
In LIMES, we compute the DoubleMetaphone similarity score as the reverse of the distance between the
DoubleMetaphone encoding of the two input strings.
* **Trigram**: A tri-gram is a group of three consecutive characters taken from a string.
In LIMES, we measure the similarity of two input strings by counting the number of trigrams they share.
Formally, we compute the trigram similarity as the normalized sum of absolute differences between
tri-gram vectors of both the input strings.
* **Qgrams**: Same as trigram but using a group of $q$ (set to four by default) consecutive characters for generating
the q-gram vectors of the input strings.


### Vector Space Measures

LIMES supports comparing numeric vectors by using the vector space measures package consisting of the following measures:

* **Euclidean**: Euclidean metric is the straight-line distance between two points in Euclidean space.
With this distance, Euclidean space becomes a metric space.
For example, `euclidean(a.wgs84:lat|wgs84:long,b.wgs84:lat|wgs84:long)` will compute the Euclidean
distance between the point representations of each resource from the source and target datasets.
* **Manhattan**: Manhattan metric defines the distance between two points as the sum of the absolute differences of their Cartesian coordinates.

### Point-Set Measures

The similarity between polygons can be measured by using the following point-set distances:
NullIndexerException(
* **Geo_Hausdorff**: The Hausdorff distance is a measure of the maximum of the minimum distances
between pairwise points in the two input geometries.
* **Geo_Max**: The idea behind this measure is to compute the overall maximal distance between
pairwise points of the two input geometries.
* **Geo_Min**: The idea of Geo_Min is akin to that of Geo_Max and is defined as minimal distance
between pairwise points of the two input geometries.
* **Geo_Mean**: The mean distance is one of the most efficient distance measures for point sets.
First, a mean point is computed for each point set. Then, the distance be- tween the two means is
computed by using the orthodromic distance.
* **Geo_Avg**: For computing the average point sets distance function, the orthodromic distance
measures between all the source-target geometries’ points pairs is cumulated and divided by the
number of points in the source-target geometries’ point pairs.
* **Geo_Frechet**: The Fréchet distance is a measure of similarity between curves (in our case
geometries representations of the POI resources) that takes into account the location and ordering
of the points along the curves.
* **Geo_Sum_Of_Min**: First, the closest point from the source geometry to each point to the target
geometry is computed. The same operation is carried out with source and target reversed.
Finally, the average of the two values is then the distance value.
* **Geo_Naive_Surjection**: The surjection distance function introduced defines the distance between
two geometries as the minimum distance between the sum of distances of the surjection of the larger
set to the smaller one. A main drawback of the surjection is being biased toward some points ignoring
some others in calculations.
* **Geo_Fair_Surjection**: In order to fix the bias of the Geo_Naive_Surjection, the fair-surjection
distance maps the elements of source geometry as evenly as possible to the elements of the target geometry.
* **Geo_Link**: The Link distance is defined as the minimum orthodromic distance between pairwise
points of the source and target geometries that satisfy the bijection relation.


For all the above measures, the threshold $\theta$ corresponds to the distance $x$ between two
points in km via $\theta = \frac{1}{1+x}$.

### Topological Measures

The topological relations between spatial representations of POI resources can be found by using
the following relations.
In these relations we assume that the first POI resource has a geospatial representation in a form
of geometry a and the target POI resource has a geospatial representation in a form of geometry b:

* **Top_Contains**: A geometry a contains geometry b if and only if no points of b lie in the exterior
of a, and at least one point of the interior of b lies in the interior of a.
* **Top_Covers**: A geometry a covers geometry b if and only if the geometry b lies in a. i.e. no points
of b lie in the exterior of a, or Every point of b is a point of (the interior or boundary of) a.
* **Top_Covered_By**: A geometry a is covered by a geometry b if and only if every point of a is a
point of b, and the interiors of the two geometries have at least one point in common. Note that
Top_Covered_By is the reverse relation of Top_Covers.
* **Top_Crosses**: A geometry a crosses a geometry b if and only if they have some but not all
interior points in common, and the dimension of the intersection is less than that of at least one of them.
* **Top_Disjoint**: Two geometries a and b are disjoint if and only if they have no point in common.
They form a set of disconnected geometries.
* **Top_Equals**: Two geometries a and b are topologically equal if their interiors intersect and
no part of the interior or boundary of one geometry intersects the exterior of the other.
* **Top_Intersects**: A geometry a intersects A geometry b if and only if geometries a and b have
at least one point in common.
* **Top_Overlaps**: A geometry a overlaps a geometry b if and only if they have some but not all
points in common, they have the same dimension and the intersection of the interiors of the two
geometries has the same dimension as the geometries themselves
* **Top_Touches**: Two geometries a and touched if they have at least one boundary point in
common, but no interior points.
* **Top_Within**: A geometry a is within a geometry b if and only if a lies in the interior of b.



### Temporal Measures

The temporal relations between POI resources can be found by using the following relations:

* **Tmp_Concurrent**: given a source and a target KB, Tmp_Concurrent links the source and the target
events that have the same begin date and were produced by the same machine. For example:
`Tmp_Concurrent(x.beginDate1|machine1,y.beginDate2|machine2)|1.0`
* **Tmp_Predecessor**: given a source and a target KB, vmp_Predecessor links the source events to
the set of target events that happen exactly before them. For example:
`Tmp_Predecessor (x.beginDate1, y.beginDate2)|1.0`.
If the Tmp_Predecessor measure is used in a complex LS, the CANONICAL planner should be used.
* **Tmp_Successor**: given a source and a target KB, Tmp_Successor links the source events to the
set of target events that happen exactly after them. For example: `Tmp_Successor (x.beginDate1, y.beginDate2)|1.0`.
If the Tmp_Successor measure is used in a complex LS, the CANONICAL planner should be used.


Moreover, LIMES support the following temporal relations between POI resources based on Allen's algebra:
* **Tmp_After**: The first POI takes place after the second POI takes place.
* **Tmp_Before**: The first POI takes place before the second POI takes place.
* **Tmp_During**: The first POI take place during the second POI takes place.
* **Tmp_During_Reverse**: The second POI take place during the first POI takes place. Tmp_During_Reverse is the reverse of Tmp_During
* **Tmp_Equals**: Both first POI and the second take place concurrently. i.e. both POIs have equal timestamp.
* **Tmp_Finishes**: The first POI finishes in the same time as the second POI finishes.
* **Tmp_Is_Finished_By**: reverse of Tmp_Finishes
* **Tmp_Overlaps**: Part of the first POI timestamp overlaps with the second POI time stamp.
* **Tmp_Is_Overlapped_By**: reverse of Tmp_Overlaps
* **Tmp_Starts**: The start first POI timestamp is the same as the start of the second POI time stamp.
* **Tmp_Is_Started_By**: reverse of Tmp_Starts
* **Tmp_Meets**: The end first POI timestamp meets the start of the second POI time stamp.
* **Tmp_Is_xBy**: reverse of Tmp_Meets

Below, an example of an atomic LS that consists of the temporal measure Tmp_Finishes and a threshold $\theta = 1.0$ is given:
`Tmp_Finishes(x.beginDate1|endDate1, y.beginDate2|endDate2) | 0.8`
where beginDate1 and beginDate2 are properties of the source and target KB respectively, whose values
indicate the begin of a temporal event instance and endDate1 and endDate2 are properties of the source
and target KB respectively, whose values indicate the end of a temporal event instance.
Both begin and end properties for both source and target MUST be included in an atomic LS whose
measure is temporal. Also, the acceptable values for all properties are in the format: `2015-04-22T11:29:51+02:00`.


### Resource-Set Measures

To compare sets of resources (e.g. in rdf containers), use the following relations:

* **Set_Jaccard**: Works much like the jaccard measure for strings with the difference that it expects
the inputs to be sets of RDF resources.

Please note that RDF collections are not supported yet.


### Semantic Measures

In order to use the semantic similarities incorporated into LIMES, you must follow the following steps:
* Visit https://wordnet.princeton.edu/download/current-version
* Go to **WordNet 3.1 DATABASE FILES ONLY** and follow the instructions on how to download version 3.1 files
* Create a folder named wordent/ inside /src/main/resources/
* Unzip the downloaded package from the wordnet website
* Place the dict folder inside /src/main/resources/wordnet/
* Now you are ready to use the semantic similarities

The semantic similarities **require** the WordNet lexical database to run.

To understand the basic idea behind the edge-counting semantic similarites, we define the following:
* a lexical vocabulary as a directed acyclic graph (DAG)
* The set of vertices $$V$$ is a set of concepts $$c_i$$, were each $$c_i$$ stands for a set of synonyms. We denote $$|V|$$ with $$n_V$$.
* $$E \subseteq V \times V$$ is a set of directed edges $$e_{jk} = (c_j, c_k)$$. We denote $$|E|$$ with $$n_E$$.
* The edge $$e_{jk}$$ stand for the hypernymy relations from a parent concept $$c_j$$ to a child concept $$c_k$$. We write $$c_j \rightarrow c_k$$ and we say that $$c_j$$ is a hypernym of $$c_k$$. We also define the hyponymy relation as a directed relation from a child concept $c_k$ to a  parent concept. We write $$c_j \leftarrow c_k$$ and we say that $$c_j$$ is a hyponym of $$c_k$$. Hypernymy and hyponymy are transitive.
* The root $$r$$ is the unique node of dictionary that has no parent concept.
* A leaf concept $$c_i$$ is a concept node with no children concepts.
* A concept as a common subsumer of $$c_1$$ and $$c_2$$ (denoted $$cs(c_1,c_2)$$) iff that concept a hypernym of both $$c_1$$ and $$c_2$$.
* The least common subsumer (LSO) of $$c_1$$ and $$c_2$$ (denoted $$lso(c_1,c_2)$$) as the most specific concept which is an ancestor of both $$c_1$$ and $$c_2$$.
* We define the directed path from $$c_1$$ to $$c_2$$ via a common subsumer $$cs(c_1,c_2)$$ as: $$path(c_1, c_2) = \{c_1 \leftarrow c_i \leftarrow \ldots \leftarrow cs(c_1,c_2) \rightarrow c_j \rightarrow \ldots \rightarrow c_2: i,j,k \in  \mathbb N, i,j,k \leq n_v$$. Note that they are multiple $$path(c_1, c_2)$$ between two concepts.
* $$len(c_1,c_2)$$ is the length of the shortest $$path(c_1, c_2)$$ between two concepts $$c_1$$ and $$c_2$$. Note that $$len$$ define a metric. Hence, it is symmetric and abides by the triangle inequality, i.e., $$len(c_1,c_2) \leq len(c_1,c_3) + len(c_2,c_3)$$ for any $$(c_1, c_2, c_3) \in V^3$$.
* We define $$depth_{m}(c_i)$$ as the length of the shortest path between $$r$$ and $$c_i$$. Analogously, $$depth_{M}(c_i)$$ as the maximum $$depth(c_i)$$. We set $$D = \max\limits_{c \in V} depth_{M}(c)$$.

The semantic measures package consists of the following edge-counting semantic measures:

* **lch**: The lch semantic similarity computes the similarity between two concepts $$c_1, c_2$$ as follows:

$$ LCH(c_1,c_2) = -\log\left(\frac{len(c_1,c_2)}{2D}\right) $$

We use a normalized version of LCH:

$$LCH(c_1,c_2) =
\begin{cases}
1 \mbox{ if } c_1 = c_2\\
\frac{-\log\left(\frac{len(c_1,c_2)}{2D}\right)}{\log(2D)} \mbox{ else.}\\
\end{cases} $$

* **li**: The li semantic similarity computes the similarity between two concepts $$c_1, c_2$$ as follows:

$$LI(c_1,c_2) = e^{-\alpha len(c_1,c_2)} \frac{e^{\beta depth(lso(c_1,c_2))} - e^{-\beta depth(lso(c_1,c_2))}}{e^{\beta depth(lso(c_1,c_2))} + e^{-\beta depth(lso(c_1,c_2))}} $$

We set $$depth(lso(c_1,c_2))= depth_{M}(lso(c_1,c_2))$$, since the original specification does not state which $$depth(lso(c_1,c_2))$$ to use.


* **shortest_path**: The shortest_path similarity computes the similarity between two concepts $$c_1, c_2$$ as follows:

$$SP(c_1,c_2) = 2D - len(c_1,c_2)$$

We use a normalized version of SP:

$$SP(c_1,c_2) = \frac{2D - len(c_1,c_2)}{2D}$$


* **wupalmer**: The wupalmer similarity computes the similarity between two concepts $$c_1, c_2$$ as follows:

$$WU(c_1,c_2) = \frac{2depth_{M}(lso(c_1,c_2))}{2depth_{M}(lso(c_1,c_2))+N_1+N_2}$$

where $$N_1 = len(lso(c_1,c_2),c_1)$$ and $$N_2 = len(lso(c_1,c_2),c_2)$$
