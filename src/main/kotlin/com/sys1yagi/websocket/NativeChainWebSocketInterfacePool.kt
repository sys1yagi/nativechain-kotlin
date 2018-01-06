package com.sys1yagi.websocket

import com.sys1yagi.NativeChain
import com.sys1yagi.util.JsonConverter

class NativeChainWebSocketInterfacePool(val nativeChain: NativeChain, val jsonConverter: JsonConverter) {

    private fun buildChainMessage() =
        """
        {
            'type': ${MessageType.RESPONSE_BLOCKCHAIN.ordinal}
            'blockchain': ${jsonConverter.toJson(nativeChain.blockchain)}
        }
        """

    private fun buildLatestMessage() =
        """
        {
            'type': ${MessageType.RESPONSE_BLOCK.ordinal},
            'block': ${jsonConverter.toJson(nativeChain.getLatestBlock())}
        }
        """

    val sockets = arrayListOf<WebSocketInterface>()

    fun initConnection(session: WebSocketInterface) {
        sockets += session
        chainLengthMessage(session)
    }

    fun chainLengthMessage(session: WebSocketInterface) {
        write(session, "{'type': ${MessageType.QUERY_LATEST.ordinal}}")
    }

    fun sendLatestMessage(session: WebSocketInterface) {
        write(session, buildLatestMessage())
    }

    fun sendChainMessage(session: WebSocketInterface) {
        write(session, buildChainMessage())
    }

    fun broadcastLatestMessage() {
        broadcast(buildLatestMessage())
    }

    fun broadcastAllMessage() {
        broadcast("{'type': ${MessageType.QUERY_ALL.ordinal}}")
    }

    private fun write(session: WebSocketInterface, message: String) {
        session.send(message)
    }

    private fun broadcast(message: String) {
        sockets.forEach {
            write(it, message)
        }
    }
}