name := "JobCrawler"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies += "org.json" % "json" % "20180130"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.3.0"

//libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.5"

//dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.5"
//dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5"
//dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.9.5"
//
//libraryDependencies ++= Seq(
//  "org.json4s" %% "json4s-jackson" % "3.6.0-M3",
//  "org.json4s" %% "json4s-native" % "3.6.0-M3"
//)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.spark" %% "spark-mllib" % "2.3.0",
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.mongodb.spark" %% "mongo-spark-connector" % "2.2.2"
)

//libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
//  "com.typesafe.akka" %% "akka-testkit" % "2.5.11" % Test
//)
//libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
//  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.11" % Test
//)
//libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-http" % "10.1.0",
//  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.0" % Test
//)