name := "remote-agent"

version := "1.0"

scalaVersion := "2.11.7"

fork in run := true

connectInput in run := true

javaOptions += "-Djava.net.preferIPv4Stack=true"

resolvers ++= Seq(
  "Avaya Nexus" at "http://nexus.forge.avaya.com/content/groups/public",
  "Maven Repo" at "http://repo.maven.apache.org/maven2"
)

libraryDependencies ++= {
  val akkaVersion = "2.4.9"
  Seq(
    "com.avaya" % "cmapi" % "5.2.4.60" exclude ("castor", "castor"),
    "castor" % "castor" % "1.0-xml",
    "xerces" % "xercesImpl" % "2.11.0",
    "commons-logging" % "commons-logging" % "1.2",
    "com.avaya" % "ecsjtapia" % "7.0.0.64",
    "log4j" % "log4j" % "1.2.17",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.0" % "test"
  )
}

