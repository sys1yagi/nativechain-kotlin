package com.sys1yagi.websocket

import com.sys1yagi.Block

data class Message(
    private val type: Int,
    val block: Block?,
    val blockchain: List<Block> = emptyList()
) {
    fun messageType() = MessageType.values()[type]
}
