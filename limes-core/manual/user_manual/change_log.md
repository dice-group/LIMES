#Change log

###Version 1.0.0
-  Kernel update
-  New Controller that support manual and graphical configuration
-  New machine learning pipeline: supports supervised, unsupervised and active learning algorithms
-  Supports qualitative (Precision, Recall, FMeasure etc.) and quantitative (runtime duration etc.) evaluation metrics for mapping evaluation, in the presence of a gold standard
-  Updated execution engine to handle dynamic planning
-  First dynamic planning for efficient link discovery
-  Supports XML and RDF cofiguration files
-  Supports pointsets metrics such as Mean, Hausdorff and Surjection.
-  Supports MongeElkan, RatcliffObershelp string measures.
-  Supports Allen's algebra temporal relations for event data
-  Supports all topological relations derived from the DE-9IM model
-  New LIMES GUI
-  Migrated the system to Java 8 and Jena 3.0.1


###The following versions can be found here:https://github.com/AKSW/LIMES. 
####Version 0.6RC4
-   Added support for several geo-spatial similarity functions (geomean, surjection, fairsurjection, geosumofmin, frechet, link)
-   Added support for temporal geo-spatial similarity functions (daysim, datesim, yearsim)
-   Added parallel implementation for ORCHID
-   Added support for Jaro and Jaro-Winkler

####Version 0.6RC3
-   Added support for geo-spatial similarity function based on Hausdorff distance
-   Added support for geo-spatial similarity function based on symmetric Hausdorff distance
-   Added support for orthodromic distance
-   Implemented ORCHID for time-efficient linking of geo-spatial resources
-   Added support for exact matches

####Version 0.6RC2
-   Time-efficient self-configuration (genetic, linear, boolean)
-   Can now read use most RDF serialization formats (RDF/XML, N3, NT, TTL) as input

####Version 0.6RC1
-   Kernel update
-   HR3 algorithm for vector space. Default granularity is now 4
-   Update for data readers and writers.
-   Genetic Learning

####Version 0.5RC1
-   Kernel change, more than 4 orders of magnitude faster
-   HYPPO algorithm for vector spaces
-   Fast prefix, suffix and position filtering for strings
-   Support for more metrics

####Version 0.4.1
-   Added support for data source type (tab-separated vectors)
-   Added factory for query modules

####Version 0.4
-   Added support for data source type (Sparql, CSV)
-   Added hybrid cache
-   Implemented CSV reader
-   Faster organizer
