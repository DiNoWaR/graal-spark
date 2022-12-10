ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "graal-spark"
  )

val sparkVersion = "3.2.1"
val akkaVersion = "2.6.6"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}