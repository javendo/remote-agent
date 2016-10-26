package com.avaya.remoteagent

import org.scalatest._
import akka.testkit._
import akka.actor.ActorSystem

class StationManagementTest extends TestKit(ActorSystem("testsystem"))
    with WordSpecLike
    with MustMatchers
    with StopSystemAfterAll {
  "A StationManagement Actor" must {
    "Return a device when it receives a Register message" in {
      import StationManagement._
      val stationManagement = TestActorRef[StationManagement]
      stationManagement ! Register(Station(30000))
    }
  }
}
