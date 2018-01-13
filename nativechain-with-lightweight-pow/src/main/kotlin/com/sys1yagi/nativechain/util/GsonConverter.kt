package com.sys1yagi.nativechain.util

import com.google.gson.Gson

class GsonConverter(private val gson: Gson) : JsonConverter {
    override fun toJson(any: Any): String = gson.toJson(any)

    override fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)
}
