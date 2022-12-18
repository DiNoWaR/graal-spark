package com.careem.util

import org.apache.spark.sql.SparkSession

object SparkUtil {

  def getSparkSession(): SparkSession = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark Graal VM Example")
      .getOrCreate()

    spark
  }

}
