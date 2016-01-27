import javax.telephony._
import javax.telephony.callcenter.AgentTerminal

object Telephone {
  val peer = JtapiPeerFactory.getJtapiPeer("com.avaya.jtapi.tsapi.TsapiPeer")
  val provider = peer.getProvider("AVAYA#APPINVCM48#CSTA#DENPSQAAES6-116;loginID=ctiuser;passwd=Ctiuser1!;servers=135.122.6.116:450")
  def address(number: String) = provider.getAddress(number)
  def terminal(number: String) = provider.getTerminal(number).asInstanceOf[AgentTerminal]
  def agent(id: String, passw: String, address: Address, terminal: AgentTerminal) = terminal.addAgent(address, null, javax.telephony.callcenter.Agent.NOT_READY, id, passw)
}
