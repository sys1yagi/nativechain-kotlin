package com.sys1yagi.nativechain.http

import com.google.gson.Gson
import com.sys1yagi.nativechain.NativeChain
import com.sys1yagi.nativechain.util.DefaultTimeProvider
import com.sys1yagi.nativechain.util.GsonConverter
import com.sys1yagi.nativechain.p2p.NativeChainAgent
import com.sys1yagi.nativechain.p2p.Peer
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("NativeChainHttpServer")
    val httpPort = args.getOrNull(0)?.toInt() ?: run {
        logger.error("Should set http port.")
        return
    }
    val webSocketPort = args.getOrNull(1)?.toInt() ?: run {
        logger.error("Should set p2p port.")
        return
    }
    val peers = args.getOrNull(2)?.let {
        listOf(Peer(it))
    } ?: emptyList()

    val nativeChain = NativeChain(DefaultTimeProvider)
    val jsonConverter = GsonConverter(Gson())
    val webSocketServer = NativeChainAgent(nativeChain, jsonConverter)

    // connect peers
    webSocketServer.connectToPeers(peers)

    // startP2PServer
    webSocketServer.startP2PServer(webSocketPort)

    // start Http server
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
                webSocketServer.broadcastLatestMessage()
                call.respond(HttpStatusCode.OK)
            }

            get("/peers") {
                call.respondText(webSocketServer.sockets().joinToString(separator = "\n") { it.peer() })
            }

            post("/addPeer") {
                val data = call.request.receiveContent().inputStream().bufferedReader().readText()
                val peer = jsonConverter.fromJson(data, Peer::class.java)
                webSocketServer.connectToPeers(listOf(peer))
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}
