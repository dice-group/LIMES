#Using the LIMES Server

LIMES can be run as a HTTP Server, accepting configuration files via POST multipart/form-data uploads.
Each configuration file gets assigned a unique *job_id*, which is also the return message from the server.
Once the server finished executing the given job, its results can also be obtained through HTTP.

## API

There are just two endpoints:

* `./execute/` `(POST)` is used to upload configuration files as multipart/form-data POST messages and returns the assigned job_id.
* `./get_result/?job_id=$job_id&result_type=$result_type` is used to obtain the resulting mapping file of type `$result_type` (either "acceptance" or "verification") for the configuration with job_id of `$job_id`.