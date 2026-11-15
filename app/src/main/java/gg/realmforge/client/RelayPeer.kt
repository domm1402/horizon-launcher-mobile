package gg.realmforge.client

import org.webrtc.*
import java.nio.ByteBuffer
import java.util.concurrent.Executor

/** WebRTC data channels used by the NetherNet transport layer. */
class RelayPeer(
    private val factory: PeerConnectionFactory,
    private val executor: Executor,
    private val onState: (String) -> Unit,
    private val onPacket: (ByteArray, Boolean) -> Unit = { _, _ -> }
) {
    private var peer: PeerConnection? = null
    private var reliable: DataChannel? = null
    private var unreliable: DataChannel? = null
    private val fragments = java.io.ByteArrayOutputStream()

    private fun attach(channel: DataChannel) {
        channel.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) {}
            override fun onStateChange() {
                onState(channel.label() + ": " + channel.state())
            }
            override fun onMessage(buffer: DataChannel.Buffer) {
                val bytes = ByteArray(buffer.data.remaining())
                buffer.data.get(bytes)
                if (bytes.isEmpty()) return
                val remaining = bytes[0].toInt() and 0xff
                val payload = bytes.copyOfRange(1, bytes.size)
                synchronized(fragments) {
                    if (remaining > 0) {
                        fragments.write(payload)
                        return
                    }
                    if (fragments.size() > 0) {
                        fragments.write(payload)
                        onPacket(fragments.toByteArray(), channel.label() == "ReliableDataChannel")
                        fragments.reset()
                    } else {
                        onPacket(payload, channel.label() == "ReliableDataChannel")
                    }
                }
            }
        })
    }

    fun connect(
        remoteSdp: String,
        remoteType: SessionDescription.Type,
        sendSignal: (SessionDescription) -> Unit
    ) {
        executor.execute {
            val config = PeerConnection.RTCConfiguration(emptyList()).apply {
                sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
                iceTransportsType = PeerConnection.IceTransportsType.ALL
            }
            peer = factory.createPeerConnection(config, object : PeerConnection.Observer {
                override fun onSignalingChange(state: PeerConnection.SignalingState) {}
                override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                    onState("ICE: " + state.name)
                }
                override fun onIceConnectionReceivingChange(receiving: Boolean) {}
                override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
                override fun onIceCandidate(candidate: IceCandidate) {}
                override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {}
                override fun onAddStream(stream: MediaStream) {}
                override fun onRemoveStream(stream: MediaStream) {}
                override fun onDataChannel(channel: DataChannel) { attach(channel) }
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(receiver: RtpReceiver, streams: Array<MediaStream>) {}
            }) ?: throw IllegalStateException("PeerConnection unavailable")

            reliable = peer!!.createDataChannel("ReliableDataChannel", DataChannel.Init().apply {
                ordered = true
            })
            unreliable = peer!!.createDataChannel("UnreliableDataChannel", DataChannel.Init().apply {
                ordered = false
                maxRetransmits = 0
            })
            attach(reliable!!)
            attach(unreliable!!)

            peer!!.setRemoteDescription(object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription) {}
                override fun onSetSuccess() {
                    peer!!.createAnswer(object : SdpObserver {
                        override fun onCreateSuccess(answer: SessionDescription) {
                            peer!!.setLocalDescription(object : SdpObserver {
                                override fun onCreateSuccess(description: SessionDescription) {}
                                override fun onSetSuccess() { sendSignal(answer) }
                                override fun onCreateFailure(error: String) { onState(error) }
                                override fun onSetFailure(error: String) { onState(error) }
                            }, answer)
                        }
                        override fun onSetSuccess() {}
                        override fun onCreateFailure(error: String) { onState(error) }
                        override fun onSetFailure(error: String) { onState(error) }
                    }, MediaConstraints())
                }
                override fun onCreateFailure(error: String) { onState(error) }
                override fun onSetFailure(error: String) { onState(error) }
            }, SessionDescription(remoteType, remoteSdp))
        }
    }

    fun send(bytes: ByteArray, isReliable: Boolean = true): Boolean {
        val channel = if (isReliable) reliable else unreliable
        if (channel?.state() != DataChannel.State.OPEN) return false
        val chunkSize = 16000
        if (bytes.size <= chunkSize) {
            return channel!!.send(DataChannel.Buffer(ByteBuffer.wrap(byteArrayOf(0) + bytes), true))
        }
        if (!isReliable) return false
        val chunks = bytes.asList().chunked(chunkSize - 1).map { it.toByteArray() }
        for (index in chunks.indices) {
            val remaining = (chunks.size - index - 1).toByte()
            if (!channel!!.send(DataChannel.Buffer(ByteBuffer.wrap(byteArrayOf(remaining) + chunks[index]), true))) {
                return false
            }
        }
        return true
    }

    fun close() {
        reliable?.close()
        unreliable?.close()
        peer?.close()
        reliable = null
        unreliable = null
        peer = null
    }
}
