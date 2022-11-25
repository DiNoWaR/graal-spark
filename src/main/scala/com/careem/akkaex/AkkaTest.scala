package com.careem.akkaex

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Duration

object AkkaTest {

  def main(args: Array[String]): Unit = {
    val emotionalMutableActor: Behavior[SimpleThing] = Behaviors.setup { context =>
      var happiness = 0

      Behaviors.receiveMessage {
        case EatChocolate =>
          println(s"($happiness) Eating chocolate, getting a shot of dopamine!")
          happiness += 2
          Behaviors.same
        case WashDishes =>
          if (happiness <= 0) {
            println(s"Sorry, bad Mood for dish. Happinnes is $happiness")
          } else {
            println(s"($happiness) Doing chores, womp, womp...")
            happiness -= 6
          }
          Behaviors.same
        case LearnAkka =>
          println(s"($happiness) Learning Akka, looking good!")
          happiness += 4
          Behaviors.same
        case _ =>
          println(s"($happiness) Received something i don't know")
          Behaviors.same
      }
    }

    val emotionalActorSystem = ActorSystem(emotionalMutableActor, "EmotionalSystem")
    val scheduler = emotionalActorSystem.scheduler

    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(700), () => emotionalActorSystem ! EatChocolate, global)
    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(300), () => emotionalActorSystem ! WashDishes, global)
    scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(2300), () => emotionalActorSystem ! LearnAkka, global)
  }
}

trait SimpleThing

case object EatChocolate extends SimpleThing
case object WashDishes extends SimpleThing
case object LearnAkka extends SimpleThing

