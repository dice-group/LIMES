# Metric Expression for Similarity Measurement
One of the core improvements of the newest LIMES kernels is the provision of a highly flexible language for the specification of complex metrics for linking (set by using the `METRIC` tag as exemplified below).

    <METRIC>
        trigrams(y.dc:title, x.linkedct:condition_name)
    </METRIC>

In this example, we use the `trigrams` metric to compare the `dc:title` of the instances retrieved from the source data source, with which the variable `y` is associated, with the `linkedct:` variable `x` is associated. While such simple metrics can be used in many cases, complex metrics are necessary in complex linking cases. LIMES includes a formal grammar for specifying complex configurations of arbitrary complexity. For this purpose, two categories of binary operations are supported: Metric operations and boolean operations.