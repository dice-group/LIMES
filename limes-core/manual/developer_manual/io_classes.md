# I/O Classes

There are two classes that are fundamental for representing linked data within LIMES:

- `ACache` stores *input data*, i.e. linked data retrieved from a triple store or file. In the most cases, there are two caches, one for the source *knowledge base* (KB) and one for the target KB.

- `AMapping` stores *output data*, i.e. the mapping (from source resources to target resources) that is generated as the result of running LIMES. An entry in a mapping is a triple of source URI, linking predicate and target URI. Additionally, Mapping could store the confidence score.
