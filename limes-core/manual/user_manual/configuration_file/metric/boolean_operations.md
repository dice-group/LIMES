#Boolean operations
Boolean operations allow to combine and filter the results of metric operations and include `AND`, `OR`, `DIFF`, e.g. as `AND(trigrams(x.rdfs:label,y.dc:title)|0.9, euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7)`.

This specification returns all links such that:

1. the trigram similarity of `x`'s `rdfs:label` and `y`'s `dc:title` is greater or equal to 0.9 and
2. the 2-dimension euclidean distance of `x`'s `lat` and `long` mit y's `latitude` and `longitude` is greater or equal to `0.7`.

We call `trigrams(x.rdfs:label,y.dc:title)|0.9` the left child of the speficiation and `euclidean(x.lat|x.long, y.latitude|y.longitude)|0.7`the right child of the specification. Both children specifications are simple specifications and combined with a boolean operator, they create a complex specification.
LIMES gives the user the opportunity to combine **exactly two** complex or simple spefications to create a new complex specification. Note that each child specification must be accompanied by its own threshold. 
