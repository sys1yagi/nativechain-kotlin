package com.sys1yagi.util

interface JsonConverter {
    fun toJson(any: Any): String?
    fun <T> fromJson(json: String, clazz: Class<T>): T?
}
