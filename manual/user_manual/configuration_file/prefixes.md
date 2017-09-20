# Prefixes
Defining a prefix in a LIMES file demands setting two values: 
 The `namespace` that will be addressed by the prefix's `label`

    <PREFIX>
        <NAMESPACE>http://www.w3.org/1999/02/22-rdf-syntax-ns#</NAMESPACE>
        <LABEL>rdf</LABEL>
    </PREFIX>

Here, we set the prefix `rdf` to correspond to `http://www.w3.org/1999/02/22-rdf-syntax-ns\#`. A LIMES link specification can contain as many prefixes as required.
