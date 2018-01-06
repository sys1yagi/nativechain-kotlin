package com.sys1yagi.websocket

interface WebSocketInterface {
    fun send(message: String)
    fun peer(): String
}
