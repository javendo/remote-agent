package com.avaya.remoteagent

import akka.testkit.TestKit
import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike
import akka.testkit.ImplicitSender

class MyActorTest extends TestKit(ActorSystem("testsystem"))
    with ImplicitSender
    with WordSpecLike
    with MustMatchers {

  "A simple actor" must {
    // Creation of the TestActorRef
    val actorRef = TestActorRef[MyActor]

    "receive messages" in {
      // This call is synchronous. The actor receive() method will be called in the current thread
      actorRef ! "world"
      expectMsg("Hello world")
    }
  }
}