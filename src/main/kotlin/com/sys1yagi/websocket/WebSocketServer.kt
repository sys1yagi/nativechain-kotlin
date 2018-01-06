package com.sys1yagi.websocket

import java.io.IOException
import javax.websocket.*
import javax.websocket.server.ServerEndpoint

@ServerEndpoint(value = "/")
class WebSocketServer {
    @OnOpen
    @Throws(IOException::class)
    fun onOpen(session: Session, config: EndpointConfig) {
        println("onOpen: " + session)
    }

    @OnClose
    @Throws(IOException::class)
    fun onClose(session: Session, reason: CloseReason) {
        println("onClose: $session, $reason")
    }

    @OnMessage
    fun onMessage(session: Session, message: String) {
        println("onMessage: $session, $message")
        for (s in session.getOpenSessions()) {
            s.getAsyncRemote().sendText(message)
        }
    }

    @OnError
    fun onError(session: Session, t: Throwable) {
        println("onError: $session, $t")
    }
}