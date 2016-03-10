package com.avaya.remoteagent

import java.util.Properties
import ch.ecma.csta.binding._
import ch.ecma.csta.errors._
import ch.ecma.csta.monitor._
import ch.ecma.csta.voiceunit._
import com.avaya.api.media.audio._
import com.avaya.cmapi._
import com.avaya.csta.async._
import com.avaya.csta.binding._
import com.avaya.csta.binding.types._
import com.avaya.csta.device._
import com.avaya.csta.physical._
import com.avaya.csta.registration._
import com.avaya.csta.tonecollection._
import com.avaya.mvcs.framework._
import akka.actor.Actor

object StationManagement {
  case class Station(number: Int)
  case class Register(station: Station)
  case class Unregister(station: Station)
}

class StationManagement extends Actor {
  import StationManagement._

  def receive = {
    case Register(station) =>
      val p = new Properties()
      p.setProperty("cmapi.server_ip", "135.122.6.116")
      p.setProperty("cmapi.username", "ctiuser")
      p.setProperty("cmapi.password", "Ctiuser1!")
      p.setProperty("cmapi.server_port", "4721")
      p.setProperty("cmapi.secure", "false")
      val serviceProvider = ServiceProvider.getServiceProvider(p)
      val devRequest = new GetDeviceId()
      devRequest.setSwitchIPInterface("135.122.41.48")
      devRequest.setExtension("40004")
      val deviceServices = serviceProvider.getService(classOf[DeviceServices].getName).asInstanceOf[DeviceServices]
      val devResponse = deviceServices.getDeviceID(devRequest)
      val deviceId = devResponse.getDevice()
      val regRequest = new RegisterTerminalRequest();
      regRequest.setDevice(deviceId);
      val login = new LoginInfo();
      login.setPassword("1234");
      login.setSharedControl(false);
      login.setTelecommuterExtension("40018");
      regRequest.setLoginInfo(login);
      regRequest.setLocalMediaInfo(null);
      val registrationServices = serviceProvider.getService(classOf[RegistrationServices].getName).asInstanceOf[RegistrationServices]
      val registerTerminal = registrationServices.registerTerminal(regRequest);
      if (RegistrationConstants.NORMAL_REGISTER.equals(registerTerminal.getCode())) {
	println("registered")
      }
    case Unregister =>
      println("unregistered")
  }
}
