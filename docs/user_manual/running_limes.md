# Running LIMES

LIMES can be run in three modes: Command Line Interface (CLI) client, CLI server, WEB UI and GUI client.
All modes offer the same functionality but over different interfaces to support a variety of use cases.    

# Using the CLI Client
For this purpose, simply run

```bash
java -jar path_to_limes.jar config.xml [OPTIONS...]
```

The following optional command line flags and options are available:

* `-f $format` sets the format of configuration file. Possible values for `$format` are`"XML"` (default) or `"RDF"`
* `-d $file_path` configure the path for the statistics JSON output file
* `-1` enforces 1-to-1 mappings, i.e. for each source resource only keep the link with the highest probability
* `-g $file_path` configure a reference mapping file (a.k.a. gold standard) to compute precision, recall and f measure
* `-F $format` sets the format of the gold standard. Possible values for `$format` are`"csv"` (default), `"tab"` or `"rdf""`. Only effective when `-g $file_path` is also specified
* `-s` runs the LIMES server
* `-p $port` used to specify port of LIMES server, defaults to port 8080
* `-l $limit` limits the number of resources processed by LIMES server to `$limit`, defaults to -1 (no limit).
**CAUTION: Setting this option will compromise the correctness of LIMES and is only encouraged to reduce server load for demo purposes. **
* `-h` prints out a help message
* `-o $file_path` sets the path of the logging file

In case your system runs out of memory, please use the `-Xmx` option (must appear before the -jar option) to
allocate more memory to the Java Virtual Machine.

# Using the CLI Server

LIMES can be run as an HTTP Server, implementing a RESTful API and serving a browser frontend by default.
Configuration files are accepted via POST multipart/form-data uploads.
Each configuration file gets assigned a unique *job_id*.
Given this job id, the user can query the server for the status of the job, its logs, a list of result files and the contents of these result files.

## API
The following RESTful operations are currently implemented:

* `submit/` **(POST)** ---
used to upload configuration files as multipart/form-data POST messages and returns the
  assigned *job_id* in a JSON object.
  **Accepts XML Configuration file** (See example below)  
  When an active learning configuration is submitted, it will also return a list of examples. (See `activeLearning/:id`)
* `activeLearning/:id` **(POST)** ---
used for active learning. **Accepts a JSON file with the single key `"exampleScores"` whose value is an
  array of floating point numbers in the interval \[-1,1\], used to score the previously retrieved examples.  
  Returns the *job_id* and a set of new examples, if any are left to be classified. 
* `status/:id`  **(GET)** ---
returns the status (a numerical code) for a given job in a JSON object. 
  The following statuses are currently implemented:
  * **-1 (Unknown)**  - a configuration file for the given *job_id* has not been found on the server 
  * **0 (Scheduled)** - the configuration file is present and the job is waiting for execution
  * **1 (Running)** - the job is currently running
  * **2 (Finished)** - the job is finished and its output files are ready for delivery
* `logs/:id` **(GET)** ---
returns the java logs for the given job. Useful for troubleshooting.
* `list/measures` **(GET)** ---
returns the list of available measures.
* `list/operators` **(GET)** ---
returns the list of available operators.
* `list/preprocessings` **(GET)** ---
returns the list of available preprocessings.
* `sparql/:urlEncodedEndpointUrl` **(GET, POST, OPTIONS)** ---
proxy for SPARQL queries. Useful for browser frontends when SPARQL endpoints do not implement CORS headers.
* `upload` **(POST)** ---
  used to upload source and/or target files as multipart/form-data POST messages and returns the
            assigned *upload_id* in a JSON object.
* `uploads/:uploadId/sparql`  **(GET)** ---
query uploaded files. Useful for browser frontends.
* `results/:id` **(GET)** ---
returns a list of result files in a JSON object.
* `result/:id/:filename`  **(GET)** ---
returns the contents of a given result file for a given job id.
  
  
## Example

