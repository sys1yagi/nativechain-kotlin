package com.sys1yagi.nativechain.p2p

import com.sys1yagi.nativechain.OldBlock

data class Message(
    private val type: Int,
    val block: OldBlock?,
    val blockchain: List<OldBlock> = emptyList()
) {
    fun messageType() = MessageType.values()[type]
}
