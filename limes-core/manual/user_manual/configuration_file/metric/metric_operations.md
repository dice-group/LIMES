#Metric operations
Metric operations allow to combine metric values. They include the operators `MIN`, `MAX` and `ADD` e.g. as follows:

    MAX(trigrams(x.rdfs:label,y.dc:title),euclidean(x.lat|long, y.latitude|longitude)).

This specification computes the maximum of:

1. The trigram similarity of x's `rdfs:label` and y's `dc:title` 
2. The 2-dimension euclidean distance of `x`'s `lat` and `long` with `y`'s `latitude` and `longitude`, i.e.,  $$ \sqrt{((x.lat- y.latitude)^2 + (x.long - y.longitude)^2)} $$. 

Note that euclidean supports arbitrarily many dimensions. In addition, note that `ADD` allows to define weighted sums as follows:`ADD(0.3*trigrams(x.rdfs:label,y.dc:title), 0.7*euclidean(x.lat|x.long,y.latitude|y.longitude))`.

