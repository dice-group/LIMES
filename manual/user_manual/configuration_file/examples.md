# Configuration File Examples
The following shows the whole configuration file for LIMES explicated in the sections above.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE LIMES SYSTEM "limes.dtd">
<LIMES>
    <PREFIX>
        <NAMESPACE>http://geovocab.org/geometry#</NAMESPACE>
        <LABEL>geom</LABEL>
    </PREFIX>
    <PREFIX>
        <NAMESPACE>http://www.opengis.net/ont/geosparql#</NAMESPACE>
        <LABEL>geos</LABEL>
    </PREFIX>
    <PREFIX>
        <NAMESPACE>http://linkedgeodata.org/ontology/</NAMESPACE>
        <LABEL>lgdo</LABEL>
    </PREFIX>
    <SOURCE>
        <ID>linkedgeodata</ID>
        <ENDPOINT>http://linkedgeodata.org/sparql</ENDPOINT>
        <VAR>?x</VAR>
        <PAGESIZE>2000</PAGESIZE>
        <RESTRICTION>?x a lgdo:RelayBox</RESTRICTION>
        <PROPERTY>geom:geometry/geos:asWKT RENAME polygon</PROPERTY>
    </SOURCE>
    <TARGET>
        <ID>linkedgeodata</ID>
        <ENDPOINT>http://linkedgeodata.org/sparql</ENDPOINT>
        <VAR>?y</VAR>
        <PAGESIZE>2000</PAGESIZE>
        <RESTRICTION>?y a lgdo:RelayBox</RESTRICTION>
        <PROPERTY>geom:geometry/geos:asWKT RENAME polygon</PROPERTY>
    </TARGET>
    <METRIC>geo_hausdorff(x.polygon, y.polygon)</METRIC>
    <ACCEPTANCE>
        <THRESHOLD>0.9</THRESHOLD>
        <FILE>lgd_relaybox_verynear.nt</FILE>
        <RELATION>lgdo:near</RELATION>
    </ACCEPTANCE>
    <REVIEW>
        <THRESHOLD>0.5</THRESHOLD>
        <FILE>lgd_relaybox_near.nt</FILE>
        <RELATION>lgdo:near</RELATION>
    </REVIEW>
    
    <EXECUTION>
        <REWRITER>default</REWRITER>
        <PLANNER>default</PLANNER>
        <ENGINE>default</ENGINE>
    </EXECUTION>
    
    <OUTPUT>TAB</OUTPUT>
</LIMES>
```

LIMES can be also configured using a RDF configuration file, the next
listing represent the same LIMES configuration used in the previous XML
file.

```turtle
@prefix geos:    <http://www.opengis.net/ont/geosparql#> .
@prefix lgdo:    <http://linkedgeodata.org/ontology/> .
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geom:    <http://geovocab.org/geometry#> .
@prefix limes:   <http://limes.sf.net/ontology/> .

limes:linkedgeodataTOlinkedgeodataTarget
      a       limes:TargetDataset ;
      rdfs:label "linkedgeodata" ;
      limes:endPoint "http://linkedgeodata.org/sparql" ;
      limes:pageSize "2000" ;
      limes:property "geom:geometry/geos:asWKT" ;
      limes:restriction "?y a lgdo:RelayBox" ;
      limes:variable "?y" .

limes:linkedgeodataTOlinkedgeodata
      a       limes:LimesSpecs ;
      limes:hasExecutionParameters limes:executionParameters ;
      limes:granularity "2" ;
      limes:hasAcceptance limes:linkedgeodataTOlinkedgeodataAcceptance ;
      limes:hasMetric limes:linkedgeodataTOlinkedgeodataMetric ;
      limes:hasReview limes:linkedgeodataTOlinkedgeodataReview ;
      limes:hasSource limes:linkedgeodataTOlinkedgeodataSource ;
      limes:hasTarget limes:linkedgeodataTOlinkedgeodataTarget ;
      limes:outputFormat "TAB" .
      
limes:executionParameters
      a limes:ExecutionParameters ;
      limes:executionPlanner "default" ;
      limes:executionRewriter "default" ;
      limes:executionEngine "default" .
      
      
limes:linkedgeodataTOlinkedgeodataReview
      a       limes:Review ;
      limes:file "lgd_relaybox_near.nt" ;
      limes:relation "lgdo:near" ;
      limes:threshold "0.5" .

limes:linkedgeodataTOlinkedgeodataMetric
      a       limes:Metric ;
      limes:expression "geo_hausdorff(x.polygon, y.polygon)" .

limes:linkedgeodataTOlinkedgeodataAcceptance
      a       limes:Acceptance ;
      limes:file "lgd_relaybox_verynear.nt" ;
      limes:relation "lgdo:near" ;
      limes:threshold "0.9" .

limes:linkedgeodataTOlinkedgeodataSource
      a       limes:SourceDataset ;
      rdfs:label "linkedgeodata" ;
      limes:endPoint "http://linkedgeodata.org/sparql" ;
      limes:pageSize "2000" ;
      limes:property "geom:geometry/geos:asWKT" ;
      limes:restriction "?x a lgdo:RelayBox" ;
      limes:variable "?x" .
