# Running the Framework

## From `jar` file
Once the configuration file (dubbed `config.xml` in this manual) has
been written, the last step consists of actually running the LIMES
framework. For this purpose, simply run

`java -jar LIMES.jar config.xml [OPTIONS...]`.

The following options and switches are available:
* `-h` print out help
* `-f $format` set format of configuration file, with `$format` being either `"XML"` (default) or `"RDF"`
* `-g` run GUI version

In case your system runs out of memory, please use the `-Xmx` option (must appear before the -jar option) to
allocate more memory to the Java Virtual Machine. Please ensure that the
Data Type Definition file for LIMES, `limes.dtd`, is in the same folder
as the `LIMES.jar` and everything should run just fine. Enjoy.

## From Java
See [Developer manual](https://github.com/AKSW/LIMES-dev/blob/dev/limes-core/manual/developer_manual/index.md)


