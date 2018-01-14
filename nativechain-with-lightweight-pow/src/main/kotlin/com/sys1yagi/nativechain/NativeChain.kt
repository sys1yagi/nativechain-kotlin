package com.sys1yagi.nativechain

import com.sys1yagi.nativechain.util.GenesisBlock
import com.sys1yagi.nativechain.util.TimeProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class NativeChain(val timeProvider: TimeProvider) {
    val logger: Logger = LoggerFactory.getLogger("NativeChain")

    var blockchain = arrayListOf(GenesisBlock)
        private set

    fun generateNextBlock(blockData: String): OldBlock {
        val previousBlock = getLatestBlock()
        val nextIndex = previousBlock.index + 1
        val nextTimestamp = timeProvider.nowSecond()
        val nextHash = calculateHash(nextIndex, previousBlock.hash, nextTimestamp, blockData)
        return OldBlock(nextIndex, previousBlock.hash, nextTimestamp, blockData, nextHash)
    }

    fun isValidNewBlock(newBlock: OldBlock, previousBlock: OldBlock): Boolean {
        return when {
            previousBlock.index + 1 != newBlock.index -> {
                logger.debug("invalid index")
                false
            }
            previousBlock.hash != newBlock.previousHash -> {
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

    fun addBlock(newBlock: OldBlock) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            logger.debug("add succeed")
            blockchain.add(newBlock)
        } else {
            logger.debug("add failed")
        }
    }

    fun replaceChain(newBlocks: List<OldBlock>) {
        if (isValidChain(newBlocks) && newBlocks.size > blockchain.size) {
            logger.debug("Received blockchain is valid. Replacing current blockchain with received blockchain")
            blockchain = ArrayList(newBlocks)
        } else {
            logger.debug("Received blockchain invalid")
        }
    }

    fun isValidChain(blockchainToValidate: List<OldBlock>): Boolean {
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

    private fun calculateHashForBlock(block: OldBlock) = calculateHash(
        block.index,
        block.previousHash,
        block.timestamp,
        block.data
    )
}
