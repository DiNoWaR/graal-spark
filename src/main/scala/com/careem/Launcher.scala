package com.careem
import com.careem.model._
import com.careem.util.SparkUtil
import org.apache.spark.sql.{Encoder, Encoders, SQLContext}
import org.apache.spark.sql.execution.streaming.MemoryStream
import faker._
import org.apache.spark.sql.streaming.Trigger
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object Launcher {

  private val spark = SparkUtil.getSparkSession()
  private implicit val context: SQLContext = spark.sqlContext

  private implicit val userEncoder: Encoder[User] = Encoders.product[User]
  private implicit val addressEncoder: Encoder[Address] = Encoders.product[Address]

  private val userSource = MemoryStream[User]
  private val addressSource = MemoryStream[Address]

  def main(args: Array[String]): Unit = {

    val generateActor = createDataActor()
    val generatorSystem = ActorSystem(generateActor, "GeneratorSystem")
    val scheduler = generatorSystem.scheduler

    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(3500), () => generatorSystem ! UserMessage, global)
    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(2500), () => generatorSystem ! AddressMessage, global)

    val sources = List(userSource, addressSource).par

    sources.foreach(source =>
      source.toDF
        .writeStream
        .format("console")
        .trigger(Trigger.ProcessingTime("20 seconds"))
        .queryName(Faker.default.firstName())
        .start
    )
    spark.streams.awaitAnyTermination()
  }

  private def createDataActor(): Behavior[StreamingMessage] =
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

sealed trait StreamingMessage
case object UserMessage extends StreamingMessage
case object AddressMessage extends StreamingMessage