package com.sys1yagi

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.sys1yagi.util.DefaultTimeProvider
import com.sys1yagi.util.TimeProvider
import org.assertj.core.api.Assertions.*
import org.junit.Test

class NativeChainTest {

    @Test
    fun generateNextBlock(){
        val timeProvider: TimeProvider = mock()
        whenever(timeProvider.nowSec()).thenReturn(10L)
        val nativeChain = NativeChain(timeProvider)

        val block = nativeChain.generateNextBlock("new data!")
        assertThat(block.index).isEqualTo(1)
        assertThat(block.previousHash).isEqualTo(NativeChain.GENESIS_BLOCK.hash)
        assertThat(block.timestamp).isEqualTo(10L)
        assertThat(block.data).isEqualTo("new data!")
        assertThat(block.hash).isEqualTo("655830C8F75597F4BA668B114798C1E71E6437D12C2AEA33F57444E768D1749C")
    }

    @Test
    fun isValidNewBlock() {
        val nativeChain = NativeChain(DefaultTimeProvider())
        val block = nativeChain.generateNextBlock("new data!")
        nativeChain.addBlock(block)
        val nextBlock = nativeChain.generateNextBlock("new data!")

        assertThat(nativeChain.isValidNewBlock(nextBlock, block)).isTrue()

        val invalidBlock = Block(
            nextBlock.index,
            nextBlock.previousHash,
            nextBlock.timestamp,
            nextBlock.data + "a",
            nextBlock.hash
        )
        assertThat(nativeChain.isValidNewBlock(invalidBlock, block)).isFalse()
    }
}