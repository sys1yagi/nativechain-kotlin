package com.sys1yagi.nativechain.p2p.connection

import io.ktor.request.uri
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.experimental.channels.*

class KtorConnection(val socket: WebSocketSession) : Connection {

    override fun send(message: String) {
        socket.outgoing.sendBlocking(Frame.Text(message))
    }

    override fun receiveChannel(): ReceiveChannel<String> {
        return socket.incoming.map {
            if (it is Frame.Text) {
                it.readText()
            } else {
                null
            }
        }.filterNotNull()
    }

    override fun peer() = socket.call.request.uri
}
