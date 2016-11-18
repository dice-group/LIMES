# String Measures

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
