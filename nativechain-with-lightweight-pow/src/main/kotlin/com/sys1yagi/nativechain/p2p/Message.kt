package com.sys1yagi.nativechain.p2p

import com.sys1yagi.nativechain.Block

data class Message(
    private val type: Int,
    val block: Block?,
    val blockchain: List<Block> = emptyList()
) {
    fun messageType() = MessageType.values()[type]
}
