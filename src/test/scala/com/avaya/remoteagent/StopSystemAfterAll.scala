package com.avaya.remoteagent

import org.scalatest.{Suite, BeforeAndAfterAll}
import akka.testkit.TestKit
import akka.testkit.TestActorRef
import com.avaya.remoteagent.StationManagement.Disconnect

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  val stationManagement = TestActorRef[StationManagement]
  override protected def afterAll() {
    stationManagement ! Disconnect
    super.afterAll()
    system.shutdown()
  }
}
