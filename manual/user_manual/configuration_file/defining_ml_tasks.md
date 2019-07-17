# Defining Machine Learning Tasks

In the following we will detail the contents of the `MLALGORITHM` tag
that users can use to configure LIMES for machine learning.
Consider the following example from the configuration file:

```xml
<MLALGORITHM>
    <NAME>wombat simple</NAME>
    <TYPE>supervised batch</TYPE>
    <TRAINING>trainingData.nt</TRAINING>
    <PARAMETER> 
        <NAME>max execution time in minutes</NAME>
        <VALUE>60</VALUE>
    </PARAMETER>
</MLALGORITHM>
```

* The tag `NAME` contains the name of the machine learning algorithm. Currently, we implemented the following algorithms:
    + wombat simple
    + wombat complete
    + eagle
* The tag `TYPE` contains the type of the machine learning algorithm, which could take one of the values:
    + supervised batch
    + supervised active
    + unsupervised
* The tag `TRAINING` contains the full path to the training data file. Note that this tag is not required in case of the supervised active and unsupervised learning algorithms. Usually a `*.ttl` file is used where the URIs of the entities are linked via `owl:sameAs` e.g.: ```<http://sourceexample.org/entity1> <http://www.w3.org/2002/07/owl#sameAs><http://targetexample.org/entity1> . ```
* The tag `PARAMETER` contains the name (using the sub-tag `NAME`) and the value (using the sub-tag `VALUE`) of the used machine learning algorithm parameter. Users can use as many `PARAMETER` tags as required. Note that LIMES uses the default values of all unspecified parameters. 

The following table contains a list of implemented algorithms together with supported implementations and parameters.

