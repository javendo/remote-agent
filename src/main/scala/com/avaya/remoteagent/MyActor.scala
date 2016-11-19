package com.avaya.remoteagent

import akka.actor.Actor

class MyActor extends Actor {

  // Sample actor internal state
  var lastMsg: String = ""

  def receive = {
    case msg: String => {
      // Storing the message in the internal state variable
      lastMsg = msg
      sender ! "Hello " + msg
    }
  }
}