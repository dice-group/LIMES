# Using the LIMES Server

LIMES can be run as a HTTP Server, implementing a RESTful API and serving a browser frontend by default.
Configuration files are accepted via POST multipart/form-data uploads.
Each configuration file gets assigned a unique *job_id*.
Given this job id, the user can query the server for the status of the job, its logs, a list of result files and the contents of these result files.

## API
The following RESTful operations are currently implemented:

* `submit/` **(POST)** ---
  used to upload configuration files as multipart/form-data POST messages and returns the
  assigned *job_id* in a JSON object.
  **Accepts XML Configuration file** (See example below)
* `status/:id`  **(GET)** ---
returns the status (a numerical code) for a given job in a JSON object. 
  The following statuses are currently implemented:
  * **-1 (Unknown)**  - a configuration file for the given *job_id* has not been found on the server 
  * **0 (Scheduled)** - the configuration file is present and the job is waiting for execution
  * **1 (Running)** - the job is currently running
  * **2 (Finished)** - the job is finished and its output files are ready for delivery
* `logs/:id` **(GET)** ---
returns the java logs for the given job. Useful for troubleshooting.
* `results/:id` **(GET)** ---
returns a list of result files in a JSON object.
* `result/:id/:filename`  **(GET)** ---
returns the contents of a given result file for a given job id.
  
  
## Example

```
// Get latest LIMES, dev branch
$ git clone https://github.com/dice-group/LIMES && cd LIMES && git checkout dev 
// Assembly
$ mvn clean package shade:shade -Dmaven.test.skip=true
$ cd limes-core/target
// Run LIMES as HTTP server on port 8080
// You can also run it on port 80 for example by adding -p 80 flag
$ java -jar limes-core-LATEST_RELEASE_VERSION_NUMBER.jar -s
// Get output
15:05:46.078 [main] INFO  org.aksw.limes.core.controller.SimpleServer:44 - Attempting to start LIMES server at port 8080...
15:05:46.088 [main] INFO  org.aksw.limes.core.controller.SimpleServer:56 - Server has been started! Waiting for requests...
// Open new terminal
$ cd PATH_TO_LIMES/LIMES/limes-core/target
// Download example XML mapping
$ wget https://raw.githubusercontent.com/dice-group/LIMES/master/limes-core/resources/lgd-lgd-optional-properties.xml
// Run mapping against endpoint and get job id
$ curl -F config_file=@lgd-lgd-optional-properties.xml  http://localhost:8080/submit
{"requestId":"7538819321022935531","success":true}
// Observe the status
$ curl http://localhost:8080/status/7538819321022935531
{"status":{"code":2,"description":"Request has been processed"},"success":true}
// Get result file list
$ curl http://localhost:8080/results/7538819321022935531
{"availableFiles":["lgd_relaybox_near.nt","lgd_relaybox_verynear.nt"],"success":true}
// Get result
$ curl http://localhost:8080/result/7538819321022935531/lgd_relaybox_verynear.nt
<http://linkedgeodata.org/triplify/node2806760713>	<http://linkedgeodata.org/triplify/node2806760713>	1.0
<http://linkedgeodata.org/triplify/node2806760713>	<http://linkedgeodata.org/triplify/node400957326>	0.9283311463354712
<http://linkedgeodata.org/triplify/node1319713883>	<http://linkedgeodata.org/triplify/node1319713883>	1.0
[...]
// Inspect the logs
$ curl http://localhost:8080/logs/7538819321022935531
2018-06-20T12:08:09,027 [ForkJoinPool.commonPool-worker-2] INFO org.aksw.limes.core.io.cache.HybridCache 111 - Checking for file [...]
```