<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0;}
.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg .tg-baqh{text-align:center;vertical-align:top}
.tg .tg-yw4l{vertical-align:top}
</style>
<table class="tg">
  <tr>
    <th class="tg-yw4l">ML Algorithm<br></th>
    <th class="tg-yw4l">Supported types<br></th>
    <th class="tg-yw4l">Parameter</th>
    <th class="tg-yw4l">Default Value<br></th>
    <th class="tg-yw4l">Note</th>
  </tr>
  <tr>
    <td class="tg-baqh" rowspan="14">WOMBAT Simple<br></td>
    <td class="tg-yw4l" rowspan="14">supervised batch, supervised active and unsupervised</td>
    <td class="tg-yw4l">max refinement tree size</td>
    <td class="tg-yw4l">2000</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">max iterations number</td>
    <td class="tg-yw4l">3</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">max iteration time in minutes</td>
    <td class="tg-yw4l">20</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">max execution time in minutes</td>
    <td class="tg-yw4l">600</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">max fitness threshold</td>
    <td class="tg-yw4l">1</td>
    <td class="tg-yw4l">Range 0 to 1</td>
  </tr>
  <tr>
    <td class="tg-yw4l">minimum property coverage</td>
    <td class="tg-yw4l">0.4</td>
    <td class="tg-yw4l">Range 0 to 1</td>
  </tr>
  <tr>
    <td class="tg-yw4l">property learning rate</td>
    <td class="tg-yw4l">0.9</td>
    <td class="tg-yw4l">Range 0 to 1</td>
  </tr>
  <tr>
    <td class="tg-yw4l">overall penalty weight</td>
    <td class="tg-yw4l">0.5<br></td>
    <td class="tg-yw4l">Range 0 to 1<br></td>
  </tr>
  <tr>
    <td class="tg-yw4l">children penalty weight</td>
    <td class="tg-yw4l">1</td>
    <td class="tg-yw4l">Range 0 to 1</td>
  </tr>
  <tr>
    <td class="tg-yw4l">complexity penalty weight</td>
    <td class="tg-yw4l">1</td>
    <td class="tg-yw4l">Range 0 to 1</td>
  </tr>
    <tr>
      <td class="tg-yw4l">beta</td>
      <td class="tg-yw4l">1</td>
      <td class="tg-yw4l">Range 0 to 1.7*10^308 (Double.MAX_VALUE)</td>
    </tr>
  <tr>
    <td class="tg-yw4l">verbose</td>
    <td class="tg-yw4l">false</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">atomic measures</td>
    <td class="tg-yw4l">jaccard, trigrams, cosine, qgrams</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">save mapping</td>
    <td class="tg-yw4l">true</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">WOMBAT Complete<br></td>
    <td class="tg-yw4l">supervised batch, supervised active and unsupervised</td>
    <td class="tg-yw4l" colspan="3">Same as WOMBAT Simple<br></td>
  </tr>
  <tr>
    <td class="tg-yw4l" rowspan="13">EAGLE</td>
    <td class="tg-yw4l" rowspan="13">supervised batch, supervised active and unsupervised</td>
    <td class="tg-yw4l">generations</td>
    <td class="tg-yw4l">10</td>
    <td class="tg-yw4l">Integer</td>
  </tr>
  <tr>
    <td class="tg-yw4l">preserve_fittest</td>
    <td class="tg-yw4l">true</td>
    <td class="tg-yw4l"></td>
  </tr>
  <tr>
    <td class="tg-yw4l">max_duration</td>
    <td class="tg-yw4l">60</td>
    <td class="tg-yw4l">[1,Inf)</td>
  </tr>
  <tr>
    <td class="tg-yw4l">inquiry_size</td>
    <td class="tg-yw4l">10</td>
    <td class="tg-yw4l">[1,Inf)</td>
  </tr>
  <tr>
    <td class="tg-yw4l">max_iterations</td>
    <td class="tg-yw4l">500</td>
    <td class="tg-yw4l">[1,Inf)</td>
  </tr>
  <tr>
    <td class="tg-yw4l">max_quality</td>
    <td class="tg-yw4l">0.5</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">termination_criteria</td>
    <td class="tg-yw4l">iteration</td>
    <td class="tg-yw4l">enum</td>
  </tr>
  <tr>
    <td class="tg-yw4l">termination_criteria_value</td>
    <td class="tg-yw4l">0.0</td>
    <td class="tg-yw4l">[0.0,Inf)</td>
  </tr>
  <tr>
    <td class="tg-yw4l">beta</td>
    <td class="tg-yw4l">1.0</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">population</td>
    <td class="tg-yw4l">20</td>
    <td class="tg-yw4l">[1,Inf)</td>
  </tr>
  <tr>
    <td class="tg-yw4l">mutation_rate</td>
    <td class="tg-yw4l">0.4</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">reproduction_rate</td>
    <td class="tg-yw4l">0.4</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">crossover_rate</td>
    <td class="tg-yw4l">0.3</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l" rowspan="13">DRAGON</td>
    <td class="tg-yw4l" rowspan="13">supervised batch</td>
    <td class="tg-yw4l">confidence threshold for pruning</td>
    <td class="tg-yw4l">0.01</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">property mapping</td>
    <td class="tg-yw4l">/path/to/propertymapping/file</td>
    <td class="tg-yw4l">tab seperated file matching properties of source and target and property type e.g.:
    name    name    string<BR>
    age age number<BR>
    </td>
  </tr>
  <tr>
    <td class="tg-yw4l">maximum height of the link specification</td>
    <td class="tg-yw4l">1</td>
    <td class="tg-yw4l">[1,100000]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">minimum property coverage</td>
    <td class="tg-yw4l">0.01</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">property learning rate</td>
    <td class="tg-yw4l">0.01</td>
    <td class="tg-yw4l">[0.0,1.0]</td>
  </tr>
  <tr>
    <td class="tg-yw4l">fitness function</td>
    <td class="tg-yw4l">GiniIndex</td>
    <td class="tg-yw4l">"GiniIndex" or "GlobalFMeasure"</td>
  </tr>
  <tr>
    <td class="tg-yw4l">pruning function</td>
    <td class="tg-yw4l">ErrorEstimate</td>
    <td class="tg-yw4l">"ErrorEstimate" or "GlobalFmeasure"</td>
  </tr>
</table>
    	
 

