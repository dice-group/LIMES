# FAQ

## What is LIMES?

LIMES is a link discovery framework for the Web of Data. It implements time-efficient approaches for large-scale link discovery based on the characteristics of metric spaces. 

## Problems with JavaFX?
While Oracle Java 8 should have JavaFX included, with OpenJDK for Java 8 it depends whether the package assembler chooses to 
include JavaFX. And in [some cases](https://github.com/AKSW/LIMES-dev/issues/56) it can happen, that neither works. 
The first thing you should do is check if you can find `jfxrt.jar`, it *should* be in `<JRE_HOME>/lib/ext/jfxrt.jar` (but in this
case everything should be working just fine). If you find it somewhere else, you have to put it on the classpath. A guide
on how to do this can be found [here](http://askubuntu.com/a/609954). If this does not work, trying to reinstall java would be a good
idea (if you've used OpenJDK before try Oracle Java!).

If this doesn't work, in Ubuntu versions 15.04 upwards you can install JavaFX separately using `sudo apt-get install openjfx`.
Unfortunately, this package seems unavailable for lower Ubuntu versions (Although there is always the possibility to 
[build it yourself](https://wiki.openjdk.java.net/display/OpenJFX/Building+OpenJFX) or use [community builds](https://wiki.openjdk.java.net/display/OpenJFX/Community+Builds)).

From Oracle Java version 11 onwards, JavaFX is not part of the JDK anymore. We are currently working on getting LIMES running in Oracle Java >= 11.
In the meantime, please use an older version to build LIMES. 