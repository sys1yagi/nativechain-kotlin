package com.sys1yagi.http

import com.google.gson.Gson
import com.sys1yagi.NativeChain
import com.sys1yagi.util.DefaultTimeProvider
import com.sys1yagi.util.GsonConverter
import com.sys1yagi.websocket.KtorWebSocket
import com.sys1yagi.websocket.NativeChainWebSocketInterfacePool
import com.sys1yagi.websocket.Peer
import com.sys1yagi.websocket.WebSocketChannel
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.experimental.async
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.Duration


fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("NativeChainHttpServer")
    val httpPort = args.getOrNull(0)?.toInt() ?: run {
        logger.error("Should set http port.")
        return
    }
    val webSocketPort = args.getOrNull(1)?.toInt() ?: run {
        logger.error("Should set websocket port.")
        return
    }
    val peers = args.getOrNull(2)?.let {
        listOf(Peer(it))
    } ?: emptyList()

    val nativeChain = NativeChain(DefaultTimeProvider())
    val jsonConverter = GsonConverter(Gson())

    val pool = NativeChainWebSocketInterfacePool(nativeChain, jsonConverter)

    fun connectToPeers(newPeers: List<Peer>) {
        newPeers.forEach { peer ->
            val webSocketChannel = WebSocketChannel(URI.create(peer.host))
            webSocketChannel.connect {
                async {
                    pool.initConnection(webSocketChannel)
                }
            }
        }
    }

    connectToPeers(peers)

    embeddedServer(Netty, webSocketPort) {
        install(DefaultHeaders)
        install(CallLogging)
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
        }
        routing {
            webSocket {
                logger.debug("receive websocket connection")
                val socket = KtorWebSocket(this)
                pool.initConnection(socket)
            }
        }
    }.start(wait = false)

    embeddedServer(Netty, httpPort) {
        routing {
            get("/blocks") {
                val blockchain = jsonConverter.toJson(nativeChain.blockchain)
                call.respondText(blockchain, ContentType.Application.Json)
            }

            post("/mineBlock") {
                val data = call.request.receiveContent().inputStream().bufferedReader().readText()
                val mineBlock = jsonConverter.fromJson(data, MineBlock::class.java)
                nativeChain.addBlock(nativeChain.generateNextBlock(mineBlock.data))
                pool.broadcastLatestMessage()
                call.respond(HttpStatusCode.OK)
            }

            get("/peers") {
                call.respondText(pool.sockets.joinToString(separator = "\n") { it.peer() })
            }

            post("/addPeer") {
                val data = call.request.receiveContent().inputStream().bufferedReader().readText()
                val peer = jsonConverter.fromJson(data, Peer::class.java)
                connectToPeers(listOf(peer))
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}
