package com.sys1yagi.util

class DefaultTimeProvider : TimeProvider {
    override fun nowSec() = System.currentTimeMillis() / 1000L
}
