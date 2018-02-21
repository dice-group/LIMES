# Running the Framework

## From `jar` file
Once the configuration file (dubbed `config.xml` in this manual) has
been written, the last step consists of actually running the LIMES
framework. 
### LIMES core
For this purpose, simply run

`java -jar LIMES.jar config.xml [OPTIONS...]`.

The following optional command line flags and options are available:

* `-f $format` sets the format of configuration file. Possible values for `$format` are`"XML"` (default) or `"RDF"`
* `-s` runs the LIMES server
* `-p $port` used to specify port of LIMES server, defaults to port 8080
* `-l $limit` limits the number of resources processed by LIMES server to `$limit`, defaults to -1 (no limit).
**CAUTION:** Setting this option will compromise the correctness of LIMES and is only encouraged to reduce server load for demo purposes.
* `-h` prints out a help message
* `-o $file_path` sets the path of the logging file

In case your system runs out of memory, please use the `-Xmx` option (must appear before the -jar option) to
allocate more memory to the Java Virtual Machine.

### LIMES GUI
Go to `limes-gui/` and run 
`mvn jfx:jar -Dcheckstyle.skip=true -Dmaven.test.skip=true` .

The jar will be placed in `limes-gui/limes-gui/target/jfx/app/`

The `limes-gui/target/jfx/app/lib` folder needs to be in the same folder as the .jar for the .jar to work!

## From Java
See [Developer manual](/developer_manual/)


