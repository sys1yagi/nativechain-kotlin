package com.sys1yagi.nativechain.p2p.connection

import io.ktor.websocket.WebSocketSession
import java.net.URI

interface ConnectionFactory {
    fun createKtorConnection(session: WebSocketSession): KtorConnection
    fun createTyrusConnection(uri: URI): TryusConnection
}