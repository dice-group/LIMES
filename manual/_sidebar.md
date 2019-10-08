# Summary

### Getting Started

* [About this documentation](README.md)
* [Installation and Setup](getting_started/installation.md)


### User manual

* [Introduction](user_manual/index.md)
* [Running LIMES](user_manual/running_limes.md#top)
    * [Using the Client](user_manual/running_limes.md#client)
    * [Using the Server](user_manual/running_limes.md#server)
    * [Using the GUI](user_manual/running_limes.md#gui)
	    * [Creating a New Configuration](user_manual/running_limes.md#how-to-create-a-new-configuration)
	    * [Creating a New Metric](user_manual/running_limes.md#how-to-create-a-new-link-specification)
	    * [Machine Learning with the GUI](user_manual/running_limes.md#machine-learning-in-the-gui)
    * [Using the Web UI](user_manual/running_limes.md#web-ui)
* [Writing Configuration Files](user_manual/configuration_file/index.md)
    * [Components of a Configuration File](user_manual/configuration_file/index.md#components-of-a-limes-xml-configuration-file)
        * [Metadata](user_manual/configuration_file/index.md#metadata)
        * [Prefixes](user_manual/configuration_file/index.md#prefixes)
        * [Data Sources](user_manual/configuration_file/index.md#data-sources)
        * [Metric](user_manual/configuration_file/index.md#metric-expression-for-similarity-measurement)
        * [Execution](user_manual/configuration_file/index.md#execution-optional)
        * [Machine Learning](user_manual/configuration_file/index.md#machine-learning-optional)
        * [Granularity](user_manual/configuration_file/index.md#granularity-optional)
        * [Acceptance Condition](user_manual/configuration_file/index.md#acceptance-condition)
        * [Review Condition](user_manual/configuration_file/index.md#review-condition)
        * [Output](user_manual/configuration_file/index.md#output-format)
    * [Configuration File Examples](user_manual/configuration_file/configuration-file-examples.md)
    * [Defining Link Specifications](user_manual/configuration_file/defining_link_specifications.md)
        * [Metric Operations](user_manual/configuration_file/defining_link_specifications.md#metric-operations)
        * [Boolean Operations](user_manual/configuration_file/defining_link_specifications.md#boolean-operations)
        * [Implemented Measures](user_manual/configuration_file/defining_link_specifications.md#implemented-measures)
            * [String measures](user_manual/configuration_file/defining_link_specifications.md#string-measures)
            * [Vector space measures](user_manual/configuration_file/defining_link_specifications.md#vector-space-measures)
            * [Pointset measures](user_manual/configuration_file/defining_link_specifications.md#point-set-measures)
            * [Topological measures](user_manual/configuration_file/defining_link_specifications.md#topological-measures)
            * [Temporal measures](user_manual/configuration_file/defining_link_specifications.md#temporal-measures)
            * [Resource set measures](user_manual/configuration_file/defining_link_specifications.md#resource-set-measures)
    * [Defining Machine Learning Tasks](user_manual/configuration_file/defining_ml_tasks.md)


<!-- * [Known Issues](user_manual/known_issues.md) 
    * [Change Log](https://github.com/dice-group/LIMES/blob/master/CHANGES.md)
    * [Example Use Cases](user_manual/usecases.md) -->


### Developer manual

* [Introduction](developer_manual/index.md)
* [Overview](developer_manual/overview.md)
* [I/O Classes](developer_manual/io_classes.md)
* [Measures](developer_manual/measures.md)
* [LSPipeline](developer_manual/ls_pipeline.md)
* [MLPipeline](developer_manual/ml_pipeline.md)


### Miscellaneous

* [FAQ](misc/faq.md)
* [Release notes](https://github.com/dice-group/LIMES/blob/master/CHANGES.md)
