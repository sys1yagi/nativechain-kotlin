package com.sys1yagi.websocket

import io.ktor.request.uri
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.experimental.channels.sendBlocking

class KtorWebSocket(val socket: WebSocketSession) : WebSocketInterface {

    override fun send(message: String) {
        socket.outgoing.sendBlocking(Frame.Text(message))
    }

    override fun peer() = socket.call.request.uri
}
