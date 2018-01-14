package com.sys1yagi.nativechain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.sys1yagi.nativechain.util.DefaultTimeProvider
import com.sys1yagi.nativechain.util.GenesisBlock
import com.sys1yagi.nativechain.util.TimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object NativeChainSpec : Spek({

    describe("generate block") {
        val timeProvider: TimeProvider = mock()
        whenever(timeProvider.nowSecond()).thenReturn(10L)
        val nativeChain = NativeChain(timeProvider)
        val block = nativeChain.generateNextBlock(emptyList(), "new transactions!")
        it("success") {
            assertThat(block.index).isEqualTo(1)
            assertThat(block.previousHash).isEqualTo(GenesisBlock.hash)
            assertThat(block.timestamp).isEqualTo(10L)
            assertThat(block.transactions).isEmpty()
            assertThat(block.script).isEqualTo("new transactions!")
            assertThat(block.hash).isEqualTo("3C166B4E109009074F62B50F4395A63A3DA6159BB42C72BE713A1574823CAE10")
        }
    }

    describe("block validation") {
        val nativeChain = NativeChain(DefaultTimeProvider)
        val block = nativeChain.generateNextBlock(emptyList(), "new transactions!")
        nativeChain.addBlock(block)
        val nextBlock = nativeChain.generateNextBlock(emptyList(), "new transactions!")

        on("valid transactions") {
            it("return true") {
                assertThat(nativeChain.isValidNewBlock(nextBlock, block)).isTrue()
            }
        }

        on("invalid transactions") {
            val invalidBlock = Block(
                nextBlock.index,
                nextBlock.previousHash,
                nextBlock.timestamp,
                nextBlock.transactions,
                "invalid!",
                nextBlock.hash
            )
            it("return false") {
                assertThat(nativeChain.isValidNewBlock(invalidBlock, block)).isFalse()
            }
        }
    }

    describe("replace chain") {
        on("new chain is valid") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            assertThat(nativeChain.blockchain.size).isEqualTo(1)

            val newBlocks = listOf(
                GenesisBlock,
                nativeChain.generateNextBlock(emptyList(), "new transactions!")
            )

            nativeChain.replaceChain(newBlocks)

            it("replace") {
                assertThat(nativeChain.blockchain.size).isEqualTo(2)
            }
        }

        on("new chain is same size with old chain") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val nextBlock = nativeChain.generateNextBlock(emptyList(), "new transactions!")
            nativeChain.addBlock(nextBlock)
            val newBlocks = listOf(
                GenesisBlock,
                nextBlock
            )

            nativeChain.replaceChain(newBlocks)

            it("skip replace") {
                assertThat(nativeChain.blockchain.size).isEqualTo(2)
            }
        }

        on("new chain is invalid") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val nextBlock = nativeChain.generateNextBlock(emptyList(), "new transactions!")
            nativeChain.addBlock(nextBlock)
            val newBlocks = listOf(
                GenesisBlock,
                nextBlock,
                nextBlock
            )

            nativeChain.replaceChain(newBlocks)

            it("skip replace") {
                assertThat(nativeChain.blockchain.size).isEqualTo(2)
            }
        }
    }
})
