package com.avaya.remoteagent

import scala.concurrent.duration.DurationInt

import org.scalatest.Finders
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import akka.util.Timeout

class StationManagementTest extends TestKit(ActorSystem("testsystem"))
    with WordSpecLike
    with MustMatchers
    with StopSystemAfterAll {
  import StationManagement._
  val stationManagement = TestActorRef[StationManagement]
  val station = Station(30000, 123456)
  "A StationManagement Actor" must {
    "return a device when it receives a Register message" in {
      stationManagement ! Register(station)
      expectMsg(10.seconds, station.number.toString())
    }
  }
  "A StationManagement Actor" must {
    "return a device when it receives an Unregister message" in {
      stationManagement ! Unregister(station)
    }
  }
}
