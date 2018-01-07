package com.sys1yagi

import com.sys1yagi.util.GenesisBlock
import com.sys1yagi.util.TimeProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class NativeChain(val timeProvider: TimeProvider) {
    val logger: Logger = LoggerFactory.getLogger("NativeChain")

    var blockchain = arrayListOf(GenesisBlock)
        private set

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

    fun replaceChain(newBlocks: List<Block>) {
        if (isValidChain(newBlocks) && newBlocks.size > blockchain.size) {
            logger.debug("Received blockchain is valid. Replacing current blockchain with received blockchain")
            blockchain = ArrayList(newBlocks)
        } else {
            logger.debug("Received blockchain invalid")
        }
    }

    fun isValidChain(blockchainToValidate: List<Block>): Boolean {
        if (blockchainToValidate.isEmpty()) {
            logger.debug("blockchainToValidate is empty.")
            return false
        }
        if (blockchainToValidate.first() != GenesisBlock) {
            logger.debug("blockchainToValidate's GenesisBlock is invalid.")
            return false
        }

        blockchainToValidate.zip(blockchainToValidate.drop(1)).forEachIndexed { index, pair ->
            if (!isValidNewBlock(pair.second, pair.first)) {
                logger.debug("Faced invalid block. index=${index+1}")
                return false
            }
        }
        return true
    }

    fun getLatestBlock() = blockchain.last()

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
}
