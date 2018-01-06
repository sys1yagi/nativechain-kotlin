package com.sys1yagi.websocket

import com.sys1yagi.Block
import com.sys1yagi.NativeChain
import com.sys1yagi.util.JsonConverter
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.consumeEach
import org.slf4j.LoggerFactory

class NativeChainWebSocketInterfacePool(val nativeChain: NativeChain, val jsonConverter: JsonConverter) {

    private val logger = LoggerFactory.getLogger("NativeChainWebSocketInterfacePool")

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

    suspend fun initConnection(session: WebSocketInterface) {
        sockets += session
        chainLengthMessage(session)

        try {
            session.receiveChannel().consumeEach {
                logger.debug("receive peer")
                handleMessage(session, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    fun handleBlockchainResponse(receivedBlocks: List<Block>) {
        val latestBlockReceived = receivedBlocks.last()
        val latestBlockHeld = nativeChain.getLatestBlock()
        if (latestBlockReceived.index > latestBlockHeld.index) {
            logger.debug("blockchain possibly behind. We got: ${latestBlockHeld.index} Peer got: ${latestBlockReceived.index}")
            if (latestBlockHeld.hash === latestBlockReceived.previousHash) {
                logger.debug("We can append the received block to our chain")
                nativeChain.addBlock(latestBlockReceived)
                broadcastLatestMessage()
            } else if (receivedBlocks.size == 1) {
                logger.debug("We have to query the chain from our peer")
                broadcastAllMessage()
            } else {
                logger.debug("Received blockchain is longer than current blockchain")
                nativeChain.replaceChain(receivedBlocks)
                broadcastLatestMessage()
            }
        } else {
            logger.debug("received blockchain is not longer than current blockchain. Do nothing")
        }
    }

    fun handleMessage(from: WebSocketInterface, json: String) {
        logger.debug("receive ${json}")
        val message = jsonConverter.fromJson(json, Message::class.java)
        when (message.messageType()) {
            MessageType.QUERY_LATEST -> {
                sendLatestMessage(from)
            }
            MessageType.QUERY_ALL -> {
                sendChainMessage(from)
            }
            MessageType.RESPONSE_BLOCK -> {
                handleBlockchainResponse(listOf(message.block!!))
            }
            MessageType.RESPONSE_BLOCKCHAIN -> {
                handleBlockchainResponse(message.blockchain)
            }
        }
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