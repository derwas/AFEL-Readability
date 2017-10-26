# Online Resources Readability Evaluator


# Requirements
These are the following requirements:
* Play Framework 2.6.x
* Play requires Java 1.8.
* Play requires sbt

To check the version of your current play application verify the build.bst file

To check that you have the latest JDK, please run:

>$java -version

To check the installed java versions on the machine, please run:

>$ls /usr/lib/jvm/

To change the active java version, please run:

>$export JAVA_HOME=/usr/lib/jvm/[your java version]/

>$export PATH=${JAVA_HOME}/bin:${PATH}


# Compile, Start and Stop the application

### Compile
Compiling the project requires sbt

>$./sbt clean

>$./sbt compile

>$./sbt stage

### Start
The application is play framework app to start it use the following command:

>$cd [location of the app]

>$./target/universal/stage/bin/online-resource-readability-evaluation -Dhttp.port=8001 -Dplay.crypto.secret=afel &> logger.log &

Please note that an accompanied start script is available. It can be used instead of this last command:

>$./start.sh

### Stop
The application runs in the background using port 8001.
Stopping the application can be done by killing the process id using that port.

To stop this application use the following command:

>$sudo netstat -tunlp | grep 8001

>$sudo kill -9 [Process ID]


