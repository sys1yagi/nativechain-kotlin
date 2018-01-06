package com.sys1yagi.websocket

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.sendBlocking
import org.glassfish.tyrus.client.ClientManager
import org.slf4j.LoggerFactory
import java.net.URI
import javax.websocket.*


class WebSocketChannel(val uri: URI) : WebSocketInterface {
    private val logger = LoggerFactory.getLogger("WebSocketChannel")
    private val channel = Channel<String>()
    private var session: Session? = null

    fun connect(onOpen: (Channel<String>) -> Unit) {
        logger.debug("connect ${uri.host}:${uri.port}")
        val config = ClientEndpointConfig.Builder.create().build()
        val client = ClientManager.createClient()
        client.connectToServer(
            object : Endpoint() {
                override fun onOpen(session: Session, config: EndpointConfig?) {
                    logger.debug("onOpen ${uri.scheme}://${uri.host}")
                    this@WebSocketChannel.session = session
                    onOpen(channel)
                    session.addMessageHandler(MessageHandler.Whole<String> { message ->
                        channel.sendBlocking(message)
                    })
                }

                override fun onClose(session: Session?, closeReason: CloseReason?) {
                    logger.debug("onClose ${uri.host} ${closeReason}")
                    super.onClose(session, closeReason)
                    channel.close()
                }

                override fun onError(session: Session?, thr: Throwable?) {
                    logger.debug("onError ${uri.host}")
                    thr?.printStackTrace()
                    channel.close()
                }
            },
            config,
            uri
        )
    }

    override fun send(message: String) {
        session?.asyncRemote?.sendText(message)
    }

    override fun peer() = uri.toString()
}
