package gg.realmforge.client
import org.webrtc.*
import java.nio.ByteBuffer
import java.util.concurrent.Executor
/** NetherNet data-channel labels + fragment framing. Xbox/Realm signaling is a separate layer. */
class RelayPeer(private val factory:PeerConnectionFactory,private val executor:Executor,private val onState:(String)->Unit,private val onPacket:(ByteArray,Boolean)->Unit={_,_->}){
 private var peer:PeerConnection?=null;private var reliable:DataChannel?=null;private var unreliable:DataChannel?=null
 private val fragments=java.io.ByteArrayOutputStream()
 private fun attach(d:DataChannel){d.registerObserver(object:DataChannel.Observer{
  override fun onBufferedAmountChange(n:Long){}
  override fun onStateChange(){onState(d.label()+": "+d.state())}
  override fun onMessage(b:DataChannel.Buffer){val bytes=ByteArray(b.data.remaining());b.data.get(bytes);if(bytes.isEmpty())return
   val left=bytes[0].toInt() and 255;val part=bytes.copyOfRange(1,bytes.size)
   synchronized(fragments){if(left>0){fragments.write(part);return};if(fragments.size()>0){fragments.write(part);onPacket(fragments.toByteArray(),d.label()=="ReliableDataChannel");fragments.reset()}else onPacket(part,d.label()=="ReliableDataChannel")}
 })}
 }
 fun connect(remoteSdp:String,remoteType:SessionDescription.Type,sendSignal:(SessionDescription)->Unit){executor.execute{
  val cfg=PeerConnection.RTCConfiguration(emptyList()).apply{sdpSemantics=PeerConnection.SdpSemantics.UNIFIED_PLAN;iceTransportsType=PeerConnection.IceTransportsType.ALL}
  peer=factory.createPeerConnection(cfg,object:PeerConnection.Observer{
   override fun onSignalingChange(s:PeerConnection.SignalingState){}
   override fun onIceConnectionChange(s:PeerConnection.IceConnectionState){onState("ICE: "+s.name)}
   override fun onIceConnectionReceivingChange(b:Boolean){}
   override fun onIceGatheringChange(s:PeerConnection.IceGatheringState){}
   override fun onIceCandidate(c:IceCandidate){}
   override fun onIceCandidatesRemoved(c:Array<IceCandidate>){}
   override fun onAddStream(s:MediaStream){}
   override fun onRemoveStream(s:MediaStream){}
   override fun onDataChannel(d:DataChannel){attach(d)}
   override fun onRenegotiationNeeded(){}
   override fun onAddTrack(r:RtpReceiver,s:Array<MediaStream>){}
  })?:error("PeerConnection unavailable")
  reliable=peer!!.createDataChannel("ReliableDataChannel",DataChannel.Init().apply{ordered=true})
  unreliable=peer!!.createDataChannel("UnreliableDataChannel",DataChannel.Init().apply{ordered=false;maxRetransmits=0})
  attach(reliable!!);attach(unreliable!!)
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
  },SessionDescription(remoteType,remoteSdp))
 }}
 fun send(bytes:ByteArray,isReliable:Boolean=true):Boolean{val d=if(isReliable)reliable else unreliable;if(d?.state()!=DataChannel.State.OPEN)return false
  val size=16000;if(bytes.size<=size)return d!!.send(DataChannel.Buffer(ByteBuffer.wrap(byteArrayOf(0)+bytes),true))
  if(!isReliable)return false;val chunks=bytes.asList().chunked(size-1).map{it.toByteArray()}
  for(i in chunks.indices){if(!d!!.send(DataChannel.Buffer(ByteBuffer.wrap(byteArrayOf((chunks.size-i-1).toByte())+chunks[i]),true)))return false};return true
 }
 fun close(){reliable?.close();unreliable?.close();peer?.close();reliable=null;unreliable=null;peer=null}
}
