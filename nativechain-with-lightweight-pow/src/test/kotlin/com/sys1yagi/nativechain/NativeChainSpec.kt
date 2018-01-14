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
        val block = nativeChain.generateNextBlock("new data!")
        it("success") {
            assertThat(block.index).isEqualTo(1)
            assertThat(block.previousHash).isEqualTo(GenesisBlock.hash)
            assertThat(block.timestamp).isEqualTo(10L)
            assertThat(block.data).isEqualTo("new data!")
            assertThat(block.hash).isEqualTo("655830C8F75597F4BA668B114798C1E71E6437D12C2AEA33F57444E768D1749C")
        }
    }

    describe("block validation") {
        val nativeChain = NativeChain(DefaultTimeProvider)
        val block = nativeChain.generateNextBlock("new data!")
        nativeChain.addBlock(block)
        val nextBlock = nativeChain.generateNextBlock("new data!")

        on("valid data") {
            it("return true") {
                assertThat(nativeChain.isValidNewBlock(nextBlock, block)).isTrue()
            }
        }

        on("invalid data") {
            val invalidBlock = OldBlock(
                nextBlock.index,
                nextBlock.previousHash,
                nextBlock.timestamp,
                nextBlock.data + "a",
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
                nativeChain.generateNextBlock("new data!")
            )

            nativeChain.replaceChain(newBlocks)

            it("replace") {
                assertThat(nativeChain.blockchain.size).isEqualTo(2)
            }
        }

        on("new chain is same size with old chain") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val nextBlock = nativeChain.generateNextBlock("new data!")
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
            val nextBlock = nativeChain.generateNextBlock("new data!")
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
