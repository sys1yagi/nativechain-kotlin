package com.sys1yagi.nativechain

data class Block(
    val index: Long,
    val previousHash: String,
    val timestamp: Long,
    val transactions: List<Transaction>,
    val script: String,
    val hash: String
)
