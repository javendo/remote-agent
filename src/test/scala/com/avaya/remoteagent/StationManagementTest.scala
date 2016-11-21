package com.avaya.remoteagent

import scala.concurrent.duration.DurationInt

import org.scalatest.Finders
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import akka.util.Timeout
import akka.testkit.ImplicitSender
import scala.util.Success

class StationManagementTest extends TestKit(ActorSystem("testsystem"))
    with ImplicitSender
    with WordSpecLike
    with MustMatchers
    with StopSystemAfterAll {
  import StationManagement._
  val station = Station(49009, 123456)
  "A StationManagement Actor" must {
    "return a Success when it receives a Register message" in {
      stationManagement ! Register(station)
      expectMsgClass(10.seconds, classOf[Success[Any]])
    }
  }
  it must {
    "return a Success when it receives an Unegister message" in {
      stationManagement ! Unregister(station)
      expectMsgClass(10.seconds, classOf[Success[Any]])
    }
  }
}
