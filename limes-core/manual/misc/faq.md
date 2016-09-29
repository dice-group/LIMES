# FAQ
## Problems with JavaFX
While Oracle Java 8 and OpenJDK 8 should have JavaFX included, in [some cases](https://github.com/AKSW/LIMES-dev/issues/56)
it can happen, that you have to install it seperately. In Ubuntu 16.04 (which is the main OS we support) this can be achieved using `sudo apt-get install openjfx`.
Unfortunately this package seems unavailable for lower Ubuntu versions (Although there is always the possibility to 
[build it yourself](https://wiki.openjdk.java.net/display/OpenJFX/Building+OpenJFX) or use [community builds](https://wiki.openjdk.java.net/display/OpenJFX/Community+Builds)).