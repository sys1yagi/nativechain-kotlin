package com.sys1yagi.nativechain.p2p

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.mock
import com.sys1yagi.nativechain.NativeChain
import com.sys1yagi.nativechain.p2p.connection.ConnectionFactory
import com.sys1yagi.nativechain.util.DefaultTimeProvider
import com.sys1yagi.nativechain.util.GsonConverter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Test

import org.junit.Assert.*

class NativeChainAgentSpec : Spek({

    describe("connect to peer") {
        on("") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val jsonConverter = GsonConverter(Gson())
            val connectionFactory: ConnectionFactory = mock()
            val agent = NativeChainAgent(nativeChain, jsonConverter, connectionFactory)

            it("") {

            }
        }
    }

})