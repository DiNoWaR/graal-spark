package com.careem.launcher

import com.careem.model.{Address, User}
import com.careem.util.SparkUtil
import org.apache.spark.sql.{Encoder, Encoders, SQLContext}
import org.apache.spark.sql.execution.streaming.MemoryStream

object LauncherHelper {
  private[launcher] val spark = SparkUtil.getSparkSession()
  private[launcher] val OUTPUT_PATH = "/Users/denisvasilyev/Documents/Projects/Careem/graal-spark/output"

  private[launcher] implicit val context: SQLContext = spark.sqlContext
  private[launcher] implicit val userEncoder: Encoder[User] = Encoders.product[User]
  private[launcher] implicit val addressEncoder: Encoder[Address] = Encoders.product[Address]

  private[launcher] val userSource = MemoryStream[User]
  private[launcher] val addressSource = MemoryStream[Address]
}
