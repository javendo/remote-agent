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
import com.avaya.csta.async.AsynchronousCallback
import com.avaya.csta.async.AsynchronousServices
import akka.actor.ActorRef
import scala.util.Success
import scala.util.Failure

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
      val registerRequest = new RegisterTerminalRequest()
      registerRequest.setDevice(deviceId)
      val login = new LoginInfo()
      login.setPassword(station.password.toString())
      login.setSharedControl(false)
      registerRequest.setLoginInfo(login)
      registerRequest.setLocalMediaInfo(null)
      val asyncSvcs = serviceProvider.getService(classOf[AsynchronousServices].getName).asInstanceOf[AsynchronousServices]
      asyncSvcs.sendRequest(registerRequest, registrationCallbackBuilder(sender))
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
      val asyncSvcs = serviceProvider.getService(classOf[AsynchronousServices].getName).asInstanceOf[AsynchronousServices]
      asyncSvcs.sendRequest(unregisterRequest, registrationCallbackBuilder(sender))
    }
    case Disconnect => {
      serviceProvider.disconnect(true)
    }
  }

  def registrationCallbackBuilder(actor: ActorRef) = new AsynchronousCallback {
    def handleResponse(response: Any): Unit = {
      if (response.isInstanceOf[RegisterTerminalResponse]) {
        if (response.asInstanceOf[RegisterTerminalResponse].getCode == RegistrationConstants.NORMAL_REGISTER) {
          actor ! Success(response.asInstanceOf[RegisterTerminalResponse].getDevice.getDeviceIdentifier.getExtension)
        }
        else {
          actor ! Failure(new Exception(response.asInstanceOf[RegisterTerminalResponse].getReason))
        }
      }
      else if (response.isInstanceOf[UnregisterTerminalResponse]) {
        if (response.asInstanceOf[UnregisterTerminalResponse].getCode == RegistrationConstants.NORMAL_UNREGISTER || response.asInstanceOf[UnregisterTerminalResponse].getCode == RegistrationConstants.CLIENT_REQUESTED_UNREG) {
          actor ! Success(response.asInstanceOf[UnregisterTerminalResponse].getDevice.getDeviceIdentifier.getExtension)
        }
        else {
          actor ! Failure(new Exception(response.asInstanceOf[UnregisterTerminalResponse].getReason))
        }
      }
      else {
          actor ! Failure(new Exception(s"This is bad. Asynchronous response is not of valid type: ${response.toString()}"))
      }
    }
    
    def handleException(exception: Throwable): Unit = {
      actor ! Failure(exception)
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
