# Running the Framework

## From `jar` file
Once the configuration file (dubbed `config.xml` in this manual) has
been written, the last step consists of actually running the LIMES
framework. For this purpose, simply run

`java -jar LIMES.jar config.xml [OPTIONS...]`.

The following optional command line flags and options are available:

* `-f $format` sets the format of configuration file. Possible values for `$format` are`"XML"` (default) or `"RDF"`
* `-g` runs the GUI version
* `-s` runs the LIMES server
* `-p $port` used to specify port of LIMES server, defaults to port 8080
* `-h` prints out a help message
* `-o $file_path` sets the path of the logging file

In case your system runs out of memory, please use the `-Xmx` option (must appear before the -jar option) to
allocate more memory to the Java Virtual Machine.

## From Java
See [Developer manual](https://github.com/AKSW/LIMES-dev/blob/dev/limes-core/manual/developer_manual/index.md)


