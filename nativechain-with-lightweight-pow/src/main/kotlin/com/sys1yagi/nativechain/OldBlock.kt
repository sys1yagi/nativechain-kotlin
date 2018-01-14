package com.sys1yagi.nativechain

data class OldBlock(
    val index: Long,
    val previousHash: String,
    val timestamp: Long,
    val data: String,
    val hash: String
)
