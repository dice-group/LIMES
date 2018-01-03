# Using the LIMES Server

LIMES can be run as a HTTP Server, accepting configuration files via POST multipart/form-data uploads.
Each configuration file gets assigned a unique *job_id*, which is also the return message from the server.
Once the server finished executing the given job, its results can also be obtained through HTTP.

## API
The following HTTP endpoints are currently implemented:

* `./execute/` **(POST)** ---
  used to upload configuration files as multipart/form-data POST messages and returns the
  assigned *job_id*.  
  **Accepts XML Configuration file** (See example below) 
* `./get_result/?job_id=$job_id&result_type=$result_type` **(GET)** ---
  used to obtain the resulting mapping files (i.e. links) for
  the configuration with *job_id* of `$job_id`.
  **Query Parameters in Detail**  
  * `$job_id` is an identifier returned from the `./execute` endpoint after submitting a job.  
  * `$result_type` specifies which output file from the configuration should be returned. Possible values: *acceptance*
    or *review*  
    *Background:* A configuration file in LIMES allows to specify two output files for two levels of confidence:
    [Acceptance](../configuration_file/acceptance.md) and [Review](../configuration_file/review.md)  
* `./get_status/?job_id=$job_id` **(GET)** ---
  returns the status (a numerical code) for a given job.  
  The following statuses are currently implemented:
  * **-1 (Unknown)**  - a configuration file for the given *job_id* has not been found on the server 
  * **0 (Scheduled)** - the configuration file is present and the job is waiting for execution
  * **1 (Running)** - the job is currently running
  * **2 (Finished)** - the job is finished and its output files are ready for delivery through `./get_result/` requests
    
    
## Example

```
// Get latest LIMES, dev branch
$ git clone https://github.com/AKSW/LIMES-dev && cd LIMES-dev && git checkout dev 
// Assembly
$ mvn clean package shade:shade -Dmaven.test.skip=true
$ cd target
// Run LIMES as HTTP server on port 8080
// The example is for version 1.0.0, the filename may differ
// You can also run it on port 80 for example by adding -p 80 flag
$ java -jar limes-core-1.0.0-SNAPSHOT.jar -s
// Download example XML mapping
$ wget https://raw.githubusercontent.com/AKSW/LIMES-dev/master/limes-core/resources/lgd-lgd.xml
// Run mapping against endpoint
$ curl --form "fileupload=@lgd-lgd.xml" http://localhost:8080/execute
46839272943
// Observe the status
$ curl http://localhost:8080/get_status?job_id=46839272943
1
// Get result file
$ curl http://localhost:8080/get_result/?job_id=46839272943&result_type=acceptance
<http://linkedgeodata.org/triplify/node2806760713>      <http://linkedgeodata.org/triplify/node2806760713>      1.0
<http://linkedgeodata.org/triplify/node2806760713>      <http://linkedgeodata.org/triplify/node400957326>       0.9283311463354712
<http://linkedgeodata.org/triplify/node1319713883>      <http://linkedgeodata.org/triplify/node1319713883>      1.0
<http://linkedgeodata.org/triplify/node385623871>       <http://linkedgeodata.org/triplify/node385623871>       1.0
...
```
