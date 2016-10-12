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

The general workflow implemented by the LIMES framework is depicted in the previous
figure. To execute LIMES, a configuration file must be given as input to the framework.
The configuration files includes information about the source S, the target T and the link
specification (LS) that will be used to link S and T.
To achieve time and memory efficiency, LIMES serialises every new S and T into caches, in order to avoid re-querying the input datasets in the future. 
Once the input resource sets are retrieved, the framework will either perform Link Discovery (LD) or Machine Learning (ML). For Link Discovery, LIMES will re-write, plan and execute a LS in order to identify the set of links that satisfy the conditions opposed by the LS. For Machine Learning, LIMES will execute the predefined ML algorithm included in the configuration file to identify an appropriate LS to link S and T. Then it proceeds in executing the LS. For both taks, the resulting set of links will be stored in a file included in the input configuration file.


The advantages of LIMES’ approach are manifold. First, it implements
**highly time-optimized** mappers, making it a complexity class faster
than other Link Discovery Frameworks. Thus, the larger the problem, the
faster LIMES is w.r.t. other Link Discovery Frameworks. Secondly, LIMES 
supports a large set of string, numeric, topological and temporal similarity metrics, 
that provide the user with the opportunity to perform various comparisons between resources.
In addition, **LIMES is guaranteed to lead to exactly the same matching as a brute
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
only requirement to carry out these tasks is a simple XML-based or TURTLE-based
configuration file. The purpose of this manual is to explicate the LIMES
Configuration Language (LCL) that underlies these configuration files,
so as allow users to generate their own configurations. An online
version of LIMES is available online at
http://limes.aksw.org.