```bash
# Get latest LIMES, dev branch
git clone https://github.com/dice-group/LIMES && cd LIMES && git checkout dev 
# Assembly
mvn clean package shade:shade -Dmaven.test.skip=true
cd limes-core/target
# Run LIMES as HTTP server on port 8080
# You can also run it on port 80 for example by adding -p 80 flag
java -jar limes-core-LATEST_RELEASE_VERSION_NUMBER.jar -s
# Get output:
# 15:05:46.078 [main] INFO  org.aksw.limes.core.controller.SimpleServer:44 - Attempting to start LIMES server at port 8080...
# 15:05:46.088 [main] INFO  org.aksw.limes.core.controller.SimpleServer:56 - Server has been started! Waiting for requests...
# Open new terminal and run
cd PATH_TO_LIMES/LIMES/limes-core/target
# Download example XML mapping
wget https://raw.githubusercontent.com/dice-group/LIMES/master/limes-core/resources/lgd-lgd-optional-properties.xml
# Run mapping against endpoint and get job id
curl -F config_file=@lgd-lgd-optional-properties.xml  http://localhost:8080/submit
# returns:
# {"requestId":"7538819321022935531","success":true}
# Observe the status
curl http://localhost:8080/status/7538819321022935531
# returns:
# {"status":{"code":2,"description":"Request has been processed"},"success":true}
# Get result file list
curl http://localhost:8080/results/7538819321022935531
# returns:
# {"availableOperators":["lgd_relaybox_near.nt","lgd_relaybox_verynear.nt"],"success":true}
# Get result
curl http://localhost:8080/result/7538819321022935531/lgd_relaybox_verynear.nt
# returns:
# <http://linkedgeodata.org/triplify/node2806760713>	<http://linkedgeodata.org/triplify/node2806760713>	1.0
# <http://linkedgeodata.org/triplify/node2806760713>	<http://linkedgeodata.org/triplify/node400957326>	0.9283311463354712
# <http://linkedgeodata.org/triplify/node1319713883>	<http://linkedgeodata.org/triplify/node1319713883>	1.0
# [...]
# Inspect the logs
curl http://localhost:8080/logs/7538819321022935531
# returns:
# 2018-06-20T12:08:09,027 [ForkJoinPool.commonPool-worker-2] INFO org.aksw.limes.core.io.cache.HybridCache 111 - Checking for file [...]
```

# Using the Web UI

LIMES Web UI is an additional tool to write configuration file in the XML using web interface and execute it using the LIMES server.
LIMES Web UI consists of six main components: *prefixes*, *data source / target* , *manual metric*, *machine learning*, *acceptance/review conditions* and *output*.

<img src="./images/full_limes_with_marks.png" width="800" alt ="Overview">

## Prefixes

