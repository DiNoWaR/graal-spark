ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "graal-spark"
  )

val sparkVersion = "3.2.2"
val flinkVersion = "1.16.0"
val akkaVersion = "2.7.0"
val icebergVersion = "1.1.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided"
libraryDependencies += "org.apache.flink" % "flink-connector-kafka" % flinkVersion
libraryDependencies += "org.apache.iceberg" % "iceberg-flink" % icebergVersion
libraryDependencies += "org.apache.iceberg" % "iceberg-core" % icebergVersion
libraryDependencies += "io.github.etspaceman" %% "scalacheck-faker" % "7.0.0"
libraryDependencies += "io.delta" %% "delta-core" % "2.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test
libraryDependencies += "org.apache.flink" % "flink-test-utils" % flinkVersion % Test
libraryDependencies += "org.apache.flink" % "flink-runtime" % flinkVersion % Test

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}