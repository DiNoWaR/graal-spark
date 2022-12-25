package com.careem.launcher

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.careem.launcher.LauncherHelper.{OUTPUT_PATH, addressSource, spark, userSource}
import com.careem.model._
import faker._
import org.apache.spark.sql._
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.apache.spark.sql.functions.{month, to_date}
import org.apache.spark.sql.streaming.Trigger

import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object Launcher {

  def main(args: Array[String]): Unit = {
    val generateActor = createGenerator()
    val generatorSystem = ActorSystem(generateActor, "GeneratorSystem")

    scheduleThreads(List(UserMessage, AddressMessage), 10000, generatorSystem)
    List(userSource, addressSource).foreach(stream => buildAndStartQuery(stream))
    spark.streams.awaitAnyTermination()
  }

  private def createGenerator(): Behavior[StreamingMessage] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case UserMessage =>
          for (_ <- 1 to 100) {
            userSource.addData(User.generateUser())
          }
          Behaviors.same
        case AddressMessage =>
          for (_ <- 1 to 100) {
            addressSource.addData(Address.generateAddress())
          }
          Behaviors.same
        case _ =>
          println(s"Received something i don't know")
          Behaviors.same
      }
    }
  }

  private def scheduleThreads(messages: List[StreamingMessage], interval: Int, actorSystem: ActorSystem[StreamingMessage]): Unit = {
    val scheduler = actorSystem.scheduler
    messages.foreach(message =>
      scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(interval), () => actorSystem ! message, global)
    )
  }

  private def buildAndStartQuery(stream: MemoryStream[_ >: User with Address <: StreamingSource]): Unit = {
    import spark.implicits._
    val queryName = Faker.default.firstName()
    stream.toDF
      .writeStream
      .trigger(Trigger.ProcessingTime("30 seconds"))
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
  }
}

sealed trait StreamingMessage
case object UserMessage extends StreamingMessage
case object AddressMessage extends StreamingMessage