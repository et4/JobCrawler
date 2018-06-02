name := "JobCrawler"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies += "org.json" % "json" % "20180130"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.3.0"

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