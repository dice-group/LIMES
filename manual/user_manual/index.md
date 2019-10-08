# User Manual (Version 1.0.0)

LIMES, the **Li**nk Discovery Framework for **Me**tric **S**paces, is a
framework for discovering links between entities contained in Linked
Data sources. LIMES is a hybrid framework that combines the mathematical
characteristics of metric spaces as well prefix-, suffix- and position
filtering to compute pessimistic approximations of the similarity of
instances. These approximations are then used to filter out a large
amount of those instance pairs that do not suffice the mapping
conditions. By these means, LIMES can reduce the number of comparisons
needed during the mapping process by several orders of magnitude and
complexity without losing a single link.

![LIMES workflow](../images/uml.png "fig:")

The LIMES framework consists of eight main modules of which each can be extended to accommodate new or improved functionality. The central module of LIMES is the **controller** module, which coordinates the matching process. The matching process is carried out as follows: First, the **controller** calls the **configuration** module, which reads the configuration file and extracts all the information necessary to carry out the comparison of instances, including the URL of the SPARQL-endpoints of source (S) and the target (T) knowledge bases, the restrictions on the instances to map (e.g., their type), the expression of the metric to be used and the threshold to be used. 

Given that the configuration file is valid w.r.t. the LIMES Specification Language (LSL), the **query** module is called. This module uses the configuration for the target and source knowledge bases to retrieve instances and properties from the SPARQL-endpoints of the source and target knowledge bases that adhere to the restrictions specified in the configuration file. The query module writes its output into a file by invoking the **cache** module. Once all instances have been stored in the cache, the controller chooses between performing Link Discovery or Machine Learning. For Link Discovery, LIMES will re-write, plan and execute the Link Specification (LS) included in the configuration file, by calling the **rewriter**, **planner** and **engine** modules resp. The main goal of LD is to identify the set of links (mapping) that satisfy the conditions opposed by the input LS. For Machine Learning, LIMES calls the **machine learning** algorithm included in the configuration file, to identify an appropriate LS to link S and T. Then it proceeds in executing the LS. For both the tasks, the mapping will be stored in the output file chosen by the user in the configuration file. The results are finally stored into an RDF or an XML file.

The advantages of LIMES’ approach are manifold. First, it implements
**highly time-optimized** mappers, making it a complexity class faster
than other Link Discovery Frameworks. Thus, the larger the problem, the
faster LIMES is w.r.t. other Link Discovery Frameworks. Secondly, LIMES 
supports a large set of string, numeric, topological and temporal similarity metrics, 
that provide the user with the opportunity to perform various comparisons between resources.
In addition, **LIMES is guaranteed to lead to exactly the same matching as a brute
force approach while at the same time reducing significantly the number
of comparisons**. In addition, LIMES supports a **large number of input
and output formats** and can easily be extended with custom
algorithms, datatypes, preprocessing functions and more thank
to its modular architecture.


In general, LIMES can be used to set links between two data sources,
e.g., a novel data source created by a data publisher and existing data
source such as DBpedia. This functionality can also be used to
detect duplicates within one data source for knowledge curation. The
only requirement to carry out these tasks is a simple XML- or RDF-based
configuration file. 
The purpose of this user manual is to
1. show the various ways in which LIMES can be invoked and
2. explicate the LIMES Configuration Language (LCL) so as allow users to generate their own configurations.
