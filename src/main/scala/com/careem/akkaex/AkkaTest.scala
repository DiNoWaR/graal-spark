package com.careem.akkaex

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object AkkaTest {

  def main(args: Array[String]): Unit = {
    val emotionalMutableActor: Behavior[SimpleThing] = Behaviors.setup { context =>
      var happiness = 0

      Behaviors.receiveMessage {
        case EatChocolate =>
          println(s"($happiness) Eating chocolate, getting a shot of dopamine!")
          happiness += 1
          Behaviors.same
        case WashDishes =>
          println(s"($happiness) Doing chores, womp, womp...")
          happiness -= 2
          Behaviors.same
        case LearnAkka =>
          println(s"($happiness) Learning Akka, looking good!")
          happiness += 100
          Behaviors.same
        case _ =>
          println(s"($happiness) Received something i don't know")
          Behaviors.same
      }
    }

    val emotionalActorSystem = ActorSystem(emotionalMutableActor, "EmotionalSystem")

    emotionalActorSystem ! EatChocolate
    emotionalActorSystem ! EatChocolate
    emotionalActorSystem ! WashDishes
    emotionalActorSystem ! LearnAkka

    emotionalActorSystem.terminate()
  }
}

trait SimpleThing

case object EatChocolate extends SimpleThing
case object WashDishes extends SimpleThing
case object LearnAkka extends SimpleThing

