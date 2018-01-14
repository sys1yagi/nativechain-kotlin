package com.sys1yagi.nativechain.util

import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Sha256Hash {
    private val logger = LoggerFactory.getLogger("Sha256Hash")
    val mssageDigest = MessageDigest.getInstance("SHA-256")

    fun digest(data: ByteArray): String {
        return mssageDigest
            .digest(data)
            .joinToString(separator = "") { "%02X".format(it) }
    }

    fun digest(data: String): String {
        logger.debug(data)
        return digest(data.toByteArray(StandardCharsets.UTF_8))
    }
}
