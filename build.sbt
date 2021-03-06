name := "bug"

organization := "com.tsunderebug"

version := "0.1"

scalaVersion := "2.12.3"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "jcenter" at "http://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.github.austinv11" % "Discord4J" % "2.9.1",
  "org.postgresql" % "postgresql" % "42.1.4",
  "net.liftweb" %% "lift-json" % "3.2.0-M2",
  "com.sparkjava" % "spark-core" % "2.6.0"
)

mainClass := Some("com.tsunderebug.bug.Main")