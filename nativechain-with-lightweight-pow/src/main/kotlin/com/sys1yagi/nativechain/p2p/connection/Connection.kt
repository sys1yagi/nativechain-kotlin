package com.sys1yagi.nativechain.p2p.connection

import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface Connection {
    fun send(message: String)
    fun receiveChannel(): ReceiveChannel<String>
    fun peer(): String
}
