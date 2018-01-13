package com.sys1yagi.websocket.`interface`

import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface WebSocketInterface {
    fun send(message: String)
    fun receiveChannel(): ReceiveChannel<String>
    fun peer(): String
}
