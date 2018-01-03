#Metric operations
Metric operations allow to combine metric values. They include the operators `MIN`, `MAX` and `ADD` e.g. as follows:

    MAX(trigrams(x.rdfs:label,y.dc:title)|0.3,euclidean(x.lat|long, y.latitude|longitude)|0.5).

This specification computes the maximum of:

1. The trigram similarity of x's `rdfs:label` and y's `dc:title` is greater or equal to 0.3
2. The 2-dimension euclidean distance of `x`'s `lat` and `long` with `y`'s `latitude` and `longitude`, i.e.,  $$ \sqrt{((x.lat- y.latitude)^2 + (x.long - y.longitude)^2)} $$ is greater or equal to 0.5. 

Note that euclidean supports arbitrarily many dimensions. In addition, note that `ADD` allows to define weighted sums as follows:`ADD(0.3*trigrams(x.rdfs:label,y.dc:title)|0.3, 0.7*euclidean(x.lat|x.long,y.latitude|y.longitude)|0.5)`.

We call `trigrams(x.rdfs:label,y.dc:title)|0.3` the left child of the specification and `euclidean(x.lat|long, y.latitude|longitude)|0.5` the right child of the specification. Both children specifications are simple specifications and combined with a metric operator, they create a complex specification. LIMES gives the user the opportunity to combine **exactly two ** complex or simple spefications to create a new complex specification. Note that each child specification must be accompanied by its own threshold.
