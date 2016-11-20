package com.avaya.remoteagent

import java.util.Properties

import com.avaya.cmapi._
import com.avaya.csta.binding._
import com.avaya.csta.device._
import com.avaya.csta.physical._
import com.avaya.csta.registration.RegistrationServices
import com.avaya.csta.terminal._

import akka.actor.Actor
import ch.ecma.csta.binding._
import ch.ecma.csta.physical._

object StationManagement {
  case class Station(number: Int, password: Int)
  case class MakeCall(deviceId: DeviceID)
  case class Register(station: Station)
  case class Unregister(station: Station)
  case class Disconnect()
}

class StationManagement extends Actor {
  import StationManagement._

  val p = new Properties()
  p.setProperty("cmapi.server_ip", "10.135.32.201")
  p.setProperty("cmapi.username", "avaya")
  p.setProperty("cmapi.password", "avayapassword")
  p.setProperty("cmapi.server_port", "4721")
  p.setProperty("cmapi.secure", "false")
  val serviceProvider = ServiceProvider.getServiceProvider(p)

  def receive = {
    case Register(station) => {
      val deviceId = getDevice(station)
      val regRequest = new RegisterTerminalRequest()
      regRequest.setDevice(deviceId)
      val login = new LoginInfo()
      login.setPassword(station.password.toString())
      login.setSharedControl(false)
      regRequest.setLoginInfo(login)
      regRequest.setLocalMediaInfo(null)
      val registrationServices = serviceProvider.getService(classOf[RegistrationServices].getName).asInstanceOf[RegistrationServices]
      val registerTerminal = registrationServices.registerTerminal(regRequest);
      if (RegistrationConstants.NORMAL_REGISTER == registerTerminal.getCode()) {
        println(s"Device: ${deviceId.getExtension}")
        sender ! deviceId.getExtension
      }
    }
    case MakeCall(deviceId) => {
      val physSvcs = serviceProvider.getService(classOf[PhysicalDeviceServices].getName).asInstanceOf[PhysicalDeviceServices]
      pressButton(ButtonIDConstants.HOLD, deviceId, physSvcs)
      pressButton("1" /*LAMP*/ , deviceId, physSvcs)
      pressButton("1", deviceId, physSvcs)
    }
    case Unregister(station) => {
      val deviceId = getDevice(station)
      val unregisterRequest = new UnregisterTerminalRequest();
      unregisterRequest.setDevice(deviceId);
      val registrationServices = serviceProvider.getService(classOf[RegistrationServices].getName).asInstanceOf[RegistrationServices]
      val registerTerminal = registrationServices.unregisterTerminal(unregisterRequest)
      sender ! deviceId.getExtension
    }
    case Disconnect => {
      serviceProvider.disconnect(true)
    }
  }

  def getDevice(station: Station): DeviceID = {
    val devRequest = new GetDeviceId()
    devRequest.setSwitchIPInterface("10.135.34.4")
    devRequest.setExtension(station.number.toString())
    val deviceServices = serviceProvider.getService(classOf[DeviceServices].getName).asInstanceOf[DeviceServices]
    val devResponse = deviceServices.getDeviceID(devRequest)
    devResponse.getDevice()
  }

  def pressButton(buttonCode: String, id: DeviceID, physSvcs: PhysicalDeviceServices) = {
    val request = new ButtonPress()
    var translatedButton: String = ""
    if ("*".equals(buttonCode)) {
      translatedButton = ButtonIDConstants.DIAL_PAD_STAR;
    } else if ("#".equals(buttonCode)) {
      translatedButton = ButtonIDConstants.DIAL_PAD_POUND;
    } else {
      translatedButton = buttonCode;
    }
    request.setDevice(id);
    request.setButton(translatedButton);
    // Send the request and catch any resulting CstaExceptions.
    physSvcs.pressButton(request)
  }

  def getButtonInformation(id: DeviceID, physSvcs: PhysicalDeviceServices) = {
    val buttonRequest = new GetButtonInformation()
    buttonRequest.setDevice(id)
    val buttonResponse = physSvcs.getButtonInformation(buttonRequest)
    val list = buttonResponse.getButtonList()
    val buttons = list.getButtonItem()
    for (i <- 0 to buttons.length - 1) {
      if (ButtonFunctionConstants.CALL_APPR == buttons(i).getButtonFunction()) {
        //callAppearanceLamps.put(buttons[i].getButton(), LampModeConstants.OFF);
      }
    }
  }

}
