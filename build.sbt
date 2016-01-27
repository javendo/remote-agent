name := "cmapi"

version := "1.0"

scalaVersion := "2.10.3"

fork in run := true

connectInput in run := true

javaOptions += "-Djava.net.preferIPv4Stack=true"

resolvers ++= Seq(
   "Avaya Nexus" at "http://nexus.forge.avaya.com/content/groups/public"
)

libraryDependencies ++= Seq(
   "com.avaya" % "cmapi" % "5.2.4.60" exclude ("castor", "castor"),
   "castor" % "castor" % "1.0-xml",
   "xerces" % "xercesImpl" % "2.11.0",
   "commons-logging" % "commons-logging" % "1.2",
   "com.avaya.ph" % "ecsjtapia" % "5.2.0.540",
   "log4j" % "log4j" % "1.2.17"
)

