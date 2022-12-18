package com.careem
import com.careem.model._
import com.careem.util.SparkUtil
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SQLContext, SaveMode}
import org.apache.spark.sql.execution.streaming.MemoryStream
import faker._
import org.apache.spark.sql.streaming.Trigger
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.apache.spark.sql.functions.{month, to_date, weekofyear}

import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object Launcher {

  private val spark = SparkUtil.getSparkSession()
  private implicit val context: SQLContext = spark.sqlContext

  private implicit val userEncoder: Encoder[User] = Encoders.product[User]
  private implicit val addressEncoder: Encoder[Address] = Encoders.product[Address]

  private val userSource = MemoryStream[User]
  private val addressSource = MemoryStream[Address]

  private val OUTPUT_PATH = "/Users/denisvasilyev/Documents/Projects/Careem/graal-spark/output"

  def main(args: Array[String]): Unit = {
    import spark.implicits._
    val generateActor = createGenerator()
    val generatorSystem = ActorSystem(generateActor, "GeneratorSystem")
    val scheduler = generatorSystem.scheduler

    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(3500), () => generatorSystem ! UserMessage, global)
    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(2500), () => generatorSystem ! AddressMessage, global)

    val sources = List(userSource, addressSource).par

    sources.foreach(source => {
      val queryName = Faker.default.firstName()
      source.toDF
        .writeStream
        .trigger(Trigger.ProcessingTime("40 seconds"))
        .queryName(queryName)
        .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
          batchDF.withColumn("month", month(to_date($"regDate", "dd/MM/yy mm:ss")))
            .write
            .partitionBy("month")
            .mode(SaveMode.Overwrite)
            .format("delta")
            .save(s"${OUTPUT_PATH}/${queryName}")
        }
        .start
    })
    spark.streams.awaitAnyTermination()
  }

  private def createGenerator(): Behavior[StreamingMessage] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case UserMessage =>
          for (_ <- 1 to 10) {
            userSource.addData(User.generateUser())
          }
          Behaviors.same
        case AddressMessage =>
          for (_ <- 1 to 10) {
            addressSource.addData(Address.generateAddress())
          }
          Behaviors.same
        case _ =>
          println(s"Received something i don't know")
          Behaviors.same
      }
    }
  }
}

sealed trait StreamingMessage
case object UserMessage extends StreamingMessage
case object AddressMessage extends StreamingMessage