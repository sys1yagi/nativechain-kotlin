package com.sys1yagi.nativechain.util

object DefaultTimeProvider : TimeProvider {
    override fun nowSecond() = System.currentTimeMillis() / 1000L
}
