package com.sys1yagi.nativechain.p2p.connection

import io.ktor.websocket.WebSocketSession
import java.net.URI

class KtorAndTyrusConnectionFactory : ConnectionFactory {
    override fun createKtorConnection(session: WebSocketSession): KtorConnection {
        return KtorConnection(session)
    }

    override fun createTyrusConnection(uri: URI): TryusConnection {
        return TryusConnection(uri)
    }

}