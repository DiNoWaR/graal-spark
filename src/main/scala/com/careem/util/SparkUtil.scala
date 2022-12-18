package com.careem.util

import org.apache.spark.sql.SparkSession

object SparkUtil {

  def getSparkSession(): SparkSession = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark Graal VM Example")
      .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
      .config("maxRecordsPerFile", "50000)")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    spark
  }
}