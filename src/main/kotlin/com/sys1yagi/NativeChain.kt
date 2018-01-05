package com.sys1yagi

import com.sys1yagi.util.GenesisBlock
import com.sys1yagi.util.TimeProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class NativeChain(val timeProvider: TimeProvider) {
    val logger: Logger = LoggerFactory.getLogger("NativeChain");

    val blockchain = arrayListOf(GenesisBlock)

    fun generateNextBlock(blockData: String): Block {
        val previousBlock = getLatestBlock()
        val nextIndex = previousBlock.index + 1
        val nextTimestamp = timeProvider.nowSec()
        val nextHash = calculateHash(nextIndex, previousBlock.hash, nextTimestamp, blockData)
        return Block(nextIndex, previousBlock.hash, nextTimestamp, blockData, nextHash)
    }

    fun isValidNewBlock(newBlock: Block, previousBlock: Block): Boolean {
        return when {
            previousBlock.index + 1 != newBlock.index -> {
                logger.debug("invalid index")
                false
            }
            previousBlock.hash !== newBlock.previousHash -> {
                logger.debug("invalid previous hash")
                false
            }
            calculateHashForBlock(newBlock) != newBlock.hash -> {
                logger.debug("invalid hash: ${calculateHashForBlock(newBlock)} ${newBlock.hash}")
                false
            }
            else -> true
        }
    }

    fun addBlock(newBlock: Block) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            logger.debug("add succeed")
            blockchain.add(newBlock)
        } else {
            logger.debug("add failed")
        }
    }

    private fun calculateHash(
        index: Long,
        previousHash: String,
        timestamp: Long,
        data: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest("$index$previousHash$timestamp$data".toByteArray(StandardCharsets.UTF_8))
            .joinToString(separator = "") { "%02X".format(it) }
    }

    private fun calculateHashForBlock(block: Block) = calculateHash(
        block.index,
        block.previousHash,
        block.timestamp,
        block.data
    )

    private fun getLatestBlock() = blockchain.last()
}