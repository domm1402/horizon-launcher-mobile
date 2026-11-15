package gg.realmforge.client

import org.webrtc.*
import java.util.concurrent.Executor

/**
 * WebRTC transport building block for NetherNet-style relay sessions.
 * NetherNet's authenticated signaling/SDP and Minecraft packet bridge are separate
 * protocol layers; this class deliberately doesn't pretend generic ICE is Realms-ready.
 */
class RelayPeer(private val factory:PeerConnectionFactory,private val executor:Executor,private val onState:(String)->Unit) {
 private var peer:PeerConnection?=null
 private var channel:DataChannel?=null
 private val iceServers= listOf(
  PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
 )
 fun connect(remoteSdp:String,remoteType:SessionDescription.Type,sendSignal:(SessionDescription)->Unit){
  executor.execute{
   val config=PeerConnection.RTCConfiguration(iceServers).apply{
    sdpSemantics=PeerConnection.SdpSemantics.UNIFIED_PLAN
    iceTransportsType=PeerConnection.IceTransportsType.ALL
   }
   peer=factory.createPeerConnection(config,object:PeerConnection.Observer{
    override fun onSignalingChange(s:PeerConnection.SignalingState){}
    override fun onIceConnectionChange(s:PeerConnection.IceConnectionState){onState("ICE: "+s.name)}
    override fun onIceConnectionReceivingChange(b:Boolean){}
    override fun onIceGatheringChange(s:PeerConnection.IceGatheringState){}
    override fun onIceCandidate(c:IceCandidate){/* Forward candidates through authenticated NetherNet signaling. */}
    override fun onIceCandidatesRemoved(c:Array<IceCandidate>){}
    override fun onAddStream(s:MediaStream){}
    override fun onRemoveStream(s:MediaStream){}
    override fun onDataChannel(d:DataChannel){channel=d;onState("Data channel received")}
    override fun onRenegotiationNeeded(){}
    override fun onAddTrack(r:RtpReceiver,s:Array<MediaStream>){}
   })?:error("PeerConnection unavailable")
   channel=peer!!.createDataChannel("nethernet",DataChannel.Init())
   val desc=SessionDescription(remoteType,remoteSdp)
   peer!!.setRemoteDescription(object:SdpObserver{
    override fun onCreateSuccess(s:SessionDescription){}
    override fun onSetSuccess(){peer!!.createAnswer(object:SdpObserver{
     override fun onCreateSuccess(s:SessionDescription){peer!!.setLocalDescription(object:SdpObserver{
      override fun onCreateSuccess(d:SessionDescription){}
      override fun onSetSuccess(){sendSignal(s)}
      override fun onCreateFailure(e:String){onState(e)}
      override fun onSetFailure(e:String){onState(e)}
     },s)}
     override fun onSetSuccess(){}
     override fun onCreateFailure(e:String){onState(e)}
     override fun onSetFailure(e:String){onState(e)}
    },MediaConstraints())}
    override fun onCreateFailure(e:String){onState(e)}
    override fun onSetFailure(e:String){onState(e)}
   },desc)
  }
 }
 fun send(bytes:ByteArray):Boolean=channel?.send(DataChannel.Buffer(java.nio.ByteBuffer.wrap(bytes),true))?:false
 fun close(){channel?.close();peer?.close();channel=null;peer=null}
}
