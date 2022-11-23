package com.careem.util

import org.apache.spark.sql.SparkSession

object SparkUtil {

  def getSparkSession(): SparkSession = {
    val spark = SparkSession
      .builder()
      .appName("Spark Graal VM Example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    spark
  }

}