```

The following configuration file uses the machine learning algorithm of the Wombat simple to find the metric expression for the same example:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE LIMES SYSTEM "limes.dtd">
<LIMES>
    <PREFIX>
        <NAMESPACE>http://geovocab.org/geometry#</NAMESPACE>
        <LABEL>geom</LABEL>
    </PREFIX>
    
    <PREFIX>
        <NAMESPACE>http://www.opengis.net/ont/geosparql#</NAMESPACE>
        <LABEL>geos</LABEL>
    </PREFIX>
    
    <PREFIX>
        <NAMESPACE>http://linkedgeodata.org/ontology/</NAMESPACE>
        <LABEL>lgdo</LABEL>
    </PREFIX>
    
    <SOURCE>
        <ID>linkedgeodata</ID>
        <ENDPOINT>http://linkedgeodata.org/sparql</ENDPOINT>
        <VAR>?x</VAR>
        <PAGESIZE>2000</PAGESIZE>
        <RESTRICTION>?x a lgdo:RelayBox</RESTRICTION>
        <PROPERTY>geom:geometry/geos:asWKT RENAME polygon</PROPERTY>
    </SOURCE>
    
    <TARGET>
        <ID>linkedgeodata</ID>
        <ENDPOINT>http://linkedgeodata.org/sparql</ENDPOINT>
        <VAR>?y</VAR>
        <PAGESIZE>2000</PAGESIZE>
        <RESTRICTION>?y a lgdo:RelayBox</RESTRICTION>
        <PROPERTY>geom:geometry/geos:asWKT RENAME polygon</PROPERTY>
    </TARGET>
    
    <MLALGORITHM>
        <NAME>wombat simple</NAME>
        <TYPE>supervised batch</TYPE>
        <TRAINING>trainingData.nt</TRAINING>
        <PARAMETER> 
            <NAME>max execution time in minutes</NAME>
            <VALUE>60</VALUE>
        </PARAMETER>
    </MLALGORITHM>
    
    <ACCEPTANCE>
        <THRESHOLD>0.9</THRESHOLD>
        <FILE>lgd_relaybox_verynear.nt</FILE>
        <RELATION>lgdo:near</RELATION>
    </ACCEPTANCE>
    
    <REVIEW>
        <THRESHOLD>0.5</THRESHOLD>
        <FILE>lgd_relaybox_near.nt</FILE>
        <RELATION>lgdo:near</RELATION>
    </REVIEW>
    
    <EXECUTION>
        <REWRITER>default</REWRITER>
        <PLANNER>default</PLANNER>
        <ENGINE>default</ENGINE>
    </EXECUTION>
    
    <OUTPUT>TAB</OUTPUT>
</LIMES>
```

Finally, the last XML configuration file in TTL format will look like:

```turtle
@prefix geos:    <http://www.opengis.net/ont/geosparql#> .
@prefix lgdo:    <http://linkedgeodata.org/ontology/> .
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix geom:    <http://geovocab.org/geometry#> .
@prefix limes:   <http://limes.sf.net/ontology/> .

limes:linkedgeodataTOlinkedgeodataTarget
      a       limes:TargetDataset ;
      rdfs:label "linkedgeodata" ;
      limes:endPoint "http://linkedgeodata.org/sparql" ;
      limes:pageSize "2000" ;
      limes:property "geom:geometry/geos:asWKT" ;
      limes:restriction "?y a lgdo:RelayBox" ;
      limes:variable "?y" .

limes:linkedgeodataTOlinkedgeodata
      a       limes:LimesSpecs ;
      limes:hasExecutionParameters limes:executionParameters ;
      limes:granularity "2" ;
      limes:hasAcceptance limes:linkedgeodataTOlinkedgeodataAcceptance ;
      limes:hasMetric limes:linkedgeodataTOlinkedgeodataMetric ;
      limes:hasReview limes:linkedgeodataTOlinkedgeodataReview ;
      limes:hasSource limes:linkedgeodataTOlinkedgeodataSource ;
      limes:hasTarget limes:linkedgeodataTOlinkedgeodataTarget ;
      limes:outputFormat "TAB" .
      
limes:executionParameters
      a limes:ExecutionParameters ;
      limes:executionPlanner "default" ;
      limes:executionRewriter "default" ;
      limes:executionEngine "default" .
      
      
limes:linkedgeodataTOlinkedgeodataReview
      a       limes:Review ;
      limes:file "lgd_relaybox_near.nt" ;
      limes:relation "lgdo:near" ;
      limes:threshold "0.5" .

limes:linkedgeodataTOlinkedgeodataMetric
      a       limes:Metric ;
      limes:expression "geo_hausdorff(x.polygon, y.polygon)" .

limes:linkedgeodataTOlinkedgeodataAcceptance
      a       limes:Acceptance ;
      limes:file "lgd_relaybox_verynear.nt" ;
      limes:relation "lgdo:near" ;
      limes:threshold "0.9" .

limes:linkedgeodataTOlinkedgeodataSource
      a       limes:SourceDataset ;
      rdfs:label "linkedgeodata" ;
      limes:endPoint "http://linkedgeodata.org/sparql" ;
      limes:pageSize "2000" ;
      limes:property "geom:geometry/geos:asWKT" ;
      limes:restriction "?x a lgdo:RelayBox" ;
      limes:variable "?x" .
```
