name := """Online Resource Readability Evaluation"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

PlayKeys.devSettings := Seq("play.server.http.port" -> "8001")



scalaVersion in ThisBuild := "2.11.0"

libraryDependencies += guice
libraryDependencies += javaJpa
libraryDependencies += "com.h2database" % "h2" % "1.4.194"



// https://mvnrepository.com/artifact/de.julielab/aliasi-lingpipe
libraryDependencies += "de.julielab" % "aliasi-lingpipe" % "4.1.0"

// https://mvnrepository.com/artifact/net.htmlparser.jericho/jericho-html
libraryDependencies += "net.htmlparser.jericho" % "jericho-html" % "3.4"

// https://mvnrepository.com/artifact/org.apache.tika/tika-core
libraryDependencies += "org.apache.tika" % "tika-core" % "1.15"

// https://mvnrepository.com/artifact/org.apache.tika/tika-parsers
libraryDependencies += "org.apache.tika" % "tika-parsers" % "1.15"

// https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.3"

// https://mvnrepository.com/artifact/commons-httpclient/commons-httpclient
libraryDependencies += "commons-httpclient" % "commons-httpclient" % "3.1"

// https://mvnrepository.com/artifact/eu.freme-project.e-services/dbpedia-spotlight
libraryDependencies += "eu.freme-project.e-services" % "dbpedia-spotlight" % "1.1"


PlayKeys.externalizeResources := false

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)
