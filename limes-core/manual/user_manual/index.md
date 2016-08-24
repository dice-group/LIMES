#User Manual (Version 1.0.0)

LIMES, the **Li**nk Discovery Framework for **Me**tric **S**paces, is a
framework for discovering links between entities contained in Linked
Data sources. LIMES is a hybrid framework that combines the mathematical
characteristics of metric spaces as well prefix-, suffix- and position
filtering to compute pessimistic approximations of the similarity of
instances. These approximations are then used to filter out a large
amount of those instance pairs that do not suffice the mapping
conditions. By these means, LIMES can reduce the number of comparisons
needed during the mapping process by several orders of magnitude and
complexity without loosing a single link.

![LIMES workflow](../images/uml.png "fig:")
[fig:workflow] 

The general workflow implemented by the LIMES framework is depicted in
Figure (fig:workflow). Given the source S, the target T and a link
specification, LIMES first separates the different data types to merge.
Strings are processed by using suffix-, prefix- and position filtering
in the string mapper. Numeric values (and all values that can be mapped
efficiently to a vector space) are mapped to a metric space and
processed by the HYPPO algorithm. All other values are mapped by using
the miscellaneous mapper. The results of all mappers processing are
filtered and merged by using time-efficient set and filtering
operations.

**TODO** Axel Change according to the new workflow

The advantages of LIMES’ approach are manifold. First, it implements
**highly time-optimized** mappers, making it a complexity class faster
than other Link Discovery Frameworks. Thus, the larger the problem, the
faster LIMES is w.r.t. other Link Discovery Frameworks. In addition,
**LIMES is guaranteed to lead to exactly the same matching as a brute
force approach while at the same time reducing significantly the number
of comparisons**. In addition, LIMES supports a **large number of input
and output formats** and can be extended very easily to fit new
algorithms , new datatypes, new preprocessing functions and others thank
to its modular architecture displayed in Figure [fig:architecture].

![Architecture of LIMES](../images/architecture.png "fig:")
[fig:architecture]

In general, LIMES can be used to set links between two data sources,
e.g., a novel data source created by a data publisher and existing data
source such as DBpedia. This functionality can also be used to
detect duplicates within one data source for knowledge curation. The
only requirement to carry out these tasks is a simple XML-based
configuration file. The purpose of this manual is to explicate the LIMES
Configuration Language (LCL) that underlies these configuration files,
so as allow users to generate their own configurations. An online
version of LIMES is available online at
http://limes.aksw.org.