The Prefixes component consists of two parts:
* Currently added prefixes. They look like chips, containing the label of the prefix and a hover tooltip with the namespace.
* Add new prefix (optional). In most cases, our interface is able to automatically find the common prefixes from [prefix.cc](https://prefix.cc/context). In case the user want to add a prefix manually, (s)he can type the prefix, choose the its respective URI  from the dropdown list (if any) or manually type it.  Then, the user click on *Add* and the prefix will be added as a new chip. Again, this process is necessary for common prefixes. 

<img src="./images/prefixes_web_ui.png" width="800" alt ="Prefixes_web_ui">

## Data source / target

The Data source and data target consists of the two similar components, which include three input fields:
* *Sparql endpoint/Local file*: One of two options can be chosen. Sparql endpoint means that the user will select the endpont from the list. Local file means that the file should be provided as an endpoint.
* *Endpoint*: A dropdown list of available endpoints. Moreover, the user can try to search for the endpoint, typing it in the input field or write your own endpoint. After clicking on the endpoint from the list or writing it by hand and press the Enter, the user will get the list of restriction classes according to this endpoint.
* *Restriction*: Contains of three parameters splitted by space (?s rdf:type some:Type). The third parameter will be changed automatically after changing the restriction class. 
* *Restriction class*: A dropdown list of restriction classes according to the endpoint. You can start typing the name of the class and the list will be filtered automatically. After choosing the restriction class, you will get all the properties related to this class.

<img src="./images/data_source_and_target_web_ui.png" width="800" alt ="Data_source_and_target_web_ui">

## Manual metric and machine learning

After the *data source and target* have chosen and you have got a message that properties have already received, you are now ready to build a metric. You can build a manual metric or use machine learning.   These options are interchangeable. Consider first the manual metric tab.

### Manual metric
To build a metric you can drag and drop elements from the toolbox. 

There are 8 blocks: 
* *Source property*, 
* *Target property*, 
* *Optional source property*, 
* *Optional target property*, 
* *Measure*, 
* *Operator*, 
* *Preprocessing function*, 
* *Preprocessing rename function*,
* *Complex reprocessing function*. 

In the Workspace you can see the *Start* block, which cannot be removed. Building the metric exactly starts from connecting the *Measure* or *Operator* block to the *Start* block. Once you connect the *Measure* block, next step can be connecting the *Source property* and the *Target property*. 
The *Measure* block consists of list of measures and checkbox for enable changing the *Threshold* value.
  
Optionally, you could preprocess your *Source* and *Target* properties using the *Preprocessing function*. For instance, to use *Preprocessing rename function* you should put the *Source property* or *Target property* into this block and then you can connect it to the *Measure*. Using *Complex reprocessing function*, for example "Concat", it is possitble to concatenate the values of two properties and rename the final value. More information about preprocessing functions you can find [here](user_manual/configuration_file/index?id=preprocessing-functions).
  
An *Operator* block accepts two *Measure* blocks as inputs. i.e., two *Measure* blocks must be attached to its input ports.   
The *Operator* block includes the list of operators.  
  
*Optional source property* and *Optional target property* can be connected just after connecting the *Source property* and the *Target property*.  

In general, once you add *Source property*/*Target property*/*Optional source property*/*Optional target property* to the workspace, you can choose the property from the dropdown list. Prefixes of the properties will automatically added to the *Prefixes*.  There is a checkbox *PP* &nbsp;for each property, which defined as Property path. If user checks this *PP*, it means that the mode Property path is enabled and new subproperties will be loaded after some time if they exist. The user can uncheck *PP* &nbsp; and then the property will return to the previous state.
  
You can remove the block using the trash can or using right click on the block.  
On the bottom after the Workspace you can see the formed *Metric*.  
  
At the top before the Workspace there are two options related to Workspace: 
* *Export workspace to xml*: You can download an xml file of the current Workspace with connected blocks. 
* *Select file for the importing to the workspace*: You can upload the xml file of the saved Workspace to change the current Workspace.
  
<img src="./images/manual_metric_web_ui.png" width="800" alt ="Manual_metric_web_ui">

### Machine learning

The tab *Machine learning* consists of three parts:
* *Name*: The machine learning (ML) algorithm name. The currently available ML algorithms include WOMBAT Simple, WOMBAT Complete and EAGLE
* *Type*: The ML algorithm types, which include supervised batch, supervised active and unsupervised
* *Parameters*: A list of parameters which can be added to the currently selected ML algorithm.

If *Type* is supervised batch, you will see the additional input, where you can upload the file with training data.

<img src="./images/ml_web_ui.png" width="800" alt ="ML_web_ui">

If *Type* is supervised active and the user wants to start execution, the *Supervised active ML dialog* window will be shown before actual execution.

<img src="./images/supervised_active_ml_dialog.png" width="800" alt ="supervised_active_ml_dialog">

On this step user have to do some iterations of learning. The user should choose the examples to learn the concept. If you want to choose an example, you need to switch radio button to "*+*". The "*+*" means that the score of the example will be assigned to 1, in case of "*-*" to -1. By default all examples are not chosen, hence all radio buttons are checked as "*-*". The button *Show table* opens more details. When you are done with the iteration, click on *Continue execution* button, it will lead you to the next iteration of learning. After all available iterations of learning, the usual execution will be processed. If you want to skip iteration, click on the button *Skip iteration*, it will assign all scores to 0 and continue with the next iteration.

## Acceptance and review conditions

In this component you can define the *Acceptance Threshold* and the *Review Threshold*. In addition, you can rename the names of files, which can be created after execution. Besides, you can change the *Relation*. Instead of prefix you can write the namespace and its respective URI will be automatically found by the interface, converted to a prefix (if it exists in prefix.cc, otherwise you have to manually add it).
<img src="./images/acc_rev_web_ui.png" width="800" alt ="acc_rev_web_ui">

## Output

Here you can choose an output format, including turtle (TTL), n-triples (N3), tab separated values (TAB), comma separated values (CSV).

### Display config and run
The re are three buttons at the bottom of the page: *Display config*, *Execute*, *Check the state of the previous run*.  
* If you click on the *Display config*, you can look at formed xml config. Also, if you want you can save it.
<img src="./images/config_xml_web_ui.png" width="500" alt ="config_xml_web_ui">  

* The *Execute* button will immediately start the process of executing this xml config. You can have a look at the log messages by clicking on the link *Show log*.

<img src="./images/job_status_web_ui.png" width="400" alt ="job_status_web_ui">  
  
* *Check the state of the previous run* button. The execution can take time. To look at the result later, you must copy the *execution key*.
In order to get the result of the previous run, you should click on *Check the state of the previous run* button. Here you can paste the *execution key*, which you copied when you ran the execution. Then click on *Check* and you will get the result. If the run is finished without errors and result is not empty, then you can download *accepted links* and *reviewed links* files.  
  
<img src="./images/check_run_web_ui.png" width="400" alt ="check_run_web_ui"> 
