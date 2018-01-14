package com.sys1yagi.nativechain

import com.sys1yagi.nativechain.util.GenesisBlock
import com.sys1yagi.nativechain.util.Sha256Hash
import com.sys1yagi.nativechain.util.TimeProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class NativeChain(val timeProvider: TimeProvider) {
    val logger: Logger = LoggerFactory.getLogger("NativeChain")

    var blockchain = arrayListOf(GenesisBlock)
        private set

    fun generateNextBlock(transactions: List<Transaction>, script: String): Block {
        val previousBlock = getLatestBlock()
        val nextIndex = previousBlock.index + 1
        val nextTimestamp = timeProvider.nowSecond()
        val nextHash = calculateHash(nextIndex, previousBlock.hash, nextTimestamp, transactions, script)
        return Block(nextIndex, previousBlock.hash, nextTimestamp, transactions, script, nextHash)
    }

    fun isValidNewBlock(newBlock: Block, previousBlock: Block): Boolean {
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
                logger.debug("Faced invalid block. index=${index + 1}")
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
        transactions: List<Transaction>,
        script: String
    ): String {
        return Sha256Hash.digest("$index$previousHash$timestamp$transactions$script")
    }

    private fun calculateHashForBlock(block: Block) = calculateHash(
        block.index,
        block.previousHash,
        block.timestamp,
        block.transactions,
        block.script
    )
}
