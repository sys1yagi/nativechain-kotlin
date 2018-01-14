package com.sys1yagi.nativechain

data class Block(
    val index: Long,
    val previousHash: String,
    val timestamp: Long,
    val data: List<Transaction>,
    val hash: String
)
